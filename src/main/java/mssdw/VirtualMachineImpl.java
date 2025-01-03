/*
 * Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package mssdw;

import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import mssdw.connect.spi.Connection;
import mssdw.event.EventQueue;
import mssdw.protocol.VirtualMachine_FindDebugOffset;
import mssdw.protocol.VirtualMachine_FindType;
import mssdw.protocol.VirtualMachine_GetVersion;
import mssdw.request.EventRequestManager;

public class VirtualMachineImpl extends MirrorImpl implements VirtualMachine
{
	final int sequenceNumber;

	private final TargetVM target;
	private final EventQueueImpl eventQueue;
	private final EventRequestManagerImpl eventRequestManager;
	final VirtualMachineManagerImpl vmManager;
	private final ThreadGroup threadGroupForJDI;

	// Allow direct access to this field so that that tracing code slows down
	// JDI as little as possible when not enabled.
	public int traceFlags = 0;

	static int TRACE_RAW_SENDS = 0x01000000;
	static int TRACE_RAW_RECEIVES = 0x02000000;

	public boolean traceReceives = false;   // pre-compute because of frequency

	// These are cached once for the life of the VM
	private final VirtualMachine_GetVersion myVersionInfo;

	// Launched debuggee process
	private Process process;

	// coordinates state changes and corresponding listener notifications
	private VMState state = new VMState(this);

	private final Object initMonitor = new Object();
	private boolean initComplete = false;

	VirtualMachineImpl(VirtualMachineManager manager, Connection connection, Process process, int sequenceNumber)
	{
		super(null);  // Can't use super(this)
		vm = this;

		this.vmManager = (VirtualMachineManagerImpl) manager;
		this.process = process;
		this.sequenceNumber = sequenceNumber;

        /* Create ThreadGroup to be used by all threads servicing
		 * this VM.
         */
		threadGroupForJDI = new ThreadGroup(vmManager.mainGroupForJDI(), "Mono Soft Debugger [" + this.hashCode() + "]");

        /*
		 * Set up a thread to communicate with the target VM over
         * the specified transport.
         */
		target = new TargetVM(this, connection);

        /*
         * Initialize client access to event setting and handling
         */
		eventQueue = new EventQueueImpl(this, target);
		eventRequestManager = new EventRequestManagerImpl(this);

		target.start();

        /*
         * Tell other threads, notably TargetVM, that initialization
         * is complete.
         */
		notifyInitCompletion();

		try
		{
			myVersionInfo = VirtualMachine_GetVersion.process(vm);

			if(myVersionInfo.majorVersion != vmManager.majorInterfaceVersion())
			{
				throw new IllegalArgumentException("Virtual Machine major version is not equal client: " + myVersionInfo.majorVersion);
			}
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	private void notifyInitCompletion()
	{
		synchronized(initMonitor)
		{
			initComplete = true;
			initMonitor.notifyAll();
		}
	}

	void waitInitCompletion()
	{
		synchronized(initMonitor)
		{
			while(!initComplete)
			{
				try
				{
					initMonitor.wait();
				}
				catch(InterruptedException e)
				{
					// ignore
				}
			}
		}
	}

	VMState state()
	{
		return state;
	}

	@Nullable
	@Override
	public TypeMirror findTypeByQualifiedName(@Nullable String vmQName)
	{
		TypeRef type = null;
		try
		{
			type = VirtualMachine_FindType.process(this, vmQName).type;
			if(type == null)
			{
				return null;
			}
			return new TypeMirror(this, type);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Nullable
	@Override
	public DebugInformationResult findDebugOffset(@Nonnull String path, int line, int column)
	{
		try
		{
			return VirtualMachine_FindDebugOffset.process(this, path, line, column).result;
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Nonnull
	@Override
	public List<ThreadMirror> allThreads()
	{
		return state.allThreads();
	}

	/*
	 * Sends a command to the back end which is defined to do an
	 * implicit vm-wide resume. The VM can no longer be considered
	 * suspended, so certain cached data must be invalidated.
	 */
	PacketStream sendResumingCommand(CommandSender sender)
	{
		return state.thawCommand(sender);
	}

	/*
	 * The VM has been suspended. Additional caching can be done
	 * as long as there are no pending resumes.
	 */
	void notifySuspend()
	{
		state.freeze();
	}

	@Override
	public void suspend()
	{
		try
		{
			JDWP.VirtualMachine.Suspend.process(vm);
		}
		catch(JDWPException exc)
		{
			throw exc.asUncheckedException();
		}
		notifySuspend();
	}

	@Override
	public void resume()
	{
		CommandSender sender = new CommandSender()
		{
			@Override
			public PacketStream send()
			{
				return JDWP.VirtualMachine.Resume.enqueueCommand(vm);
			}
		};
		try
		{
			PacketStream stream = state.thawCommand(sender);
			JDWP.VirtualMachine.Resume.waitForReply(vm, stream);
		}
		catch(VMDisconnectedException exc)
		{
            /*
             * If the debugger makes a VMDeathRequest with SUSPEND_ALL,
             * then when it does an EventSet.resume after getting the
             * VMDeathEvent, the normal flow of events is that the
             * BE shuts down, but the waitForReply comes back ok.  In this
             * case, the run loop in TargetVM that is waiting for a packet
             * gets an EOF because the socket closes. It generates a
             * VMDisconnectedEvent and everyone is happy.
             * However, sometimes, the BE gets shutdown before this
             * waitForReply completes.  In this case, TargetVM.waitForReply
             * gets awakened with no reply and so gens a VMDisconnectedException
             * which is not what we want.  It might be possible to fix this
             * in the BE, but it is ok to just ignore the VMDisconnectedException
             * here.  This will allow the VMDisconnectedEvent to be generated
             * correctly.  And, if the debugger should happen to make another
             * request, it will get a VMDisconnectedException at that time.
             */
		}
		catch(JDWPException exc)
		{
			throw exc.asUncheckedException();
		}
	}

	@Nonnull
	@Override
	public EventQueue eventQueue()
	{
        /*
         * No VM validation here. We allow access to the event queue
         * after disconnection, so that there is access to the terminating
         * events.
         */
		return eventQueue;
	}

	@Nonnull
	@Override
	public EventRequestManager eventRequestManager()
	{
		return eventRequestManager;
	}

	EventRequestManagerImpl eventRequestManagerImpl()
	{
		return eventRequestManager;
	}

	@Override
	public void dispose()
	{
		try
		{
			JDWP.VirtualMachine.Dispose.process(vm);
		}
		catch(JDWPException exc)
		{
			throw exc.asUncheckedException();
		}
		target.stopListening();
	}

	@Override
	public void exit(int exitCode)
	{
		try
		{
			JDWP.VirtualMachine.Exit.process(vm, exitCode);
		}
		catch(JDWPException exc)
		{
			throw exc.asUncheckedException();
		}
		target.stopListening();
	}

	@Nonnull
	@Override
	public Process process()
	{
		return process;
	}

	@Override
	public boolean isAtLeastVersion(int major, int minor)
	{
		return (myVersionInfo.majorVersion > major) || ((myVersionInfo.majorVersion == major && myVersionInfo.minorVersion >= minor));
	}

	@Override
	public void enableEvents(@Nonnull EventKind... eventKinds)
	{
		enableEvents(SuspendPolicy.ALL, eventKinds);
	}

	@Override
	public void enableEvents(@Nonnull SuspendPolicy policy, @Nonnull EventKind... eventKinds)
	{
		for(EventKind eventKind : eventKinds)
		{
			try
			{
				JDWP.EventRequest.Set.process(vm, (byte) eventKind.ordinal(), policy.ordinal(), new JDWP.EventRequest.Set.Modifier[0]);
			}
			catch(JDWPException e)
			{
				throw e.asUncheckedException();
			}
		}
	}

	@Nonnull
	@Override
	public String version()
	{
		return myVersionInfo.majorVersion + "." + myVersionInfo.minorVersion;
	}

	@Nonnull
	@Override
	public String name()
	{
		return myVersionInfo.description;
	}

	public void checkVersion(int major, int minor)
	{
		if(!isAtLeastVersion(major, minor))
		{
			throw new VersionMismatchException();
		}
	}

	public void printTrace(String string)
	{
		System.err.println("[MDI: " + string + "]");
	}

	public void printReceiveTrace(int depth, String string)
	{
		StringBuilder sb = new StringBuilder("Receiving:");
		for(int i = depth; i > 0; --i)
		{
			sb.append("    ");
		}
		sb.append(string);
		printTrace(sb.toString());
	}

	void sendToTarget(Packet packet)
	{
		target.send(packet);
	}

	void waitForTargetReply(Packet packet)
	{
		target.waitForReply(packet);
	}

	ThreadGroup threadGroupForJDI()
	{
		return threadGroupForJDI;
	}
}

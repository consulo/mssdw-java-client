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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import mssdw.request.*;

import javax.annotation.Nullable;

/**
 * This interface is used to create and remove Breakpoints, Watchpoints,
 * etc.
 * It include implementations of all the request interfaces..
 */
// Warnings from List filters and List[] requestLists is  hard to fix.
// Remove SuppressWarning when we fix the warnings from List filters
// and List[] requestLists. The generic array is not supported.
@SuppressWarnings("unchecked")
public class EventRequestManagerImpl extends MirrorImpl implements EventRequestManager
{
	private Map<EventKind, List<EventRequest>> myEventRequests = new HashMap<EventKind, List<EventRequest>>();

	/**
	 * Constructor.
	 */
	EventRequestManagerImpl(VirtualMachine vm)
	{
		super(vm);

		for(EventKind eventKind : EventKind.values())
		{
			myEventRequests.put(eventKind, new ArrayList<EventRequest>());
		}
	}

	@Nonnull
	@Override
	public ExceptionRequest createExceptionRequest(@Nullable TypeMirror refType, boolean notifyCaught, boolean notifyUncaught, boolean notifyOnSubclasses)
	{
		validateMirrorOrNull(refType);
		return add(new ExceptionRequest(refType, notifyCaught, notifyUncaught, notifyOnSubclasses, vm, this));
	}

	@Nonnull
	@Override
	public StepRequest createStepRequest(ThreadMirror thread, StepRequest.StepDepth depth)
	{
		validateMirror(thread);
		return add(new StepRequest(thread, depth, vm, this));
	}

	@Override
	public ThreadDeathRequest createThreadDeathRequest()
	{
		return add(new ThreadDeathRequest(vm, this));
	}

	@Nonnull
	@Override
	public EventRequest createModuleLoadRequest()
	{
		return add(new ModuleLoadRequest(vm, this));
	}

	@Nonnull
	@Override
	public EventRequest createModuleUnloadRequest()
	{
		return add(new ModuleUnloadRequest(vm, this));
	}

	@Override
	public ThreadStartRequest createThreadStartRequest()
	{
		return add(new ThreadStartRequest(vm, this));
	}

	@Nonnull
	@Override
	public MethodEntryRequest createMethodEntryRequest()
	{
		return add(new MethodEntryRequest(vm, this));
	}

	@Nonnull
	@Override
	public MethodExitRequest createMethodExitRequest()
	{
		return add(new MethodExitRequest(vm, this));
	}

	@Nonnull
	@Override
	public BreakpointRequest createBreakpointRequest(@Nonnull DebugInformationResult debugInformationResult)
	{
		return add(new BreakpointRequest(vm, this, debugInformationResult));
	}

	@Override
	public VMDeathRequest createVMDeathRequest()
	{
		return add(new VMDeathRequest(vm, this));
	}

	@Override
	public void deleteEventRequest(EventRequest eventRequest)
	{
		validateMirror(eventRequest);
		eventRequest.delete();
	}

	@Override
	public void deleteEventRequests(Collection<? extends EventRequest> eventRequests)
	{
		validateMirrors(eventRequests);
		for(EventRequest eventRequest : eventRequests)
		{
			eventRequest.delete();
		}
	}

	@Override
	public void deleteAllBreakpoints()
	{
		requestList(EventKind.BREAKPOINT).clear();

		try
		{
			JDWP.EventRequest.ClearAllBreakpoints.process(vm);
		}
		catch(JDWPException exc)
		{
			throw exc.asUncheckedException();
		}
	}

	@Override
	public List<StepRequest> stepRequests()
	{
		return (List<StepRequest>) unmodifiableRequestList(EventKind.STEP);
	}

	@Override
	public List<ThreadStartRequest> threadStartRequests()
	{
		return (List<ThreadStartRequest>) unmodifiableRequestList(EventKind.THREAD_START);
	}

	@Override
	public List<ThreadDeathRequest> threadDeathRequests()
	{
		return (List<ThreadDeathRequest>) unmodifiableRequestList(EventKind.THREAD_DEATH);
	}

	@Override
	public List<ExceptionRequest> exceptionRequests()
	{
		return (List<ExceptionRequest>) unmodifiableRequestList(EventKind.EXCEPTION);
	}

	@Override
	public List<BreakpointRequest> breakpointRequests()
	{
		return (List<BreakpointRequest>) unmodifiableRequestList(EventKind.BREAKPOINT);
	}

	@Override
	public List<MethodEntryRequest> methodEntryRequests()
	{
		return (List<MethodEntryRequest>) unmodifiableRequestList(EventKind.METHOD_ENTRY);
	}

	@Override
	public List<MethodExitRequest> methodExitRequests()
	{
		return (List<MethodExitRequest>) unmodifiableRequestList(EventKind.METHOD_EXIT);
	}

	@Nonnull
	@Override
	public List<EventRequest> moduleLoadEventRequests()
	{
		return (List<EventRequest>) unmodifiableRequestList(EventKind.MODULE_LOAD);
	}

	@Nonnull
	@Override
	public List<EventRequest> moduleUnloadEventRequests()
	{
		return (List<EventRequest>) unmodifiableRequestList(EventKind.MODULE_UNLOAD);
	}

	@Override
	public List<VMDeathRequest> vmDeathRequests()
	{
		return (List<VMDeathRequest>) unmodifiableRequestList(EventKind.VM_DEATH);
	}

	List<? extends EventRequest> unmodifiableRequestList(EventKind eventCmd)
	{
		return Collections.unmodifiableList(requestList(eventCmd));
	}

	EventRequest request(EventKind eventCmd, int requestId)
	{
		List<? extends EventRequest> rl = requestList(eventCmd);
		for(int i = rl.size() - 1; i >= 0; i--)
		{
			EventRequest er = rl.get(i);
			if(er.id() == requestId)
			{
				return er;
			}
		}
		return null;
	}

	public <T extends EventRequest> T add(T t)
	{
		myEventRequests.get(t.eventCmd()).add(t);
		return t;
	}

	public List<? extends EventRequest> requestList(EventKind eventCmd)
	{
		return myEventRequests.get(eventCmd);
	}
}

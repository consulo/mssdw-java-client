/*
 * Copyright (c) 1998, 2001, Oracle and/or its affiliates. All rights reserved.
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

package mssdw.request;

import java.util.Iterator;
import java.util.List;

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.JDWP;
import mssdw.ThreadMirror;
import mssdw.VirtualMachineImpl;

/**
 * Request for notification when a step occurs in the target VM.
 * When an enabled StepRequest is satisfied, an
 * {@link mssdw.event.EventSet event set} containing a
 * {@link mssdw.event.StepEvent StepEvent} will be placed on the
 * {@link mssdw.event.EventQueue EventQueue}.
 * The collection of existing StepRequests is
 * managed by the {@link EventRequestManager}
 *
 * @author Robert Field
 * @see mssdw.event.StepEvent
 * @see mssdw.event.EventQueue
 * @see EventRequestManager
 * @since 1.3
 */
public class StepRequest extends TypeVisibleEventRequest
{
	public static enum StepDepth
	{
		Into,
		Over,
		Out
	}

	private ThreadMirror thread;
	private StepDepth depth;

	public StepRequest(ThreadMirror thread, StepDepth depth, VirtualMachineImpl vm, EventRequestManagerImpl requestManager)
	{
		super(vm, requestManager);
		this.thread = thread;
		this.depth = depth;

            /*
			 * Make sure this isn't a duplicate
             */
		List<StepRequest> requests = requestManager.stepRequests();
		Iterator<StepRequest> iter = requests.iterator();
		while(iter.hasNext())
		{
			StepRequest request = iter.next();
			if((request != this) &&
					request.isEnabled() &&
					request.thread().equals(thread))
			{
				throw new DuplicateRequestException("Only one step request allowed per thread");
			}
		}

		filters.add(JDWP.EventRequest.Set.Modifier.Step.create(this.thread, depth));
	}

	public StepDepth depth()
	{
		return depth;
	}

	public ThreadMirror thread()
	{
		return thread;
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.STEP;
	}

	@Override
	public String toString()
	{
		return "step request " + thread() + state();
	}
}

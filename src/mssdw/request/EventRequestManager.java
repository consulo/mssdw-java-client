/*
 * Copyright (c) 1998, 2005, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mssdw.DebugInformationResult;
import mssdw.Mirror;
import mssdw.ThreadMirror;
import mssdw.TypeMirror;
import mssdw.VirtualMachine;

/**
 * Manages the creation and deletion of {@link EventRequest}s. A single
 * implementor of this interface exists in a particuar VM and
 * is accessed through {@link VirtualMachine#eventRequestManager()}
 *
 * @author Robert Field
 * @see EventRequest
 * @see mssdw.event.Event
 * @see BreakpointRequest
 * @see mssdw.event.BreakpointEvent
 * @see VirtualMachine
 * @since 1.3
 */

public interface EventRequestManager extends Mirror
{
	@NotNull
	EventRequest createModuleLoadRequest();

	@NotNull
	EventRequest createModuleUnloadRequest();


	/**
	 * Creates a new disabled {@link ThreadStartRequest}.
	 * The new event request is added to the list managed by this
	 * EventRequestManager. Use {@link EventRequest#enable()} to
	 * activate this event request.
	 *
	 * @return the created {@link ThreadStartRequest}
	 */
	ThreadStartRequest createThreadStartRequest();

	/**
	 * Creates a new disabled {@link ThreadDeathRequest}.
	 * The new event request is added to the list managed by this
	 * EventRequestManager. Use {@link EventRequest#enable()} to
	 * activate this event request.
	 *
	 * @return the created {@link ThreadDeathRequest}
	 */
	ThreadDeathRequest createThreadDeathRequest();

	/**
	 * Creates a new disabled {@link ExceptionRequest}.
	 * The new event request is added to the list managed by this
	 * EventRequestManager. Use {@link EventRequest#enable()} to
	 * activate this event request.
	 *
	 * @param refType        If non-null, specifies that exceptions which are
	 *                       instances of refType will be reported. If null,
	 *                       all instances will be reported
	 * @param notifyCaught   If true, caught exceptions will be reported.
	 * @param notifyUncaught If true, uncaught exceptions will be reported.
	 * @return the created {@link ExceptionRequest}
	 */
	@NotNull
	ExceptionRequest createExceptionRequest(@Nullable TypeMirror refType, boolean notifyCaught, boolean notifyUncaught, boolean notifyOnSubclasses);

	/**
	 * Creates a new disabled {@link MethodEntryRequest}.
	 * The new event request is added to the list managed by this
	 * EventRequestManager. Use {@link EventRequest#enable()} to
	 * activate this event request.
	 *
	 * @return the created {@link MethodEntryRequest}
	 */
	@NotNull
	MethodEntryRequest createMethodEntryRequest();

	/**
	 * Creates a new disabled {@link MethodExitRequest}.
	 * The new event request is added to the list managed by this
	 * EventRequestManager. Use {@link EventRequest#enable()} to
	 * activate this event request.
	 *
	 * @return the created {@link MethodExitRequest}
	 */
	@NotNull
	MethodExitRequest createMethodExitRequest();

	@NotNull
	StepRequest createStepRequest(ThreadMirror thread, StepRequest.StepDepth depth);

	@NotNull
	BreakpointRequest createBreakpointRequest(@NotNull DebugInformationResult result);

	/**
	 * Creates a new disabled {@link VMDeathRequest}.
	 * The new request is added to the list managed by this
	 * EventRequestManager.
	 * Use {@link EventRequest#enable()} to
	 * activate this event request.
	 * <p/>
	 * This request (if enabled) will cause a
	 * {@link mssdw.event.VMDeathEvent}
	 * to be sent on termination of the target VM.
	 * <p/>
	 * A VMDeathRequest with a suspend policy of
	 * {@link EventRequest#SUSPEND_ALL SUSPEND_ALL}
	 * can be used to assure processing of incoming
	 * {@link EventRequest#SUSPEND_NONE SUSPEND_NONE} or
	 * {@link EventRequest#SUSPEND_EVENT_THREAD SUSPEND_EVENT_THREAD}
	 * events before VM death.  If all event processing is being
	 * done in the same thread as event sets are being read,
	 * enabling the request is all that is needed since the VM
	 * will be suspended until the {@link mssdw.event.EventSet}
	 * containing the {@link mssdw.event.VMDeathEvent}
	 * is resumed.
	 * <p/>
	 * Not all target virtual machines support this operation.
	 * Use {@link VirtualMachine#canRequestVMDeathEvent()}
	 * to determine if the operation is supported.
	 *
	 * @return the created request
	 * @throws java.lang.UnsupportedOperationException
	 *          if
	 *          the target VM does not support this
	 *          operation.
	 * @since 1.4
	 */
	VMDeathRequest createVMDeathRequest();

	/**
	 * Removes an eventRequest. The eventRequest is disabled and
	 * the removed from the requests managed by this
	 * EventRequestManager. Once the eventRequest is deleted, no
	 * operations (for example, {@link EventRequest#setEnabled})
	 * are permitted - attempts to do so will generally cause an
	 * {@link InvalidRequestStateException}.
	 * No other eventRequests are effected.
	 * <p/>
	 * Because this method changes the underlying lists of event
	 * requests, attempting to directly delete from a list returned
	 * by a request accessor (e.g. below):
	 * <PRE>
	 * Iterator iter = requestManager.stepRequests().iterator();
	 * while (iter.hasNext()) {
	 * requestManager.deleteEventRequest(iter.next());
	 * }
	 * </PRE>
	 * may cause a {@link java.util.ConcurrentModificationException}.
	 * Instead use
	 * {@link #deleteEventRequests(List) deleteEventRequests(List)}
	 * or copy the list before iterating.
	 *
	 * @param eventRequest the eventRequest to remove
	 */
	void deleteEventRequest(EventRequest eventRequest);

	/**
	 * Removes a list of {@link EventRequest}s.
	 *
	 * @param eventRequests the list of eventRequests to remove
	 * @see #deleteEventRequest(EventRequest)
	 */
	void deleteEventRequests(Collection<? extends EventRequest> eventRequests);

	/**
	 * Remove all breakpoints managed by this EventRequestManager.
	 *
	 * @see #deleteEventRequest(EventRequest)
	 */
	void deleteAllBreakpoints();

	/**
	 * Return an unmodifiable list of the enabled and disabled step requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the all {@link StepRequest} objects.
	 */
	List<StepRequest> stepRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled thread start requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the all {@link ThreadStartRequest} objects.
	 */
	List<ThreadStartRequest> threadStartRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled thread death requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the all {@link ThreadDeathRequest} objects.
	 */
	List<ThreadDeathRequest> threadDeathRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled exception requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the all {@link ExceptionRequest} objects.
	 */
	List<ExceptionRequest> exceptionRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled breakpoint requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the list of all {@link BreakpointRequest} objects.
	 */
	List<BreakpointRequest> breakpointRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled method entry requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the list of all {@link MethodEntryRequest} objects.
	 */
	List<MethodEntryRequest> methodEntryRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled method exit requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 *
	 * @return the list of all {@link MethodExitRequest} objects.
	 */
	List<MethodExitRequest> methodExitRequests();

	/**
	 * Return an unmodifiable list of the enabled and disabled VM death requests.
	 * This list is a live view of these requests and thus changes as requests
	 * are added and deleted.
	 * Note: the unsolicited VMDeathEvent does not have a
	 * corresponding request.
	 *
	 * @return the list of all {@link VMDeathRequest} objects.
	 * @since 1.4
	 */
	List<VMDeathRequest> vmDeathRequests();

	@NotNull
	List<EventRequest> moduleLoadEventRequests();

	@NotNull
	List<EventRequest> moduleUnloadEventRequests();
}

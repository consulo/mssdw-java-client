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

package mssdw.event;

import java.util.Iterator;
import java.util.Set;

import jakarta.annotation.Nonnull;
import mssdw.Mirror;
import mssdw.SuspendPolicy;
import mssdw.ThreadMirror;
import mssdw.VirtualMachine;

/**
 * Several {@link Event} objects may be created at a given time by
 * the target {@link VirtualMachine}. For example, there may be
 * more than one {@link mssdw.request.BreakpointRequest}
 * for a given {@link Location}
 * or you might single step to the same location as a
 * BreakpointRequest.  These {@link Event} objects are delivered
 * together as an EventSet.  For uniformity, an EventSet is always used
 * to deliver {@link Event} objects.  EventSets are delivered by
 * the {@link EventQueue}.
 * EventSets are unmodifiable.
 * <p/>
 * Associated with the issuance of an event set, suspensions may
 * have occurred in the target VM.  These suspensions correspond
 * with the {@link #suspendPolicy() suspend policy}.
 * To assure matching resumes occur, it is recommended,
 * where possible,
 * to complete the processing of an event set with
 * {@link #resume() EventSet.resume()}.
 * <p/>
 * The events that are grouped in an EventSet are restricted in the
 * following ways:
 * <p/>
 * <UL>
 * <LI>Always singleton sets:
 * <UL>
 * <LI>{@link VMStartEvent}
 * <LI>{@link VMDisconnectEvent}
 * </UL>
 * <LI>Only with other VMDeathEvents:
 * <UL>
 * <LI>{@link VMDeathEvent}
 * </UL>
 * <LI>Only with other ThreadStartEvents for the same thread:
 * <UL>
 * <LI>{@link ThreadStartEvent}
 * </UL>
 * <LI>Only with other ThreadDeathEvents for the same thread:
 * <UL>
 * <LI>{@link ThreadDeathEvent}
 * </UL>
 * <LI>Only with other ClassPrepareEvents for the same class:
 * <UL>
 * <LI>{@link ClassPrepareEvent}
 * </UL>
 * <LI>Only with other ClassUnloadEvents for the same class:
 * <UL>
 * <LI>{@link ClassUnloadEvent}
 * </UL>
 * <LI>Only with other AccessWatchpointEvents for the same field access:
 * <UL>
 * <LI>{@link AccessWatchpointEvent}
 * </UL>
 * <LI>Only with other ModificationWatchpointEvents for the same field
 * modification:
 * <UL>
 * <LI>{@link ModificationWatchpointEvent}
 * </UL>
 * <LI>Only with other ExceptionEvents for the same exception occurrance:
 * <UL>
 * <LI>{@link ExceptionEvent}
 * </UL>
 * <LI>Only with other MethodExitEvents for the same method exit:
 * <UL>
 * <LI>{@link MethodExitEvent}
 * </UL>
 * <LI>Only with other Monitor contended enter events for the same monitor object:
 * <UL>
 * <LI>Monitor Contended Enter Event
 * </UL>
 * <LI>Only with other Monitor contended entered events for the same monitor object:
 * <UL>
 * <LI>Monitor Contended Entered Event
 * </UL>
 * <LI>Only with other Monitor wait events for the same monitor object:
 * <UL>
 * <LI>Monitor Wait Event
 * </UL>
 * <LI>Only with other Monitor waited events for the same monitor object:
 * <UL>
 * <LI>Monitor Waited Event
 * </UL>
 * <LI>Only with other members of this group, at the same location
 * and in the same thread:
 * <UL>
 * <LI>{@link BreakpointEvent}
 * <LI>{@link StepEvent}
 * <LI>{@link MethodEntryEvent}
 * </UL>
 * </UL>
 *
 * @author Robert Field
 * @see Event
 * @see EventQueue
 * @since 1.3
 */

public interface EventSet extends Mirror, Set<Event>
{
	/**
	 * Returns the policy used to suspend threads in the target VM
	 * for this event set. This policy is selected from the suspend
	 * policies for each event's request; the target VM chooses the
	 * policy which suspends the most threads.  The target VM
	 * suspends threads according to that policy
	 * and that policy is returned here. See
	 * {@link mssdw.request.EventRequest} for the possible
	 * policy values.
	 * <p/>
	 * In rare cases, the suspend policy may differ from the requested
	 * value if a {@link ClassPrepareEvent} has occurred in a
	 * debugger system thread. See {@link ClassPrepareEvent#thread}
	 * for details.
	 *
	 * @return the suspendPolicy which is either
	 * {@link mssdw.request.EventRequest#SUSPEND_ALL SUSPEND_ALL},
	 * {@link mssdw.request.EventRequest#SUSPEND_EVENT_THREAD SUSPEND_EVENT_THREAD} or
	 * {@link mssdw.request.EventRequest#SUSPEND_NONE SUSPEND_NONE}.
	 */
	@Nonnull
	SuspendPolicy suspendPolicy();

	/**
	 * Return an iterator specific to {@link Event} objects.
	 */
	Iterator<Event> eventIterator();

	ThreadMirror eventThread();
}

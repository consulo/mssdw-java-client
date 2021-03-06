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

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.VirtualMachine;

/**
 * Request for notification when a thread terminates in the target VM.
 * When an enabled ThreadDeathRequest is satisfied, an
 * {@link mssdw.event.EventSet event set} containing a
 * {@link mssdw.event.ThreadDeathEvent ThreadDeathEvent}
 * will be placed on the
 * {@link mssdw.event.EventQueue EventQueue}.
 * The collection of existing ThreadDeathRequests is
 * managed by the {@link EventRequestManager}
 *
 * @see mssdw.event.ThreadDeathEvent
 * @see mssdw.event.EventQueue
 * @see EventRequestManager
 *
 * @author Robert Field
 * @since  1.3
 */
public class ThreadDeathRequest extends ThreadVisibleEventRequest
{
	public ThreadDeathRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager)
	{
		super(virtualMachine, requestManager);
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.THREAD_DEATH;
	}

	@Override
	public String toString()
	{
		return "thread death request " + state();
	}
}
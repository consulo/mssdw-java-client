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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.JDWP;
import mssdw.TypeMirror;
import mssdw.VirtualMachineImpl;

/**
 * Request for notification when an exception occurs in the target VM.
 * When an enabled ExceptionRequest is satisfied, an
 * {@link mssdw.event.EventSet event set} containing an
 * {@link mssdw.event.ExceptionEvent ExceptionEvent} will be placed
 * on the {@link mssdw.event.EventQueue EventQueue}.
 * The collection of existing ExceptionRequests is
 * managed by the {@link EventRequestManager}
 *
 * @author Robert Field
 * @see mssdw.event.ExceptionEvent
 * @see mssdw.event.EventQueue
 * @see EventRequestManager
 * @since 1.3
 */
public class ExceptionRequest extends TypeVisibleEventRequest
{
	private TypeMirror exception = null;
	private boolean caught = true;
	private boolean uncaught = true;
	private boolean myNotifyOnSubclasses;

	public ExceptionRequest(@Nullable TypeMirror refType, boolean notifyCaught, boolean notifyUncaught, boolean notifyOnSubclasses, VirtualMachineImpl vm, EventRequestManagerImpl requestManager)
	{
		super(vm, requestManager);
		exception = refType;
		this.caught = notifyCaught;
		this.uncaught = notifyUncaught;
		myNotifyOnSubclasses = notifyOnSubclasses;
		filters.add(0, JDWP.EventRequest.Set.Modifier.ExceptionOnly.create(refType, notifyCaught, notifyUncaught, notifyOnSubclasses));
	}

	@Nonnull
	public TypeMirror exception()
	{
		return exception;
	}

	public boolean notifyCaught()
	{
		return caught;
	}

	public boolean notifyUncaught()
	{
		return uncaught;
	}

	public boolean notifyOnSubclasses()
	{
		return myNotifyOnSubclasses;
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.EXCEPTION;
	}

	@Override
	public String toString()
	{
		return "exception request " + exception() + state();
	}
}
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
import mssdw.JDWP;
import mssdw.Location;
import mssdw.VirtualMachine;

/**
 * Identifies a {@link Location} in the target VM at which
 * execution should be stopped. When an enabled BreakpointRequest is
 * satisfied, an
 * {@link mssdw.event.EventSet event set} containing an
 * {@link mssdw.event.BreakpointEvent BreakpointEvent}
 * will be placed on the
 * {@link mssdw.event.EventQueue EventQueue} and
 * the application is interrupted. The collection of existing breakpoints is
 * managed by the {@link EventRequestManager}
 *
 * @see Location
 * @see mssdw.event.BreakpointEvent
 * @see mssdw.event.EventQueue
 * @see EventRequestManager
 *
 * @author Robert Field
 * @since  1.3
 */
public class BreakpointRequest extends TypeVisibleEventRequest
{
	private String myPath;
	private int myLine;
	private int myColumn;

	public BreakpointRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager, String path, int line, int column)
	{
		super(virtualMachine, requestManager);
		myPath = path;
		myLine = line;
		myColumn = column;
		filters.add(0, JDWP.EventRequest.Set.Modifier.BreakpointLocation.create(path, line, column));
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.BREAKPOINT;
	}

	@Override
	public String toString()
	{
		return "breakpoint request at " + myPath + ":" + myLine + ":" + myColumn + " " + state();
	}
}
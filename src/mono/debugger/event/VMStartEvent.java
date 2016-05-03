/*
 * Copyright (c) 1998, 1999, Oracle and/or its affiliates. All rights reserved.
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

package mono.debugger.event;

import mono.debugger.JDWP;
import mono.debugger.VirtualMachine;

/**
 * Notification of initialization of a target VM.  This event is
 * received before the main thread is started and before any
 * application code has been executed. Before this event occurs
 * a significant amount of system code has executed and a number
 * of system classes have been loaded.
 * This event is always generated by the target VM, even
 * if not explicitly requested.
 *
 * @see VMDeathEvent
 * @see EventQueue
 * @see VirtualMachine
 *
 * @author Robert Field
 * @since  1.3
 */
public class VMStartEvent extends ThreadedEvent
{
	public VMStartEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.VMStart evt)
	{
		super(virtualMachine, evt, evt.requestID, evt.thread);
	}

	@Override
	public String eventName()
	{
		return "VMStartEvent";
	}
}

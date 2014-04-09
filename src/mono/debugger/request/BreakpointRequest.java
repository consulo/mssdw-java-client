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

package mono.debugger.request;

import mono.debugger.*;

/**
 * Identifies a {@link Location} in the target VM at which
 * execution should be stopped. When an enabled BreakpointRequest is
 * satisfied, an
 * {@link mono.debugger.event.EventSet event set} containing an
 * {@link mono.debugger.event.BreakpointEvent BreakpointEvent}
 * will be placed on the
 * {@link mono.debugger.event.EventQueue EventQueue} and
 * the application is interrupted. The collection of existing breakpoints is
 * managed by the {@link EventRequestManager}
 *
 * @see Location
 * @see mono.debugger.event.BreakpointEvent
 * @see mono.debugger.event.EventQueue
 * @see EventRequestManager
 *
 * @author Robert Field
 * @since  1.3
 */
public interface BreakpointRequest extends EventRequest, Locatable {

    /**
     * Returns the location of the requested breakpoint.
     *
     * @return the {@link Location} where this breakpoint has been set.
     */
	@Override
	Location location();

    /**
     * Restricts the events generated by this request to those in
     * the given thread.
     * @param thread the thread to filter on.
     * @throws InvalidRequestStateException if this request is currently
     * enabled or has been deleted.
     * Filters may be added only to disabled requests.
     */
    void addThreadFilter(ThreadMirror thread);

    /**
     * Restricts the events generated by this request to those in
     * which the currently executing instance is the object
     * specified.
     * <P>
     * Not all targets support this operation.
     * Use {@link VirtualMachine#canUseInstanceFilters()}
     * to determine if the operation is supported.
     * @since 1.4
     * @param instance the object which must be the current instance
     * in order to pass this filter.
     * @throws java.lang.UnsupportedOperationException if
     * the target virtual machine does not support this
     * operation.
     * @throws InvalidRequestStateException if this request is currently
     * enabled or has been deleted.
     * Filters may be added only to disabled requests.
     */
    void addInstanceFilter(ObjectReference instance);
}

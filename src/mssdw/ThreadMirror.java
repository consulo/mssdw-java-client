/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
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
import java.util.List;

import org.jetbrains.annotations.NotNull;
import mssdw.protocol.Thread_GetFrameInfo;
import mssdw.protocol.Thread_GetName;
import mssdw.protocol.Thread_GetState;

public class ThreadMirror extends MirrorWithIdAndName
{
	public static interface ThreadState
	{
		int Running = 0;
		int Suspended = 1;
	}

	ThreadMirror(VirtualMachine aVm, int aRef)
	{
		super(aVm, aRef);
	}

	@NotNull
	@Override
	public String nameImpl() throws JDWPException
	{
		String threadName = Thread_GetName.process(vm, this).threadName;
		if(threadName.length() == 0)
		{
			return "";
		}
		return threadName;
	}

	private Thread_GetState status()
	{
		try
		{
			return Thread_GetState.process(vm, this);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	public boolean isRunning()
	{
		return status().debugState == ThreadState.Running;
	}

	public boolean isSuspended()
	{
		return status().debugState == ThreadState.Suspended;
	}

	@NotNull
	public List<StackFrameMirror> frames()
	{
		try
		{
			Thread_GetFrameInfo.Frame[] frames = Thread_GetFrameInfo.process(vm, this).frames;
			List<StackFrameMirror> frameMirrors = new ArrayList<StackFrameMirror>(frames.length);
			for(Thread_GetFrameInfo.Frame frame : frames)
			{

				StackFrameMirror frameMirror = new StackFrameMirror(vm, this, frame.frameID, frame.FilePath, frame.Line, frame.Column, frame.TypeRef, frame.FunctionToken);
				frameMirrors.add(frameMirror);
			}
			return frameMirrors;

		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}
}

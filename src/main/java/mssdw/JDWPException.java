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

import jakarta.annotation.Nonnull;

public class JDWPException extends Exception
{
	private static final long serialVersionUID = -6321344442751299874L;
	short errorCode;

	public JDWPException(short errorCode)
	{
		super();
		this.errorCode = errorCode;
	}

	short errorCode()
	{
		return errorCode;
	}

	@Nonnull
	public RuntimeException asUncheckedException()
	{
		switch(errorCode)
		{
			case JDWP.Error.NOT_SUSPENDED:
				return new NotSuspendedException();
			case JDWP.Error.INVALID_OBJECT:
				return new InvalidObjectException();
			case JDWP.Error.INVALID_FRAMEID:
				return new InvalidStackFrameException();
			case JDWP.Error.INVALID_FIELDID:
				return new InvalidFieldIdException();
			case JDWP.Error.NOT_IMPLEMENTED:
				return new UnsupportedOperationException();
			case JDWP.Error.ABSENT_INFORMATION:
				return new AbsentInformationException();
			case JDWP.Error.INVALID_ARGUMENT:
				return new IllegalArgumentException();
			case JDWP.Error.ERR_UNLOADED:
				return new UnloadedElementException();
			default:
				return new InternalException("Unexpected JDWP Error: " + errorCode, errorCode);
		}
	}
}

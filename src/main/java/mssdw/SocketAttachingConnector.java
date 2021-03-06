/*
 * Copyright (c) 1998, 2004, Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import mssdw.connect.Connector;
import mssdw.connect.IllegalConnectorArgumentsException;
import mssdw.connect.Transport;

/*
 * An AttachingConnector that uses the SocketTransportService
 */
public class SocketAttachingConnector extends GenericAttachingConnector
{
	public static final String ARG_PORT = "port";
	public static final String ARG_HOST = "hostname";

	public SocketAttachingConnector()
	{
		super(new SocketTransportService());

		String defaultHostName;
		try
		{
			defaultHostName = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException e)
		{
			defaultHostName = "";
		}

		addStringArgument(ARG_HOST, defaultHostName, false);

		addIntegerArgument(ARG_PORT, "", true, 0, Integer.MAX_VALUE);

		transport = new Transport()
		{
			@Override
			public String name()
			{
				return "dt_socket";     // for compatability reasons
			}
		};

	}

	/*
	 * Create an "address" from the hostname and port connector
	 * arguments and attach to the target VM.
	 */
	@Override
	public VirtualMachine attach(Map<String, ? extends Connector.Argument> arguments) throws IOException, IllegalConnectorArgumentsException
	{
		String host = argument(ARG_HOST, arguments).value();
		if(host.length() > 0)
		{
			host = host + ":";
		}
		String address = host + argument(ARG_PORT, arguments).value();
		return super.attach(address, arguments);
	}

	@Override
	public String name()
	{
		return "mono.debugger.SocketAttach";
	}
}

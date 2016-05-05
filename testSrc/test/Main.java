package test;

import java.util.List;
import java.util.Map;

import mssdw.LocationImpl;
import mssdw.MethodMirror;
import mssdw.SocketListeningConnector;
import mssdw.StackFrameMirror;
import mssdw.SuspendPolicy;
import mssdw.TypeMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;
import mssdw.connect.Connector;
import mssdw.event.EventSet;
import mssdw.protocol.Method_GetDebugInfo;
import mssdw.request.BreakpointRequest;
import mssdw.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 07.04.14
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		SocketListeningConnector socketListeningConnector = new SocketListeningConnector();

		Map<String, Connector.Argument> argumentMap = socketListeningConnector.defaultArguments();

		argumentMap.get(SocketListeningConnector.ARG_LOCALADDR).setValue("127.0.0.1");
		argumentMap.get(SocketListeningConnector.ARG_PORT).setValue("10110");

		VirtualMachineImpl accept = (VirtualMachineImpl) socketListeningConnector.accept(argumentMap);

		Thread.sleep(1000L);

		accept.resume();
		accept.suspend();

		TypeMirror typeMirror = accept.findTypesByQualifiedName("Program", true)[0];

		int index = 0;
		MethodMirror m = null;
		l:
		for(MethodMirror methodMirror : typeMirror.methods())
		{
			if("Main".equals(methodMirror.name()))
			{
				for(Method_GetDebugInfo.Entry entry : methodMirror.debugInfo())
				{
					if(entry.line == 54)
					{
						m = methodMirror;
						index = entry.offset;
						break l;
					}
				}
			}
		}

		EventRequestManager eventRequestManager = accept.eventRequestManager();


		BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(new LocationImpl(accept, m, index));
		breakpointRequest.enable();
		accept.resume();

		while(true)
		{
			EventSet eventSet = accept.eventQueue().remove();
			if(eventSet.suspendPolicy() == SuspendPolicy.ALL)
			{
				List<StackFrameMirror> frames = eventSet.eventThread().frames();

				for(StackFrameMirror frame : frames)
				{
					System.out.println("frame: " + frame.location().method());
					Value value = frame.thisObject();

					TypeMirror type = value.type();

					if(type == null)
					{
						continue;
					}

					MethodMirror toString = typeMirror.findMethodByName("ToString", true);

					assert toString != null;

					Value<?> invoke = toString.invoke(frame.thread(), value);

					System.out.println("tst: " + invoke.value());
				}
			}


			Thread.sleep(100L);
		}
	}
}

package test;

import java.util.List;
import java.util.Map;

import mssdw.EventKind;
import mssdw.MethodMirror;
import mssdw.SocketAttachingConnector;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.VirtualMachineImpl;
import mssdw.connect.Connector;
import mssdw.event.BreakpointEvent;
import mssdw.event.Event;
import mssdw.event.EventSet;
import mssdw.event.ModuleLoadEvent;
import mssdw.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 07.04.14
 */
public class Main
{
	public static void main(String[] args) throws Exception
	{
		SocketAttachingConnector socketListeningConnector = new SocketAttachingConnector();

		Map<String, Connector.Argument> argumentMap = socketListeningConnector.defaultArguments();

		argumentMap.get(SocketAttachingConnector.ARG_HOST).setValue("127.0.0.1");
		argumentMap.get(SocketAttachingConnector.ARG_PORT).setValue("12345");

		VirtualMachineImpl virtualMachine = (VirtualMachineImpl) socketListeningConnector.attach(argumentMap);

		Thread.sleep(1000L);

		virtualMachine.enableEvents(EventKind.MODULE_LOAD);

		EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();

		virtualMachine.resume();

		while(true)
		{
			EventSet eventSet = virtualMachine.eventQueue().remove();

			Event next = eventSet.iterator().next();
			if(next instanceof ModuleLoadEvent)
			{
				String path = ((ModuleLoadEvent) next).getPath();
				if(path.endsWith("TestApplication.exe"))
				{
					eventRequestManager.createBreakpointRequest("R:\\_github.com\\consulo\\mssdw\\TestApplication\\Program.cs", 7, -1).enable();
				}
				virtualMachine.resume();

			}
			else if(next instanceof BreakpointEvent)
			{
				List<ThreadMirror> threadMirrors = virtualMachine.allThreads();
				for(ThreadMirror threadMirror : threadMirrors)
				{
					List<StackFrameMirror> frames = threadMirror.frames();
					System.out.println("thread: " + threadMirror.id() + ", " + frames.size());
					for(StackFrameMirror frame : frames)
					{
						MethodMirror method = frame.getMethod();

						System.out.println("> " + method.name() + " " + frame.getFilePath());
					}
				}
				System.out.println("test");
			}

			Thread.sleep(100);
		}
	}
}

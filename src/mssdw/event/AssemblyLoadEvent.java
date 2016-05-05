package mssdw.event;

import org.jetbrains.annotations.NotNull;
import mssdw.AssemblyMirror;
import mssdw.JDWP;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 08.04.14
 */
public class AssemblyLoadEvent extends ThreadedEvent implements Event
{
	private final AssemblyMirror myAssembly;

	public AssemblyLoadEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.AssemblyLoad evt)
	{
		super(virtualMachine, evt, evt.requestID, evt.thread);

		myAssembly = evt.assembly;
	}

	@Override
	public String eventName()
	{
		return "AssemblyLoadEvent";
	}

	@NotNull
	public AssemblyMirror getAssembly()
	{
		return myAssembly;
	}
}

package mssdw.event;

import org.jetbrains.annotations.NotNull;
import mssdw.AssemblyMirror;
import mssdw.JDWP;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 08.04.14
 */
public class AssemblyUnloadEvent extends ThreadedEvent implements Event
{
	private final AssemblyMirror myAssembly;

	public AssemblyUnloadEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.AssemblyUnLoad evt)
	{
		super(virtualMachine, evt, evt.requestID, evt.thread);

		myAssembly = evt.assembly;
	}

	@Override
	public String eventName()
	{
		return "AssemblyUnloadEvent";
	}

	@NotNull
	public AssemblyMirror getAssembly()
	{
		return myAssembly;
	}
}

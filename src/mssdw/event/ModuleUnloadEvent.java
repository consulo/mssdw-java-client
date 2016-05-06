package mssdw.event;

import mssdw.EventSetImpl;
import mssdw.JDWP;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 14.04.14
 */
public class ModuleUnloadEvent extends EventSetImpl.EventImpl  implements Event
{
	private String myPath;

	public ModuleUnloadEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.ModuleUnload evt)
	{
		super(virtualMachine, evt, evt.requestID);
		myPath = evt.path;
	}

	public String getPath()
	{
		return myPath;
	}

	@Override
	public String eventName()
	{
		return "ModuleUnloadEvent";
	}
}

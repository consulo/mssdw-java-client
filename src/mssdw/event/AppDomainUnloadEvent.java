package mssdw.event;

import org.jetbrains.annotations.NotNull;
import mssdw.AppDomainMirror;
import mssdw.JDWP;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 14.04.14
 */
public class AppDomainUnloadEvent extends ThreadedEvent implements Event
{
	private AppDomainMirror myAppDomainMirror;

	public AppDomainUnloadEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.AppDomainUnload evt)
	{
		super(virtualMachine, evt, evt.requestID, evt.thread);
		myAppDomainMirror = evt.appDomainMirror;
	}

	@NotNull
	public AppDomainMirror getAppDomainMirror()
	{
		return myAppDomainMirror;
	}

	@Override
	public String eventName()
	{
		return "AppDomainUnloadEvent";
	}
}

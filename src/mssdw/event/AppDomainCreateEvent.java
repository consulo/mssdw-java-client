package mssdw.event;

import org.jetbrains.annotations.NotNull;
import mssdw.AppDomainMirror;
import mssdw.JDWP;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 14.04.14
 */
public class AppDomainCreateEvent extends ThreadedEvent implements Event
{
	private AppDomainMirror myAppDomainMirror;

	public AppDomainCreateEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.AppDomainCreate evt)
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
		return "AppDomainCreateEvent";
	}
}

package mssdw.request;

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 11.05.14
 */
public class AppDomainCreateRequest extends EventRequest
{
	public AppDomainCreateRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager)
	{
		super(virtualMachine, requestManager);
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.APPDOMAIN_CREATE;
	}
}

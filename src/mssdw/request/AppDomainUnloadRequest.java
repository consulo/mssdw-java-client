package mssdw.request;

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 11.05.14
 */
public class AppDomainUnloadRequest extends EventRequest
{
	public AppDomainUnloadRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager)
	{
		super(virtualMachine, requestManager);
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.APPDOMAIN_UNLOAD;
	}
}

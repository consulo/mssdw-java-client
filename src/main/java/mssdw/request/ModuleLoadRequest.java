package mssdw.request;

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 11.05.14
 */
public class ModuleLoadRequest extends EventRequest
{
	public ModuleLoadRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager)
	{
		super(virtualMachine, requestManager);
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.MODULE_LOAD;
	}
}

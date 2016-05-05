package mssdw.request;

import mssdw.EventKind;
import mssdw.EventRequestManagerImpl;
import mssdw.VirtualMachine;

/**
 * @author VISTALL
 * @since 24.04.14
 */
public class TypeLoadRequest extends TypeVisibleEventRequest
{
	public TypeLoadRequest(VirtualMachine virtualMachine, EventRequestManagerImpl requestManager)
	{
		super(virtualMachine, requestManager);
	}

	@Override
	public EventKind eventCmd()
	{
		return EventKind.TYPE_LOAD;
	}
}

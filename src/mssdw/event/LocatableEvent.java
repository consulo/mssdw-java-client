package mssdw.event;

import mssdw.JDWP;
import mssdw.Locatable;
import mssdw.Location;
import mssdw.MethodMirror;
import mssdw.ThreadMirror;
import mssdw.VirtualMachine;

public abstract class LocatableEvent extends ThreadedEvent implements Locatable
{
	private Location location;

	public LocatableEvent(VirtualMachine virtualMachine, JDWP.Event.Composite.Events.EventsCommon evt, int requestID, ThreadMirror thread, Location location)
	{
		super(virtualMachine, evt, requestID, thread);
		this.location = location;
	}

	@Override
	public Location location()
	{
		return location;
	}

	/**
	 * For MethodEntry and MethodExit
	 */
	public MethodMirror method()
	{
		return location.method();
	}

	@Override
	public String toString()
	{
		return eventName() + "@" +
				((location() == null) ? " null" : location().toString()) +
				" in thread " + thread().name();
	}
}

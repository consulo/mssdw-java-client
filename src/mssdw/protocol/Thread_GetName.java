package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.ThreadMirror;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class Thread_GetName implements Thread
{
	static final int COMMAND = 2;

	public static Thread_GetName process(VirtualMachineImpl vm, ThreadMirror thread) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, thread);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(
			VirtualMachineImpl vm, ThreadMirror thread)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(thread);
		ps.send();
		return ps;
	}

	static Thread_GetName waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Thread_GetName(vm, ps);
	}


	/**
	 * The thread name.
	 */
	public final String threadName;

	private Thread_GetName(VirtualMachineImpl vm, PacketStream ps)
	{
		threadName = ps.readString();
	}
}

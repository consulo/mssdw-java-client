package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.ThreadMirror;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class Thread_GetState implements Thread
{
	static final int COMMAND = 3;

	public static Thread_GetState process(VirtualMachineImpl vm, ThreadMirror thread) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, thread);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror thread)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(thread.id());
		ps.send();
		return ps;
	}

	static Thread_GetState waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Thread_GetState(vm, ps);
	}

	public final int debugState;
	public final int userState;

	private Thread_GetState(VirtualMachineImpl vm, PacketStream ps)
	{
		debugState = ps.readInt();
		userState = ps.readInt();
	}
}

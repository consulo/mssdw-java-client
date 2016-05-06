package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class VirtualMachine_GetVersion implements VirtualMachine
{
	static final int COMMAND = 1;

	public static VirtualMachine_GetVersion process(VirtualMachineImpl vm) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.send();
		return ps;
	}

	static VirtualMachine_GetVersion waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new VirtualMachine_GetVersion(vm, ps);
	}


	/**
	 * Text information on the VM version
	 */
	public final String description;

	/**
	 * Major DWP Version number
	 */
	public final int jdwpMajor;

	/**
	 * Minor DWP Version number
	 */
	public final int jdwpMinor;

	private VirtualMachine_GetVersion(VirtualMachineImpl vm, PacketStream ps)
	{
		description = ps.readString();
		jdwpMajor = ps.readInt();
		jdwpMinor = ps.readInt();
	}
}

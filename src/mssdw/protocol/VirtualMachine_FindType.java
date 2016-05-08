package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class VirtualMachine_FindType implements VirtualMachine
{
	static final int COMMAND = 10;

	public static VirtualMachine_FindType process(VirtualMachineImpl vm, String vmQName) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, vmQName);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, String vmQName)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeString(vmQName);
		ps.send();
		return ps;
	}

	static VirtualMachine_FindType waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new VirtualMachine_FindType(vm, ps);
	}

	public final TypeRef type;

	private VirtualMachine_FindType(VirtualMachineImpl vm, PacketStream ps)
	{
		type = ps.readTypeRef();
	}
}

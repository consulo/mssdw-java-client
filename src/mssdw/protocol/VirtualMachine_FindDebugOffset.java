package mssdw.protocol;

import mssdw.DebugInformationResult;
import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class VirtualMachine_FindDebugOffset implements VirtualMachine
{
	static final int COMMAND = 11;

	public static VirtualMachine_FindDebugOffset process(VirtualMachineImpl vm, String path, int line, int column) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, path, line, column);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, String path, int line, int column)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeString(path);
		ps.writeInt(line);
		ps.writeInt(column);
		ps.send();
		return ps;
	}

	static VirtualMachine_FindDebugOffset waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new VirtualMachine_FindDebugOffset(vm, ps);
	}

	public final DebugInformationResult result;

	private VirtualMachine_FindDebugOffset(VirtualMachineImpl vm, PacketStream ps)
	{
		boolean valid = ps.readByteBool();
		if(valid)
		{
			result = new DebugInformationResult(ps.readString(), ps.readInt(), ps.readInt());
		}
		else
		{
			result = null;
		}
	}
}

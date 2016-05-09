package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MirrorWithId;
import mssdw.PacketStream;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 26.04.14
 */
public class ArrayReference_GetValue implements ArrayReference
{
	static final int COMMAND = 1;

	public final Value<?> value;

	public static ArrayReference_GetValue process(VirtualMachineImpl vm, MirrorWithId objectValueMirror, int index) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, objectValueMirror, index);
		return waitForReply(vm, ps, index);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, MirrorWithId objectValueMirror, int index)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(objectValueMirror.id());
		ps.writeInt(index);
		ps.send();
		return ps;
	}

	static ArrayReference_GetValue waitForReply(VirtualMachineImpl vm, PacketStream ps, int length) throws JDWPException
	{
		ps.waitForReply();
		return new ArrayReference_GetValue(vm, ps, length);
	}

	private ArrayReference_GetValue(VirtualMachineImpl vm, PacketStream ps, int length)
	{
		value = ps.readValue();
	}
}

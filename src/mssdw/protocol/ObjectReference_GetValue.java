package mssdw.protocol;

import mssdw.FieldMirror;
import mssdw.JDWPException;
import mssdw.ObjectValueMirror;
import mssdw.PacketStream;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class ObjectReference_GetValue implements ObjectReference
{
	static final int COMMAND = 1;

	public static ObjectReference_GetValue process(VirtualMachineImpl vm, ObjectValueMirror objectValueMirror, FieldMirror fieldMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, objectValueMirror, fieldMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ObjectValueMirror objectValueMirror, FieldMirror fieldMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(objectValueMirror.id());
		ps.writeInt(fieldMirror.id());
		ps.send();
		return ps;
	}

	static ObjectReference_GetValue waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new ObjectReference_GetValue(vm, ps);
	}

	public final Value<?> value;

	private ObjectReference_GetValue(VirtualMachineImpl vm, PacketStream ps)
	{
		value = ps.readValue();
	}
}

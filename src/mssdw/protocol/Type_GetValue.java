package mssdw.protocol;

import mssdw.FieldMirror;
import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.TypeMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class Type_GetValue implements Type
{
	static final int COMMAND = 4;

	public static Type_GetValue process(VirtualMachineImpl vm, TypeMirror typeMirror, FieldMirror fieldMirror, StackFrameMirror stackFrameMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror, fieldMirror, stackFrameMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, TypeMirror typeMirror,FieldMirror fieldMirror, StackFrameMirror stackFrameMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(typeMirror.getTypeRef());
		ps.writeInt(fieldMirror.id());
		ps.writeInt(stackFrameMirror.thread().id());
		ps.writeInt(stackFrameMirror.id());
		ps.send();
		return ps;
	}

	static Type_GetValue waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetValue(vm, ps);
	}

	public final Value<?> value;

	private Type_GetValue(VirtualMachineImpl vm, PacketStream ps)
	{
		value = ps.readValue();
	}
}

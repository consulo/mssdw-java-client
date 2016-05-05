package mssdw.protocol;

import mssdw.CustomAttributeMirror;
import mssdw.FieldMirror;
import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.TypeMirror;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 23.07.2015
 */
public class Type_GetFieldCustomAttributes implements Type
{
	static final int COMMAND = 11;

	public static Type_GetFieldCustomAttributes process(VirtualMachineImpl vm, TypeMirror typeMirror, FieldMirror fieldMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror, fieldMirror);
		return waitForReply(vm, ps, typeMirror);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, TypeMirror typeMirror, FieldMirror fieldMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(typeMirror);
		ps.writeId(fieldMirror);
		ps.writeInt(0); // attribute id
		ps.send();
		return ps;
	}

	static Type_GetFieldCustomAttributes waitForReply(VirtualMachineImpl vm, PacketStream ps, TypeMirror typeMirror) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetFieldCustomAttributes(vm, ps, typeMirror);
	}

	public final CustomAttributeMirror[] customAttributeMirrors;

	private Type_GetFieldCustomAttributes(VirtualMachineImpl vm, PacketStream ps, TypeMirror parent)
	{
		customAttributeMirrors = ps.readCustomAttributes();
	}
}

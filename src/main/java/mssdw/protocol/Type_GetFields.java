package mssdw.protocol;

import mssdw.FieldMirror;
import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.TypeMirror;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class Type_GetFields implements Type
{
	static final int COMMAND = 3;

	public static Type_GetFields process(VirtualMachineImpl vm, TypeMirror typeMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror);
		return waitForReply(vm, ps, typeMirror);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, TypeMirror typeMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(typeMirror.getTypeRef());
		ps.send();
		return ps;
	}

	static Type_GetFields waitForReply(VirtualMachineImpl vm, PacketStream ps, TypeMirror typeMirror) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetFields(vm, ps, typeMirror);
	}

	public final FieldMirror[] fields;

	private Type_GetFields(VirtualMachineImpl vm, PacketStream ps, TypeMirror parent)
	{
		int size = ps.readInt();
		fields = new FieldMirror[size];
		for(int i = 0; i < size; i++)
		{
			int id = ps.readInt();
			String name = ps.readString();
			TypeRef typeRef = ps.readTypeRef();
			int attributes = ps.readInt();
			fields[i] = new FieldMirror(vm, id, name, typeRef, parent, attributes);
		}
	}
}

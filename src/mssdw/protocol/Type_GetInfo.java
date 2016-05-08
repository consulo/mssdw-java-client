package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.TypeMirror;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class Type_GetInfo implements Type
{
	static final int COMMAND = 1;

	public static Type_GetInfo process(VirtualMachineImpl vm, TypeMirror typeMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror);
		return waitForReply(vm, typeMirror, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, TypeMirror typeMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(typeMirror.getTypeRef());
		ps.send();
		return ps;
	}

	static Type_GetInfo waitForReply(VirtualMachineImpl vm, TypeMirror typeMirror, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetInfo(vm, typeMirror, ps);
	}

	public final String namespace;
	public final String name;
	public final String fullName;
	public final TypeRef baseTypeRef;
	public final int attributes;
	public final boolean isArray;

	private Type_GetInfo(VirtualMachineImpl vm, TypeMirror parent, PacketStream ps)
	{
		namespace = ps.readString();
		name = ps.readString();
		fullName = ps.readString();
		baseTypeRef = ps.readTypeRef();
		attributes = ps.readInt();
		isArray = ps.readByteBool();
	}
}

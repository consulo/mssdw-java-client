package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.PropertyMirror;
import mssdw.TypeMirror;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class Type_GetProperties implements Type
{
	static final int COMMAND = 9;

	public static Type_GetProperties process(VirtualMachineImpl vm, TypeMirror typeMirror) throws JDWPException
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

	static Type_GetProperties waitForReply(VirtualMachineImpl vm, PacketStream ps, TypeMirror typeMirror) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetProperties(vm, ps, typeMirror);
	}

	public final PropertyMirror[] properties;

	private Type_GetProperties(VirtualMachineImpl vm, PacketStream ps, TypeMirror parent)
	{
		int size = ps.readInt();
		properties = new PropertyMirror[size];
		for(int i = 0; i < size; i++)
		{
			int id = ps.readInt();
			String name = ps.readString();
			int attributes = ps.readInt();
			int getMethodId = ps.readInt();
			int setMethodId = ps.readInt();
			properties[i] = new PropertyMirror(vm, id, name, getMethodId, setMethodId, parent, attributes);
		}
	}
}

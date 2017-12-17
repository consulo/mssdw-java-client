package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.TypeMirror;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class Type_GetMethods implements Type
{
	static final int COMMAND = 2;

	public static Type_GetMethods process(VirtualMachineImpl vm, TypeMirror typeMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror);
		return waitForReply(vm, ps, typeMirror.getTypeRef());
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, TypeMirror typeMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(typeMirror.getTypeRef());
		ps.send();
		return ps;
	}

	static Type_GetMethods waitForReply(VirtualMachineImpl vm, PacketStream ps, TypeRef typeRef) throws JDWPException
	{
		ps.waitForReply();
		return new Type_GetMethods(vm, ps, typeRef);
	}

	public final MethodMirror[] methods;

	private Type_GetMethods(VirtualMachineImpl vm, PacketStream ps, TypeRef typeRef)
	{
		int size = ps.readInt();
		methods = new MethodMirror[size];
		for(int i = 0; i < size; i++)
		{
			int id = ps.readInt();
			methods[i] = new MethodMirror(vm, typeRef, id);
		}
	}
}

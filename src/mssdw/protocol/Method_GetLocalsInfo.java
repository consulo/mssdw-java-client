package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.LocalVariableMirror;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 12.04.14
 */
public class Method_GetLocalsInfo implements Method
{
	static final int COMMAND = 5;

	public static Method_GetLocalsInfo process(VirtualMachineImpl vm, MethodMirror methodMirror, StackFrameMirror stackFrameMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, methodMirror, stackFrameMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, MethodMirror methodMirror, StackFrameMirror stackFrameMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(methodMirror.getTypeRef());
		ps.writeInt(methodMirror.id());
		ps.writeInt(stackFrameMirror.thread().id());
		ps.writeInt(stackFrameMirror.id());
		ps.send();
		return ps;
	}

	static Method_GetLocalsInfo waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Method_GetLocalsInfo(vm, ps);
	}

	public final LocalVariableMirror[] localVariables;

	private Method_GetLocalsInfo(VirtualMachineImpl vm, PacketStream ps)
	{
		int size = ps.readInt();

		this.localVariables = new LocalVariableMirror[size];

		for(int i = 0; i < size; i++)
		{
			int index = ps.readInt();
			String name = ps.readString();
			TypeRef typeRef = ps.readTypeRef();
			localVariables[i] = new LocalVariableMirror(vm, index, typeRef, name);
		}
	}
}

package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class Method_GetName implements Method
{
	static final int COMMAND = 1;

	public static Method_GetName process(VirtualMachineImpl vm, MethodMirror methodMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, methodMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, MethodMirror methodMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(methodMirror.getTypeRef());
		ps.writeInt(methodMirror.id());
		ps.send();
		return ps;
	}

	static Method_GetName waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Method_GetName(vm, ps);
	}


	public final String name;

	private Method_GetName(VirtualMachineImpl vm, PacketStream ps)
	{
		name = ps.readString();
	}
}

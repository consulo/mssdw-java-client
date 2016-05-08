package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class Method_GetInfo implements Method
{
	static final int COMMAND = 6;

	public static Method_GetInfo process(VirtualMachineImpl vm, MethodMirror methodMirror) throws JDWPException
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

	static Method_GetInfo waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Method_GetInfo(vm, ps);
	}

	public final int attributes;
	public final int implAttributes;

	private Method_GetInfo(VirtualMachineImpl vm, PacketStream ps)
	{
		attributes = ps.readInt();
		implAttributes = ps.readInt();
	}
}

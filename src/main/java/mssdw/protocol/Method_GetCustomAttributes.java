package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 23.07.2015
 */
public class Method_GetCustomAttributes implements Method
{
	static final int COMMAND = 9;

	public static Method_GetCustomAttributes process(VirtualMachineImpl vm, MethodMirror methodMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, methodMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, MethodMirror methodMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeTypeRef(methodMirror.declarationTypeRef());
		ps.writeInt(methodMirror.id());
		ps.send();
		return ps;
	}

	static Method_GetCustomAttributes waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Method_GetCustomAttributes(vm, ps);
	}

	public final String[] customAttributeMirrors;

	private Method_GetCustomAttributes(VirtualMachineImpl vm, PacketStream ps)
	{
		customAttributeMirrors = ps.readCustomAttributes();
	}
}

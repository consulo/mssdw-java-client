package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.MethodParameterMirror;
import mssdw.PacketStream;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class Method_GetParamInfo implements Method
{
	static final int COMMAND = 4;

	public static Method_GetParamInfo process(VirtualMachineImpl vm, MethodMirror methodMirror) throws JDWPException
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

	static Method_GetParamInfo waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Method_GetParamInfo(vm, ps);
	}

	public final int callConversion;
	public final int genericParameterCount;
	public final TypeRef returnType;
	public final MethodParameterMirror[] parameters;

	private Method_GetParamInfo(VirtualMachineImpl vm, PacketStream ps)
	{
		callConversion = ps.readInt();
		int parameterCount = ps.readInt();
		genericParameterCount = ps.readInt();
		returnType = ps.readTypeRef();

		parameters = new MethodParameterMirror[parameterCount];
		for(int i = 0; i < parameterCount; i++)
		{
			parameters[i] = new MethodParameterMirror(vm, i, ps.readTypeRef(), ps.readString());
		}
	}
}

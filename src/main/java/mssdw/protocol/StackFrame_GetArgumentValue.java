package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.MethodParameterMirror;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class StackFrame_GetArgumentValue implements StackFrame
{
	static final int COMMAND = 3;

	public static StackFrame_GetArgumentValue process(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror, MethodParameterMirror parameterMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, stackFrameMirror, parameterMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror, MethodParameterMirror pos)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(threadMirror.id());
		ps.writeInt(stackFrameMirror.id());
		ps.writeInt(pos.id());
		ps.send();
		return ps;
	}

	static StackFrame_GetArgumentValue waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new StackFrame_GetArgumentValue(vm, ps);
	}

	public Value value;

	private StackFrame_GetArgumentValue(VirtualMachineImpl vm, PacketStream ps)
	{
		value = ps.readValue();
	}
}

package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.LocalVariableMirror;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class StackFrame_GetLocalValue implements StackFrame
{
	static final int COMMAND = 1;

	public static StackFrame_GetLocalValue process(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror, LocalVariableMirror localVariableMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, stackFrameMirror, localVariableMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror, LocalVariableMirror pos)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(threadMirror.id());
		ps.writeInt(stackFrameMirror.id());
		ps.writeInt(pos.id());
		ps.send();
		return ps;
	}

	static StackFrame_GetLocalValue waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new StackFrame_GetLocalValue(vm, ps);
	}

	public Value value;

	private StackFrame_GetLocalValue(VirtualMachineImpl vm, PacketStream ps)
	{
		value = ps.readValue();
	}
}

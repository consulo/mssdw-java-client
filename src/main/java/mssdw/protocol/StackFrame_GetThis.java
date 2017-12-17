package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class StackFrame_GetThis implements StackFrame
{
	static final int COMMAND = 2;

	public static StackFrame_GetThis process(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, stackFrameMirror);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror threadMirror, StackFrameMirror stackFrameMirror)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeInt(threadMirror.id());
		ps.writeInt(stackFrameMirror.id());
		ps.send();
		return ps;
	}

	static StackFrame_GetThis waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new StackFrame_GetThis(vm, ps);
	}

	public Value value;

	private StackFrame_GetThis(VirtualMachineImpl vm, PacketStream ps)
	{
		value = ps.readValue();
	}
}

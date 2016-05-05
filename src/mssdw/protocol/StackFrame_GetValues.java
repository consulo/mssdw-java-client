package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.LocalVariableOrParameterMirror;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class StackFrame_GetValues implements StackFrame
{
	static final int COMMAND = 1;

	public static StackFrame_GetValues process(
			VirtualMachineImpl vm,
			ThreadMirror threadMirror,
			StackFrameMirror stackFrameMirror,
			LocalVariableOrParameterMirror... pos) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, stackFrameMirror, pos);
		return waitForReply(vm, ps, pos.length);
	}


	static PacketStream enqueueCommand(
			VirtualMachineImpl vm,
			ThreadMirror threadMirror,
			StackFrameMirror stackFrameMirror,
			LocalVariableOrParameterMirror[] pos)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(threadMirror);
		ps.writeId(stackFrameMirror);
		ps.writeInt(pos.length);
		for(LocalVariableOrParameterMirror po : pos)
		{
			ps.writeInt(po.idForStackFrame());
		}
		ps.send();
		return ps;
	}

	static StackFrame_GetValues waitForReply(VirtualMachineImpl vm, PacketStream ps, int length) throws JDWPException
	{
		ps.waitForReply();
		return new StackFrame_GetValues(vm, ps, length);
	}

	public Value[] values;

	private StackFrame_GetValues(VirtualMachineImpl vm, PacketStream ps, int length)
	{
		values = new Value[length];
		for(int i = 0; i < length; i++)
		{
			values[i] = ps.readValue();
		}
	}
}

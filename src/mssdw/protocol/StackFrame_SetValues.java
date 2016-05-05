package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.LocalVariableOrParameterMirror;
import mssdw.PacketStream;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;
import mssdw.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 13.04.14
 */
public class StackFrame_SetValues implements StackFrame
{
	static final int COMMAND = 3;

	public static StackFrame_SetValues process(
			VirtualMachineImpl vm,
			ThreadMirror threadMirror,
			StackFrameMirror stackFrameMirror,
			ImmutablePair<LocalVariableOrParameterMirror, Value<?>>... pairs) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, stackFrameMirror, pairs);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(
			VirtualMachineImpl vm,
			ThreadMirror threadMirror,
			StackFrameMirror stackFrameMirror,
			ImmutablePair<LocalVariableOrParameterMirror, Value<?>>... pairs)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(threadMirror);
		ps.writeId(stackFrameMirror);
		ps.writeInt(pairs.length);
		for(ImmutablePair<LocalVariableOrParameterMirror, Value<?>> po : pairs)
		{
			ps.writeInt(po.getLeft().idForStackFrame());
		}
		for(ImmutablePair<LocalVariableOrParameterMirror, Value<?>> po : pairs)
		{
			ps.writeValue(po.getRight());
		}
		ps.send();
		return ps;
	}

	static StackFrame_SetValues waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new StackFrame_SetValues(vm, ps);
	}


	private StackFrame_SetValues(VirtualMachineImpl vm, PacketStream ps)
	{
	}
}

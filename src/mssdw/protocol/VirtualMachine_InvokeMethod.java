package mssdw.protocol;

import mssdw.InvokeFlags;
import mssdw.JDWPException;
import mssdw.MethodMirror;
import mssdw.PacketStream;
import mssdw.ThreadMirror;
import mssdw.ThrowValueException;
import mssdw.Value;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class VirtualMachine_InvokeMethod implements VirtualMachine
{
	static final int COMMAND = 7;

	public static VirtualMachine_InvokeMethod process(VirtualMachineImpl vm, ThreadMirror threadMirror, InvokeFlags invokeFlags,
			MethodMirror methodMirror, Value<?> thisObjectMirror, Value<?>... arguments) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, threadMirror, invokeFlags, methodMirror, thisObjectMirror, arguments);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror threadMirror, InvokeFlags invokeFlags, MethodMirror methodMirror,
			Value<?> thisObjectMirror, Value<?>[] arguments)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(threadMirror);
		ps.writeInt(invokeFlags.ordinal());
		ps.writeId(methodMirror);
		ps.writeValue(thisObjectMirror);
		ps.writeInt(arguments.length);
		for(Value<?> argument : arguments)
		{
			ps.writeValue(argument);
		}
		ps.send();
		return ps;
	}

	static VirtualMachine_InvokeMethod waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new VirtualMachine_InvokeMethod(vm, ps);
	}

	private boolean myThrowException;
	private Value myValue;

	private VirtualMachine_InvokeMethod(VirtualMachineImpl vm, PacketStream ps)
	{
		byte result = ps.readByte();
		myThrowException = result == 0;
		myValue = ps.readValue();
	}

	public Value<?> getValue()
	{
		if(myThrowException)
		{
			throw new ThrowValueException(myValue);
		}
		else
		{
			return myValue;
		}
	}
}
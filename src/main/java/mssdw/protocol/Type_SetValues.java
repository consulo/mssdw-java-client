package mssdw.protocol;

import mssdw.FieldOrPropertyMirror;
import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.TypeMirror;
import mssdw.Value;
import mssdw.VirtualMachineImpl;
import mssdw.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public class Type_SetValues implements Type
{
	static final int COMMAND = 7;

	public static Type_SetValues process(
			VirtualMachineImpl vm,
			TypeMirror typeMirror,
			ImmutablePair<FieldOrPropertyMirror, Value<?>>... pairs) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, typeMirror, pairs);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(
			VirtualMachineImpl vm,
			TypeMirror typeMirror,
			ImmutablePair<FieldOrPropertyMirror, Value<?>>... pairs)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(typeMirror);
		ps.writeInt(pairs.length);
		for(ImmutablePair<FieldOrPropertyMirror, Value<?>> po : pairs)
		{
			ps.writeId(po.getLeft());
		}
		for(ImmutablePair<FieldOrPropertyMirror, Value<?>> po : pairs)
		{
			ps.writeValue(po.getRight());
		}
		ps.send();
		return ps;
	}

	static Type_SetValues waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Type_SetValues(vm, ps);
	}

	private Type_SetValues(VirtualMachineImpl vm, PacketStream ps)
	{
	}
}

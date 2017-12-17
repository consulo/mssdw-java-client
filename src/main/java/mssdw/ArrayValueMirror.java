package mssdw;

import org.jetbrains.annotations.NotNull;
import mssdw.protocol.ArrayReference_GetValue;
import mssdw.protocol.ArrayReference_SetValues;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class ArrayValueMirror extends ValueImpl<Object> implements MirrorWithId
{
	private int myLength;
	private final ObjectValueMirror myObjectValueMirror;

	public ArrayValueMirror(VirtualMachine aVm, int length, ObjectValueMirror objectValueMirror)
	{
		super(aVm);
		myLength = length;
		myObjectValueMirror = objectValueMirror;
	}

	@NotNull
	public Value<?> get(int index)
	{
		try
		{
			return ArrayReference_GetValue.process(vm, myObjectValueMirror, index).value;
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	public void set(int index, @NotNull Value<?> value)
	{
		try
		{
			ArrayReference_SetValues.process(vm, myObjectValueMirror,index, new Value[] {value});
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	public int length()
	{
		return myLength;
	}

	@Override
	public TypeMirror type()
	{
		return myObjectValueMirror.type();
	}

	@Override
	public void accept(@NotNull ValueVisitor valueVisitor)
	{
		valueVisitor.visitArrayValue(this);
	}

	@NotNull
	public ObjectValueMirror object()
	{
		return myObjectValueMirror;
	}

	@Override
	public Object value()
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ArrayValue { type = ").append(type()).append(", length = ").append(length()).append(" }");
		return builder.toString();
	}

	@Override
	public int id()
	{
		return myObjectValueMirror.id();
	}
}

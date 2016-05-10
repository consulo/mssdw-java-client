package mssdw;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 05.01.2016
 */
public abstract class ValueTypeValueMirror<T> extends ValueImpl<T>
{
	private int myId;
	private long myAddress;
	private TypeMirror myTypeMirror;
	private Value[] myValues;

	public ValueTypeValueMirror(int id, long address, VirtualMachine aVm, @NotNull TypeMirror typeMirror, Value... values)
	{
		super(aVm);
		myId = id;
		myAddress = address;
		myTypeMirror = typeMirror;
		myValues = values;
	}

	public int id()
	{
		return myId;
	}

	public long address()
	{
		return myAddress;
	}

	public abstract boolean isEnum();

	@NotNull
	public Value[] fieldValues()
	{
		return myValues;
	}

	@NotNull
	@Override
	public TypeMirror type()
	{
		return myTypeMirror;
	}
}

package mssdw;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class StringValueMirror extends ValueImpl<String>
{
	private int myId;
	private final String myValue;

	public StringValueMirror(VirtualMachine aVm, int id, String value)
	{
		super(aVm);
		myId = id;
		myValue = value;
	}

	public int id()
	{
		return myId;
	}

	@Nonnull
	@Override
	public String value()
	{
		return myValue;
	}

	@Override
	public TypeMirror type()
	{
		return virtualMachine().findTypeByQualifiedName(TypeTag.String.getType());
	}

	@Override
	public void accept(@Nonnull ValueVisitor valueVisitor)
	{
		valueVisitor.visitStringValue(this, value());
	}
}

package mssdw;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class StringValueMirror extends ValueImpl<String>
{
	private final String myValue;

	public StringValueMirror(VirtualMachine aVm, String value)
	{
		super(aVm);
		myValue = value;
	}

	@NotNull
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
	public void accept(@NotNull ValueVisitor valueVisitor)
	{
		valueVisitor.visitStringValue(this, value());
	}
}

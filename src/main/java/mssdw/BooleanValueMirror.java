package mssdw;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public class BooleanValueMirror extends ValueImpl<Boolean>
{
	private final Boolean myValue;

	public BooleanValueMirror(VirtualMachine aVm, @Nonnull Boolean value)
	{
		super(aVm);
		myValue = value;
	}

	@Nullable
	@Override
	public TypeMirror type()
	{
		return virtualMachine().findTypeByQualifiedName(TypeTag.Boolean.getType());
	}

	@Nonnull
	@Override
	public Boolean value()
	{
		return myValue;
	}

	@Override
	public void accept(@Nonnull ValueVisitor valueVisitor)
	{
		valueVisitor.visitBooleanValue(this, value());
	}
}

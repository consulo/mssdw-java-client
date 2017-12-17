package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public class VoidValueMirror extends ValueImpl<Void>
{
	public VoidValueMirror(VirtualMachine aVm)
	{
		super(aVm);
	}

	@Nullable
	@Override
	public TypeMirror type()
	{
		return virtualMachine().findTypeByQualifiedName(TypeTag.Void.getType());
	}

	@Nullable
	@Override
	public Void value()
	{
		return null;
	}

	@Override
	public void accept(@NotNull ValueVisitor valueVisitor)
	{

	}
}
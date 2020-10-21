package mssdw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 23.07.2015
 */
public class TypeValueMirror extends ValueImpl<TypeMirror>
{
	private TypeMirror myTypeMirror;

	TypeValueMirror(VirtualMachine aVm, TypeMirror typeMirror)
	{
		super(aVm);
		myTypeMirror = typeMirror;
	}

	@Nullable
	@Override
	public TypeMirror type()
	{
		return vm.findTypeByQualifiedName("System.Type");
	}

	@Nonnull
	@Override
	public TypeMirror value()
	{
		return myTypeMirror;
	}

	@Override
	public void accept(@Nonnull ValueVisitor valueVisitor)
	{
		valueVisitor.visitTypeValue(this, value());
	}
}

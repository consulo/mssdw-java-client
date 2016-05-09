package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 25.04.14
 */
public class CharValueMirror extends ValueImpl<Character>
{
	private final Character myChar;

	public CharValueMirror(VirtualMachine aVm, Character character)
	{
		super(aVm);
		myChar = character;
	}

	@Nullable
	@Override
	public TypeMirror type()
	{
		return virtualMachine().findTypeByQualifiedName(TypeTag.Char.getType());
	}

	@NotNull
	@Override
	public Character value()
	{
		return myChar;
	}

	@Override
	public void accept(@NotNull ValueVisitor valueVisitor)
	{
		valueVisitor.visitCharValue(this, value());
	}
}

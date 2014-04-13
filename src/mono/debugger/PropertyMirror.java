package mono.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class PropertyMirror extends FieldOrPropertyMirror
{
	private final MethodMirror myGetMethod;
	private final MethodMirror mySetMethod;

	public PropertyMirror(
			@NotNull VirtualMachine aVm,
			long id,
			@NotNull String name,
			@Nullable MethodMirror getMethod,
			@Nullable MethodMirror setMethod,
			@NotNull TypeMirror parent,
			int attributes)
	{
		super(aVm, id, parent, attributes, name);
		myGetMethod = getMethod;
		mySetMethod = setMethod;
	}

	public MethodMirror methodGet()
	{
		return myGetMethod;
	}

	public MethodMirror methodSet()
	{
		return mySetMethod;
	}

	@Override
	@NotNull
	public TypeMirror type()
	{
		if(myGetMethod != null)
		{
			TypeMirror typeMirror = myGetMethod.returnType();
			assert typeMirror != null;
			return typeMirror;
		}
		else if(mySetMethod != null)
		{
			return mySetMethod.parameters()[0].type();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public boolean isStatic()
	{
		if(myGetMethod != null)
		{
			return myGetMethod.isStatic();
		}
		else if(mySetMethod != null)
		{
			return mySetMethod.isStatic();
		}
		throw new IllegalArgumentException();
	}
}
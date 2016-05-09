package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mssdw.protocol.Type_GetPropertyCustomAttributes;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class PropertyMirror extends FieldOrPropertyMirror
{
	private final MethodMirror myGetMethod;
	private final MethodMirror mySetMethod;

	public PropertyMirror(@NotNull VirtualMachine aVm,
			int id,
			@NotNull String name,
			int getMethodId,
			int setMethodId,
			@NotNull TypeMirror parent,
			int attributes)
	{
		super(aVm, id, parent, attributes, name);
		myGetMethod = getMethodId == 0 ? null : new MethodMirror(aVm, parent.getTypeRef(), getMethodId);
		mySetMethod = setMethodId == 0 ? null : new MethodMirror(aVm, parent.getTypeRef(), setMethodId);
	}

	public MethodMirror methodGet()
	{
		return myGetMethod;
	}

	public MethodMirror methodSet()
	{
		return mySetMethod;
	}

	/**
	 * In .NET bytecode - index method like
	 * <p/>
	 * T this[int index]
	 * {
	 * }
	 * <p/>
	 * Stored in bytecode as Property with name `Item`.
	 * And accessors methods have +1 parameter(index)
	 * For original properties - get have no parameters, set - have one parameter
	 */
	public boolean isArrayProperty()
	{
		if(myGetMethod != null)
		{
			return myGetMethod.parameters().length >= 1;
		}
		else if(mySetMethod != null)
		{
			return mySetMethod.parameters().length >= 2;
		}
		throw new IllegalArgumentException("Not setter and getter");
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
	public Value<?> value(@Nullable StackFrameMirror stackFrameMirror, @Nullable ObjectValueMirror thisObjectValue)
	{
		if(isStatic() && thisObjectValue != null || !isStatic() && thisObjectValue == null)
		{
			throw new IllegalArgumentException();
		}

		if(stackFrameMirror == null)
		{
			throw new IllegalArgumentException("No thread mirror");
		}
		if(myGetMethod != null)
		{
			return myGetMethod.invoke(stackFrameMirror, thisObjectValue);
		}
		return null;
	}

	@Override
	public void setValue(@Nullable StackFrameMirror stackFrameMirror, @Nullable ObjectValueMirror thisObjectValue, @NotNull Value<?> value)
	{
		if(isStatic() && thisObjectValue != null || !isStatic() && thisObjectValue == null)
		{
			throw new IllegalArgumentException();
		}

		if(stackFrameMirror == null)
		{
			throw new IllegalArgumentException("No thread mirror");
		}

		if(mySetMethod != null)
		{
			mySetMethod.invoke(stackFrameMirror, thisObjectValue, value);
		}
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

	@NotNull
	@Override
	public CustomAttributeMirror[] customAttributesImpl() throws JDWPException
	{
		return Type_GetPropertyCustomAttributes.process(vm, parent(), this).customAttributeMirrors;
	}
}

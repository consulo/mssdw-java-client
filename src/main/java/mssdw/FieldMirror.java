package mssdw;

import jakarta.annotation.Nonnull;

import consulo.internal.dotnet.asm.signature.FieldAttributes;
import mssdw.protocol.ObjectReference_GetValue;
import mssdw.protocol.ObjectReference_SetValues;
import mssdw.protocol.Type_GetFieldCustomAttributes;
import mssdw.protocol.Type_GetValue;
import mssdw.protocol.Type_SetValues;
import mssdw.util.ImmutablePair;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class FieldMirror extends FieldOrPropertyMirror
{
	private final TypeRef myTypeRef;
	private TypeMirror myTypeMirror;

	public FieldMirror(@Nonnull VirtualMachine aVm, int id, @Nonnull String name, @Nonnull TypeRef typeRef, @Nonnull TypeMirror parent, int attributes)
	{
		super(aVm, id, parent, attributes, name);
		myTypeRef = typeRef;
	}

	@Override
	public Value<?> value(@Nonnull StackFrameMirror stackFrameMirror, @Nullable ObjectValueMirror thisObjectValue)
	{
		if(isStatic() && thisObjectValue != null || !isStatic() && thisObjectValue == null)
		{
			throw new IllegalArgumentException();
		}

		try
		{
			if(thisObjectValue == null)
			{
				Type_GetValue process = Type_GetValue.process(vm, parent(), this, stackFrameMirror);
				return process.value;
			}
			else
			{
				ObjectReference_GetValue process = ObjectReference_GetValue.process(vm, thisObjectValue, this);
				return process.value;
			}
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(@Nullable StackFrameMirror stackFrameMirror, @Nullable ObjectValueMirror thisObjectValue, @Nonnull Value<?> value)
	{
		if(isStatic() && thisObjectValue != null || !isStatic() && thisObjectValue == null)
		{
			throw new IllegalArgumentException();
		}

		try
		{
			if(thisObjectValue == null)
			{
				Type_SetValues.process(vm, parent(), new ImmutablePair<FieldOrPropertyMirror, Value<?>>(this, value));
			}
			else
			{
				ObjectReference_SetValues.process(vm, thisObjectValue, new ImmutablePair<FieldOrPropertyMirror, Value<?>>(this, value));
			}
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Override
	@Nonnull
	public TypeMirror type()
	{
		if(myTypeMirror != null)
		{
			return myTypeMirror;
		}
		return myTypeMirror = new TypeMirror(virtualMachine(), myTypeRef);
	}

	@Nonnull
	@Override
	public String[] customAttributesImpl() throws JDWPException
	{
		return Type_GetFieldCustomAttributes.process(vm, parent(), this).customAttributeMirrors;
	}

	@Override
	public boolean isStatic()
	{
		return (myAttributes & FieldAttributes.Static) == FieldAttributes.Static;
	}

	@Override
	public boolean isAbstract()
	{
		return false;
	}
}

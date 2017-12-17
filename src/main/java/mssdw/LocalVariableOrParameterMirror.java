package mssdw;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 12.04.14
 */
public abstract class LocalVariableOrParameterMirror  extends MirrorWithIdAndName
{
	private final String myName;
	private final TypeRef myTypeRef;

	private TypeMirror myType;

	public LocalVariableOrParameterMirror(VirtualMachineImpl vm, int i, TypeRef typeRef, String name)
	{
		super(vm, i);
		myTypeRef = typeRef;
		myName = name;
	}

	@NotNull
	public TypeMirror type()
	{
		if(myType != null)
		{
			return myType;
		}
		return myType = new TypeMirror(virtualMachine(), myTypeRef);
	}

	@Deprecated
	public int idForStackFrame()
	{
		return id();
	}

	@NotNull
	@Override
	protected String nameImpl() throws JDWPException
	{
		return myName;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName()).append(" {").append(" id = ").append(id()).append(", name = ").append(name()).append(", type = ")
				.append(type()).append(" }");
		return builder.toString();
	}
}

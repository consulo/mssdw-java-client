package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class ObjectValueMirror extends ValueImpl<Object> implements MirrorWithId
{
	private final int myId;
	private final long myAddress;
	private final TypeRef myTypeRef;

	public ObjectValueMirror(VirtualMachine aVm, int id, long address, TypeRef typeRef)
	{
		super(aVm);
		myId = id;
		myAddress = address;
		myTypeRef = typeRef;
	}

	public long address()
	{
		return myAddress;
	}

	@Override
	public int id()
	{
		return myId;
	}

	@Override
	public TypeMirror type()
	{
		return new TypeMirror(virtualMachine(), myTypeRef);
	}

	@Nullable
	@Override
	public Object value()
	{
		return "object";
	}

	@Override
	public void accept(@NotNull ValueVisitor valueVisitor)
	{
		valueVisitor.visitObjectValue(this);
	}
}

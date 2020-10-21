package mssdw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	public void accept(@Nonnull ValueVisitor valueVisitor)
	{
		valueVisitor.visitObjectValue(this);
	}
}

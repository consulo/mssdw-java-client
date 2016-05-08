package mssdw;

import java.util.List;

/**
 * @author VISTALL
 * @since 5/6/2016
 */
public class TypeRef
{
	private int myModuleNameId;

	private int myClassToken;

	private boolean myIsPointer;

	private boolean myIsByRef;

	private List<Integer> myArraySizes;

	private List<Integer> myArrayLowerBounds;

	public TypeRef(int moduleNameId, int classToken, boolean isPointer, boolean isByRef, List<Integer> arraySizes, List<Integer> arrayLowerBounds)
	{
		myModuleNameId = moduleNameId;
		myClassToken = classToken;
		myIsPointer = isPointer;
		myIsByRef = isByRef;
		myArraySizes = arraySizes;
		myArrayLowerBounds = arrayLowerBounds;
	}

	public int getModuleNameId()
	{
		return myModuleNameId;
	}

	public int getClassToken()
	{
		return myClassToken;
	}

	public boolean isPointer()
	{
		return myIsPointer;
	}

	public boolean isByRef()
	{
		return myIsByRef;
	}

	public List<Integer> getArraySizes()
	{
		return myArraySizes;
	}

	public List<Integer> getArrayLowerBounds()
	{
		return myArrayLowerBounds;
	}
}

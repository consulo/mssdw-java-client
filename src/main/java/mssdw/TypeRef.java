package mssdw;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 5/6/2016
 */
public class TypeRef
{
	private String myModuleName;

	private int myClassToken;

	private boolean myIsPointer;

	private boolean myIsByRef;

	private List<Integer> myArraySizes;

	private List<Integer> myArrayLowerBounds;

	public TypeRef(@NotNull String moduleName, int classToken, boolean isPointer, boolean isByRef, List<Integer> arraySizes, List<Integer> arrayLowerBounds)
	{
		myModuleName = moduleName;
		myClassToken = classToken;
		myIsPointer = isPointer;
		myIsByRef = isByRef;
		myArraySizes = arraySizes;
		myArrayLowerBounds = arrayLowerBounds;
	}

	public String getModuleName()
	{
		return myModuleName;
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		TypeRef typeRef = (TypeRef) o;

		if(!myModuleName.equals(typeRef.myModuleName))
		{
			return false;
		}
		if(myClassToken != typeRef.myClassToken)
		{
			return false;
		}
		if(myIsPointer != typeRef.myIsPointer)
		{
			return false;
		}
		if(myIsByRef != typeRef.myIsByRef)
		{
			return false;
		}
		if(myArraySizes != null ? !myArraySizes.equals(typeRef.myArraySizes) : typeRef.myArraySizes != null)
		{
			return false;
		}
		if(myArrayLowerBounds != null ? !myArrayLowerBounds.equals(typeRef.myArrayLowerBounds) : typeRef.myArrayLowerBounds != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = myModuleName.hashCode();
		result = 31 * result + myClassToken;
		result = 31 * result + (myIsPointer ? 1 : 0);
		result = 31 * result + (myIsByRef ? 1 : 0);
		result = 31 * result + (myArraySizes != null ? myArraySizes.hashCode() : 0);
		result = 31 * result + (myArrayLowerBounds != null ? myArrayLowerBounds.hashCode() : 0);
		return result;
	}
}

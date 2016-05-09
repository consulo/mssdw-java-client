package mssdw;

/**
 * @author VISTALL
 * @since 5/9/2016
 */
public class DebugInformationResult
{
	private final String myModuleName;
	private final int myMethodToken;
	private final int myOffset;

	public DebugInformationResult(String moduleName, int methodToken, int offset)
	{
		myModuleName = moduleName;
		myMethodToken = methodToken;
		myOffset = offset;
	}

	public String getModuleName()
	{
		return myModuleName;
	}

	public int getMethodToken()
	{
		return myMethodToken;
	}

	public int getOffset()
	{
		return myOffset;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("DebugInformationResult{");
		sb.append("myModuleName='").append(myModuleName).append('\'');
		sb.append(", myMethodToken=").append(myMethodToken);
		sb.append(", myOffset=").append(myOffset);
		sb.append('}');
		return sb.toString();
	}
}

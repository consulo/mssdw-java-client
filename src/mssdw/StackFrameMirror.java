package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mssdw.protocol.StackFrame_GetThis;
import mssdw.protocol.StackFrame_GetValues;
import mssdw.protocol.StackFrame_SetValues;
import mssdw.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class StackFrameMirror extends MirrorImpl implements MirrorWithId
{
	private final ThreadMirror myThreadMirror;
	private final int myFrameID;
	private final String myFilePath;
	private final int myLine;
	private final int myColumn;
	private final TypeRef myTypeRef;
	private final int myFunctionId;

	public StackFrameMirror(VirtualMachine aVm, ThreadMirror threadMirror, int frameID, String filePath, int line, int column, TypeRef typeRef, int functionId)
	{
		super(aVm);
		myThreadMirror = threadMirror;
		myFrameID = frameID;
		myFilePath = filePath;
		myLine = line;
		myColumn = column;
		myTypeRef = typeRef;
		myFunctionId = functionId;
	}

	public String getFilePath()
	{
		return myFilePath;
	}

	public int getLine()
	{
		return myLine;
	}

	public int getColumn()
	{
		return myColumn;
	}

	public TypeRef getTypeRef()
	{
		return myTypeRef;
	}

	public int getFunctionId()
	{
		return myFunctionId;
	}

	@NotNull
	public MethodMirror getMethod()
	{
		return new MethodMirror(virtualMachine(), getTypeRef(), myFunctionId);
	}

	@NotNull
	public ThreadMirror thread()
	{
		return myThreadMirror;
	}

	@NotNull
	public Value thisObject()
	{
		try
		{
			return StackFrame_GetThis.process(vm, myThreadMirror, this).value;
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Override
	public int id()
	{
		return myFrameID;
	}

	@Nullable
	public Value localOrParameterValue(LocalVariableOrParameterMirror mirror)
	{
		try
		{
			StackFrame_GetValues process = StackFrame_GetValues.process(vm, myThreadMirror, this, mirror);
			return process.values[0];
		}
		catch(JDWPException e)
		{
			if(e.errorCode == JDWP.Error.ABSENT_INFORMATION)
			{
				return null;
			}
			throw e.asUncheckedException();
		}
	}

	@Nullable
	public Value[] localOrParameterValues(LocalVariableOrParameterMirror... mirror)
	{
		try
		{
			StackFrame_GetValues process = StackFrame_GetValues.process(vm, myThreadMirror, this, mirror);
			return process.values;
		}
		catch(JDWPException e)
		{
			if(e.errorCode == JDWP.Error.ABSENT_INFORMATION)
			{
				return null;
			}
			throw e.asUncheckedException();
		}
	}

	public void setLocalOrParameterValues(@NotNull ImmutablePair<LocalVariableOrParameterMirror, Value<?>>... pairs)
	{
		try
		{
			StackFrame_SetValues.process(vm, myThreadMirror, this, pairs);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}
}

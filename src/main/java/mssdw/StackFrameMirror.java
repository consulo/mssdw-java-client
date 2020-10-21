package mssdw;

import javax.annotation.Nonnull;

import mssdw.protocol.StackFrame_GetArgumentValue;
import mssdw.protocol.StackFrame_GetLocalValue;
import mssdw.protocol.StackFrame_GetThis;

import javax.annotation.Nullable;

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

	@Nonnull
	public MethodMirror getMethod()
	{
		return new MethodMirror(virtualMachine(), getTypeRef(), myFunctionId);
	}

	@Nonnull
	public ThreadMirror thread()
	{
		return myThreadMirror;
	}

	@Nonnull
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
	public Value localValue(LocalVariableMirror mirror)
	{
		try
		{
			StackFrame_GetLocalValue process = StackFrame_GetLocalValue.process(vm, myThreadMirror, this, mirror);
			return process.value;
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
	public Value argumentValue(MethodParameterMirror mirror)
	{
		try
		{
			StackFrame_GetArgumentValue process = StackFrame_GetArgumentValue.process(vm, myThreadMirror, this, mirror);
			return process.value;
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
}

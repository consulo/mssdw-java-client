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
public class StackFrameMirror extends MirrorImpl implements Locatable, MirrorWithId
{
	public enum StackFrameFlags
	{
		NONE,
		DEBUGGER_INVOKE,
		NATIVE_TRANSITION
	}

	private final ThreadMirror myThreadMirror;
	private final int myFrameID;
	private final Location myLocation;
	private final StackFrameFlags myFlags;

	public StackFrameMirror(VirtualMachine aVm, ThreadMirror threadMirror, int frameID, Location location, StackFrameFlags flags)
	{
		super(aVm);
		myThreadMirror = threadMirror;
		myFrameID = frameID;
		myLocation = location;
		myFlags = flags;
	}

	public StackFrameFlags flags()
	{
		return myFlags;
	}

	@NotNull
	@Override
	public Location location()
	{
		return myLocation;
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
		// native methods ill throw absent information
		if(flags() == StackFrameFlags.NATIVE_TRANSITION)
		{
			return null;
		}
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
		// native methods ill throw absent information
		if(flags() == StackFrameFlags.NATIVE_TRANSITION)
		{
			return null;
		}
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
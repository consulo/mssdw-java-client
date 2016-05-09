package mssdw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import mssdw.protocol.Method_GetCustomAttributes;
import mssdw.protocol.Method_GetInfo;
import mssdw.protocol.Method_GetLocalsInfo;
import mssdw.protocol.Method_GetName;
import mssdw.protocol.Method_GetParamInfo;
import mssdw.protocol.VirtualMachine_InvokeMethod;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class MethodMirror extends CustomAttributeMirrorOwner implements MirrorWithId, ModifierOwner
{
	private TypeMirror myDeclarationType;
	private Method_GetParamInfo myParamInfo;
	private Method_GetInfo myInfo;
	private Method_GetLocalsInfo myLocalsInfo;

	private TypeRef myTypeRef;

	public MethodMirror(@NotNull VirtualMachine aVm, @NotNull TypeRef typeRef, int id)
	{
		super(aVm, id);
		myTypeRef = typeRef;
	}

	public TypeRef getTypeRef()
	{
		return myTypeRef;
	}

	public Method_GetParamInfo paramInfo()
	{
		if(myParamInfo != null)
		{
			return myParamInfo;
		}
		try
		{
			return myParamInfo = Method_GetParamInfo.process(vm, this);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	private Method_GetInfo info()
	{
		if(myInfo != null)
		{
			return myInfo;
		}
		try
		{
			return myInfo = Method_GetInfo.process(vm, this);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	private Method_GetLocalsInfo localsInfo(StackFrameMirror stackFrameMirror)
	{
		if(myLocalsInfo != null)
		{
			return myLocalsInfo;
		}
		try
		{
			return myLocalsInfo = Method_GetLocalsInfo.process(vm, this, stackFrameMirror);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	public MethodParameterMirror[] parameters()
	{
		return paramInfo().parameters;
	}

	public int genericParameterCount()
	{
		return paramInfo().genericParameterCount;
	}


	@Nullable
	public TypeMirror returnType()
	{
		TypeRef returnType = paramInfo().returnType;
		if(returnType == null)
		{
			return null;
		}
		return new TypeMirror(virtualMachine(), paramInfo().returnType);
	}

	@Nullable
	public Value<?> invoke(@NotNull StackFrameMirror stackFrameMirror, @Nullable Value<?> thisObject, Value<?>... arguments)
	{
		if(arguments.length != parameters().length)
		{
			throw new IllegalArgumentException("Wrong count of arguments");
		}
		try
		{
			List<Value<?>> list = new ArrayList<Value<?>>(arguments.length + 1);
			if(thisObject != null)
			{
				list.add(thisObject);
			}
			Collections.addAll(list, arguments);
			return VirtualMachine_InvokeMethod.process(vm, stackFrameMirror, this, list).getValue();
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@NotNull
	public LocalVariableMirror[] locals(@NotNull StackFrameMirror stackFrameMirror)
	{
		try
		{
			LocalVariableMirror[] locals = Method_GetLocalsInfo.process(vm, this, stackFrameMirror).localVariables;
			List<LocalVariableMirror> localVariableMirrors = new ArrayList<LocalVariableMirror>(locals.length);
			for(LocalVariableMirror local : locals)
			{
				localVariableMirrors.add(local);
			}
			return localVariableMirrors.toArray(new LocalVariableMirror[localVariableMirrors.size()]);
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Override
	protected CustomAttributeMirror[] customAttributesImpl() throws JDWPException
	{
		return Method_GetCustomAttributes.process(vm, this).customAttributeMirrors;
	}

	@NotNull
	@Override
	protected String nameImpl() throws JDWPException
	{
		return Method_GetName.process(vm, this).name;
	}

	@NotNull
	public TypeMirror declaringType()
	{
		if(myDeclarationType != null)
		{
			return myDeclarationType;
		}

		return myDeclarationType = new TypeMirror(virtualMachine(), getTypeRef());
	}

	@Override
	public boolean isStatic()
	{
		return (info().attributes & MethodAttributes.Static) == MethodAttributes.Static;
	}
}

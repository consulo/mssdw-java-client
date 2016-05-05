package mssdw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mssdw.protocol.AppDomain_CreateBoxValue;
import mssdw.protocol.AppDomain_CreateString;
import mssdw.protocol.AppDomain_GetAssemblies;
import mssdw.protocol.AppDomain_GetCorlib;
import mssdw.protocol.AppDomain_GetEntryAssembly;
import mssdw.protocol.AppDomain_GetFriendlyName;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class AppDomainMirror extends MirrorWithIdAndName
{
	private AssemblyMirror myEntryAssemblyMirror;
	private AssemblyMirror myCorlibAssemblyMirror;
	private AssemblyMirror[] myAssemblyMirrors;

	public AppDomainMirror(@NotNull VirtualMachine aVm, int aRef)
	{
		super(aVm, aRef);
	}

	@NotNull
	public StringValueMirror createString(@NotNull String str)
	{
		try
		{
			return AppDomain_CreateString.process(vm, this, str).value;
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@NotNull
	public ObjectValueMirror createBoxValue(int tag, @NotNull Number value)
	{
		try
		{
			return AppDomain_CreateBoxValue.process(vm, this, tag, value).value;
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}

	@Nullable
	public AssemblyMirror entryAssembly()
	{
		if(myEntryAssemblyMirror == null)
		{
			try
			{
				myEntryAssemblyMirror = AppDomain_GetEntryAssembly.process(vm, this).assembly;
			}
			catch(JDWPException e)
			{
				throw e.asUncheckedException();
			}
		}
		return myEntryAssemblyMirror;
	}

	@NotNull
	public AssemblyMirror corlibAssembly()
	{
		if(myCorlibAssemblyMirror == null)
		{
			try
			{
				myCorlibAssemblyMirror = AppDomain_GetCorlib.process(vm, this).assembly;
			}
			catch(JDWPException e)
			{
				throw e.asUncheckedException();
			}
		}
		return myCorlibAssemblyMirror;
	}

	@NotNull
	public AssemblyMirror[] assemblies()
	{
		if(myAssemblyMirrors == null)
		{
			try
			{
				myAssemblyMirrors = AppDomain_GetAssemblies.process(vm, this).assemblies;
			}
			catch(JDWPException e)
			{
				throw e.asUncheckedException();
			}
		}
		return myAssemblyMirrors;
	}

	@NotNull
	@Override
	protected String nameImpl() throws JDWPException
	{
		return AppDomain_GetFriendlyName.process(vm, this).name;
	}
}
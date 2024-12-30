package mssdw;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 23.07.2015
 */
public abstract class CustomAttributeMirrorOwner extends MirrorWithIdAndName implements ModifierOwner
{
	private String[] myCustomAttributeMirrors;

	public CustomAttributeMirrorOwner(@Nonnull VirtualMachine aVm, int id)
	{
		super(aVm, id);
	}

	protected abstract String[] customAttributesImpl() throws JDWPException;

	@Nonnull
	@Override
	public final String[] customAttributes()
	{
		if(myCustomAttributeMirrors != null)
		{
			return myCustomAttributeMirrors;
		}
		try
		{
			return myCustomAttributeMirrors = customAttributesImpl();
		}
		catch(JDWPException e)
		{
			throw e.asUncheckedException();
		}
	}
}

package mssdw;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public interface ModifierOwner
{
	@Nonnull
	String[] customAttributes();

	boolean isStatic();

	boolean isAbstract();
}

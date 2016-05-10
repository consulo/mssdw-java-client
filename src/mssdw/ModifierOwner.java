package mssdw;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public interface ModifierOwner
{
	@NotNull
	String[] customAttributes();

	boolean isStatic();

	boolean isAbstract();
}

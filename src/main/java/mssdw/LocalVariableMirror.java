package mssdw;

/**
 * @author VISTALL
 * @since 12.04.14
 */
public class LocalVariableMirror extends LocalVariableOrParameterMirror
{
	public LocalVariableMirror(VirtualMachineImpl vm, int i, TypeRef type, String name)
	{
		super(vm, i, type, name);
	}
}

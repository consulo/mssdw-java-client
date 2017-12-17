package mssdw;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class MethodParameterMirror extends LocalVariableOrParameterMirror
{
	public MethodParameterMirror(VirtualMachineImpl vm, int i, TypeRef type, String name)
	{
		super(vm, i, type, name);
	}
}

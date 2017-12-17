package mssdw;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public interface ValueVisitor
{
	class Adapter implements ValueVisitor
	{
		@Override
		public void visitObjectValue(@NotNull ObjectValueMirror value)
		{
		}

		@Override
		public void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue)
		{
		}

		@Override
		public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
		{
		}

		@Override
		public void visitNoObjectValue(@NotNull NoObjectValueMirror value)
		{
		}

		@Override
		public void visitArrayValue(@NotNull ArrayValueMirror value)
		{
		}

		@Override
		public void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue)
		{
		}

		@Override
		public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
		{
		}

		@Override
		public void visitTypeValue(@NotNull TypeValueMirror typeValueMirror, @NotNull TypeMirror mainValue)
		{
		}

		@Override
		public void visitStructValue(@NotNull StructValueMirror mirror)
		{
		}

		@Override
		public void visitEnumValue(@NotNull EnumValueMirror mirror)
		{
		}
	}

	void visitObjectValue(@NotNull ObjectValueMirror value);

	void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue);

	void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue);

	void visitNoObjectValue(@NotNull NoObjectValueMirror value);

	void visitArrayValue(@NotNull ArrayValueMirror value);

	void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue);

	void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue);

	void visitTypeValue(@NotNull TypeValueMirror typeValueMirror, @NotNull TypeMirror mainValue);

	void visitStructValue(@NotNull StructValueMirror mirror);

	void visitEnumValue(@NotNull EnumValueMirror mirror);
}
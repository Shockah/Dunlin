package pl.shockah.dunlin.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public abstract class ArgumentSet {
	void parse(@Nonnull String textInput) {
	}
	
	public void onUnknownArgument(@Nonnull String argumentName, @Nullable String rawValue) {
		throw new IllegalArgumentException(String.format("Unknown argument %s.", argumentName));
	}
	
	public boolean isValueValid(@Nonnull Field field, @Nullable Object value) {
		return true;
	}
	
	public void finalValidation() {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Argument {
		String value() default "";
		boolean isDefault() default false;
		ArgumentType type() default ArgumentType.Auto;
	}
	
	public enum ArgumentType {
		@Deprecated Auto, Integer, Decimal, Range, String, Bool, Enum;
	}
}
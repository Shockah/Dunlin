package pl.shockah.dunlin.commands;

import java.lang.reflect.Field;

public abstract class ArgumentSet {
	void parse(String textInput) {
		
	}
	
	public void onUnknownArgument(String argumentName, String rawValue) {
		throw new IllegalArgumentException(String.format("Unknown argument %s.", argumentName));
	}
	
	public boolean isValueValid(Field field, Object value) {
		return true;
	}
	
	public void finalValidation() {
	}
	
	public static @interface Argument {
		String value() default "";
		boolean isDefault() default false;
		ArgumentType type() default ArgumentType.Auto;
	}
	
	public static enum ArgumentType {
		@Deprecated Auto, Integer, Decimal, Range, String, Bool, Enum;
	}
}
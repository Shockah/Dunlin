package pl.shockah.dunlin.commands;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.shockah.util.Box;
import pl.shockah.util.UnexpectedException;

public class ArgumentSetParser<T extends ArgumentSet> {
	public static final String SPLIT_PATTERN = "(?<=\\s+)(?!\\s)|(?<!\\s)(?=\\s+)";
	public static final Pattern ARGUMENT_NAME_PATTERN = Pattern.compile("\\-(\\S+)");
	
	public final Class<T> clazz;
	
	public ArgumentSetParser(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public T parse(String textInput) {
		try {
			T argumentSet = clazz.newInstance();
			ArgumentSetParseProcess process = new ArgumentSetParseProcess(argumentSet);
			
			String[] split = textInput.split(SPLIT_PATTERN);
			if (split.length != 0) {
				int offset = split[0].matches("\\s+") ? 1 : 0;
				for (int i = offset; i < split.length; i += 2) {
					Matcher m = ARGUMENT_NAME_PATTERN.matcher(split[i]);
					if (m.find()) {
						String argumentName = m.group(1);
						
						Box<Integer> index = new Box<>(i + 1);
						String rawValue = parseRawValue(split, index);
						i = index.value - 1;
						
						ArgumentSetParseProcess.Argument argument = process.nameToArgument.get(argumentName);
						if (argument == null) {
							argumentSet.onUnknownArgument(argumentName, rawValue);
							continue;
						}
					}
					
					if (process.defaultArgument != null) {
						Box<Integer> index = new Box<>(i + 1);
						String rawValue = parseRawValue(split, index);
						putArgumentValue(process.defaultArgument, rawValue);
						break;
					}
				}
			}
			
			argumentSet.finalValidation();
			return argumentSet;
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	protected String parseRawValue(String[] split, Box<Integer> index) {
		StringBuilder sb = new StringBuilder();
		String[] copy = Arrays.copyOf(split, split.length);
		
		if (copy[index.value].charAt(0) == '"') {
			copy[index.value] = copy[index.value].substring(1);
			
			while (index.value < copy.length) {
				String s = copy[index.value];
				index.value++;
				if (s.endsWith("\"") && !s.endsWith("\\\"")) {
					sb.append(s.substring(0, s.length() - 1));
					return sb.toString();
				} else {
					if (ARGUMENT_NAME_PATTERN.matcher(s).find())
						return sb.toString();
					sb.append(s);
				}
			}
		}
		
		return sb.toString();
	}
	
	protected void putArgumentValue(ArgumentSetParseProcess.Argument argument, String rawValue) {
		Class<?> clazz = argument.field.getType();
		switch (argument.type) {
			case Bool: {
				boolean value = Boolean.parseBoolean(rawValue);
				if (clazz == boolean.class) {
					putArgumentValueInternal(argument, value);
					return;
				} else if (clazz == Boolean.class) {
					putArgumentValueInternal(argument, (Boolean)value);
					return;
				}
			} break;
			case Integer: {
				BigInteger value = new BigInteger(rawValue);
				if (clazz == BigInteger.class) {
					putArgumentValueInternal(argument, value);
					return;
				} else if (clazz == int.class) {
					putArgumentValueInternal(argument, value.intValueExact());
					return;
				} else if (clazz == long.class) {
					putArgumentValueInternal(argument, value.longValueExact());
					return;
				}
			} break;
			case Decimal: {
				BigDecimal value = new BigDecimal(rawValue);
				if (clazz == BigDecimal.class) {
					putArgumentValueInternal(argument, value);
					return;
				} else if (clazz == float.class) {
					putArgumentValueInternal(argument, value.floatValue());
					return;
				} else if (clazz == long.class) {
					putArgumentValueInternal(argument, value.doubleValue());
					return;
				}
			} break;
			case String: {
				if (clazz == String.class) {
					putArgumentValueInternal(argument, rawValue);
					return;
				}
			} break;
			case Enum: {
				for (Object obj : clazz.getEnumConstants()) {
					Enum<?> enumConst = (Enum<?>)obj;
					if (enumConst.name().equalsIgnoreCase(rawValue)) {
						putArgumentValueInternal(argument, enumConst);
						return;
					}
				}
				
				try {
					int ordinal = Integer.parseInt(rawValue);
					for (Object obj : clazz.getEnumConstants()) {
						Enum<?> enumConst = (Enum<?>)obj;
						if (enumConst.ordinal() == ordinal) {
							putArgumentValueInternal(argument, enumConst);
							return;
						}
					}
				} catch (NumberFormatException e2) {
				}
			} break;
			default:
				break;
		}
		throw new IllegalArgumentException(String.format("Cannot handle argument %s of type %s.", argument.name, argument.type.name()));
	}
	
	private void putArgumentValueInternal(ArgumentSetParseProcess.Argument argument, Object value) {
		if (!argument.argumentSet.isValueValid(argument.field, value))
			throw new IllegalArgumentException(String.format("Invalid value for argument %s.", argument.name));
		argument.put(value);
	}
}
package io.shockah.dunlin.complexcommandparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.shockah.dunlin.commands.CommandParseException;

public final class ComplexParserArgument {
	public static final Pattern RANGE_PATTERN = Pattern.compile("^([0-9]+)\\-([0-9]+)$");
	public static final Pattern INT_PLUS_PATTERN = Pattern.compile("^([0-9]+)\\+$");
	public static final Pattern INT_MINUS_PATTERN = Pattern.compile("^([0-9]+)\\-$");
	public static final Pattern GREATER_OR_EQUAL_PATTERN = Pattern.compile("^\\>\\=([0-9]+)$");
	public static final Pattern LESSER_OR_EQUAL_PATTERN = Pattern.compile("^\\<\\=([0-9]+)$");
	public static final Pattern GREATER_PATTERN = Pattern.compile("^\\>([0-9]+)$");
	public static final Pattern LESSER_PATTERN = Pattern.compile("^\\<([0-9]+)$");
	
	public final Type type;
	public final boolean multiUse;
	public final String[] names;
	
	public static ComplexParserArgument make(Type type, boolean multiUse, String name, String... moreNames) {
		String[] names = new String[moreNames.length + 1];
		names[0] = name;
		for (int i = 0; i < moreNames.length; i++) {
			names[i + 1] = moreNames[i];
		}
		return new ComplexParserArgument(type, multiUse, names);
	}
	
	protected ComplexParserArgument(Type type, boolean multiUse, String[] names) {
		this.type = type;
		this.multiUse = multiUse;
		this.names = names;
	}
	
	public ParseResult parse(String name, String str) throws CommandParseException {
		if (str.length() == 0 && type != Type.Flag)
			throw new CommandParseException(String.format("Empty value for key %s.", name));
		
		switch (type) {
			case Flag:
				return new ParseResult(this, true, str);
			case Bool: {
				String[] split = str.split("\\s");
				String sval = split[0];
				String svalLower = sval.toLowerCase();
				
				Boolean b = null;
				try {
					b = Boolean.getBoolean(svalLower);
				} catch (Exception e1) {
					if (svalLower.equals("t") || svalLower.equals("yes") || svalLower.equals("y")) {
						b = true;
					} else if (svalLower.equals("f") || svalLower.equals("no") || svalLower.equals("n")) {
						b = false;
					} else {
						try {
							int i = Integer.parseInt(sval);
							if (i == 1)
								b = true;
							else if (i == 0)
								b = false;
							else
								throw new CommandParseException(String.format("Cannot parse '%s' as bool argument %s.", sval, name));
						} catch (Exception e2) {
							throw new CommandParseException(String.format("Cannot parse '%s' as bool argument %s.", sval, name));
						}
					}
				}
				return new ParseResult(this, b, str.substring(sval.length() + 1));
			}
			case Integer: {
				String[] split = str.split("\\s");
				String sval = split[0];
				
				try {
					return new ParseResult(this, new BigInteger(sval), str.substring(sval.length() + 1));
				} catch (Exception e) {
					throw new CommandParseException(String.format("Cannot parse '%s' as integer argument %s.", sval, name));
				}
			}
			case Decimal: {
				String[] split = str.split("\\s");
				String sval = split[0];
				
				try {
					return new ParseResult(this, new BigDecimal(sval), str.substring(sval.length() + 1));
				} catch (Exception e) {
					throw new CommandParseException(String.format("Cannot parse '%s' as decimal argument %s.", sval, name));
				}
			}
			case Range: {
				String[] split = str.split("\\s");
				String sval = split[0];
				return new ParseResult(this, parseRange(name, sval), str.substring(sval.length() + 1));
			}
			case String: {
				StringBuilder sb = new StringBuilder();
				Character lastDelimiter = null;
				
				while (str != null && !str.isEmpty()) {
					String[] split = str.split("\\s");
					String word = split[0];
					
					if (word.startsWith("\\")) {
						if (lastDelimiter != null)
							sb.append(lastDelimiter);
						sb.append(word.substring(1));
						lastDelimiter = split.length == 1 ? null : str.charAt(word.length());
						str = str.substring(word.length() + 1);
					} else if (word.startsWith("-")) {
						return new ParseResult(this, sb.toString(), str);
					} else {
						if (lastDelimiter != null)
							sb.append(lastDelimiter);
						sb.append(word);
						lastDelimiter = split.length == 1 ? null : str.charAt(word.length());
						str = str.substring(word.length() + 1);
					}
				}
				
				if (lastDelimiter != null)
					sb.append(lastDelimiter);
				return new ParseResult(this, sb.toString(), str);
			}
		}
		
		return null;
	}
	
	private Range parseRange(String name, String val) throws CommandParseException {
		Matcher m;
		
		m = RANGE_PATTERN.matcher(val);
		if (m.find())
			return new Range(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		
		m = INT_PLUS_PATTERN.matcher(val);
		if (m.find())
			return new Range(Integer.parseInt(m.group(1)), null);
		
		m = INT_MINUS_PATTERN.matcher(val);
		if (m.find())
			return new Range(null, Integer.parseInt(m.group(1)));
		
		m = GREATER_OR_EQUAL_PATTERN.matcher(val);
		if (m.find())
			return new Range(Integer.parseInt(m.group(1)), null);
		
		m = LESSER_OR_EQUAL_PATTERN.matcher(val);
		if (m.find())
			return new Range(null, Integer.parseInt(m.group(1)));
		
		m = GREATER_PATTERN.matcher(val);
		if (m.find())
			return new Range(Integer.parseInt(m.group(1)) + 1, null);
		
		m = LESSER_PATTERN.matcher(val);
		if (m.find())
			return new Range(null, Integer.parseInt(m.group(1)) - 1);
		
		try {
			int num = Integer.parseInt(val);
			return new Range(num, num);
		} catch (Exception e) {
			throw new CommandParseException(String.format("Cannot parse '%s' as range argument %s.", val, name));
		}
	}
	
	public static enum Type {
		Flag, Bool, Integer, Decimal, Range, String;
	}
	
	public static final class ParseResult {
		public final ComplexParserArgument argument;
		public final Object value;
		public final String stringLeft;
		
		public ParseResult(ComplexParserArgument argument, Object value, String stringLeft) {
			this.argument = argument;
			this.value = value;
			this.stringLeft = stringLeft;
		}
	}
}
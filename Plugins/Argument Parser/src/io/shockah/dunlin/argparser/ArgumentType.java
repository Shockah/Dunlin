package io.shockah.dunlin.argparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import io.shockah.dunlin.commands.CommandParseException;

public enum ArgumentType {
	Bool, BoolList(true),
	Number, NumberList(true),
	Decimal, DecimalList(true),
	String, StringList(true),
	IntRange, IntRangeList(true),
	DoubleRange, DoubleRangeList(true);
	
	private static final List<String> trueValues = Arrays.asList(new String[] { "1", "true", "t", "yes", "y" });
	private static final List<String> falseValues = Arrays.asList(new String[] { "0", "false", "f", "no", "n" });
	
	public final boolean isListType;
	
	private ArgumentType() {
		this(false);
	}
	
	private ArgumentType(boolean isListType) {
		this.isListType = isListType;
	}
	
	public Object parse(String input) throws CommandParseException {
		switch (this) {
			case Bool: case BoolList: {
				input = input.toLowerCase();
				if (trueValues.contains(input))
					return true;
				else if (falseValues.contains(input))
					return false;
				else
					throw new CommandParseException();
			}
			case Number: case NumberList: {
				try {
					return new BigInteger(input);
				} catch (Exception e) {
					throw new CommandParseException();
				}
			}
			case Decimal: case DecimalList: {
				try {
					return new BigDecimal(input);
				} catch (Exception e) {
					throw new CommandParseException();
				}
			}
			case String: case StringList: {
				return input;
			}
			case IntRange: case IntRangeList: {
				//TODO:
				throw new UnsupportedOperationException();
			}
			case DoubleRange: case DoubleRangeList: {
				//TODO:
				throw new UnsupportedOperationException();
			}
		}
		throw new IllegalStateException();
	}
}
package io.shockah.dunlin.complexcommandparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class ComplexParserResult {
	public final ComplexParser parser;
	protected final List<ComplexParserArgument.ParseResult> values = new LinkedList<>();
	
	public ComplexParserResult(ComplexParser parser) {
		this.parser = parser;
	}
	
	public int getInt(String name) {
		return getInt(name, false);
	}
	
	public int consumeInt(String name) {
		return getInt(name, true);
	}
	
	private int getInt(String name, boolean consume) {
		BigInteger bi = (BigInteger)get(name, consume);
		return bi.intValue();
	}
	
	public long getLong(String name) {
		return getLong(name, false);
	}
	
	public long consumeLong(String name) {
		return getLong(name, true);
	}
	
	private long getLong(String name, boolean consume) {
		BigInteger bi = (BigInteger)get(name, consume);
		return bi.longValue();
	}
	
	public BigInteger getBigInteger(String name) {
		return getBigInteger(name, false);
	}
	
	public BigInteger consumeBigInteger(String name) {
		return getBigInteger(name, true);
	}
	
	private BigInteger getBigInteger(String name, boolean consume) {
		return (BigInteger)get(name, consume);
	}
	
	public float getFloat(String name) {
		return getFloat(name, false);
	}
	
	public float consumeFloat(String name) {
		return getFloat(name, true);
	}
	
	private float getFloat(String name, boolean consume) {
		BigDecimal bd = (BigDecimal)get(name, consume);
		return bd.floatValue();
	}
	
	public double getDouble(String name) {
		return getDouble(name, false);
	}
	
	public double consumeDouble(String name) {
		return getDouble(name, true);
	}
	
	private double getDouble(String name, boolean consume) {
		BigDecimal bd = (BigDecimal)get(name, consume);
		return bd.doubleValue();
	}
	
	public BigDecimal getBigDecimal(String name) {
		return getBigDecimal(name, false);
	}
	
	public BigDecimal consumeBigDecimal(String name) {
		return getBigDecimal(name, true);
	}
	
	private BigDecimal getBigDecimal(String name, boolean consume) {
		return (BigDecimal)get(name, consume);
	}
	
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}
	
	public boolean consumeBoolean(String name) {
		return getBoolean(name, true);
	}
	
	private boolean getBoolean(String name, boolean consume) {
		return (Boolean)get(name, consume);
	}
	
	public String getString(String name) {
		return getString(name, false);
	}
	
	public String consumeString(String name) {
		return getString(name, true);
	}
	
	private String getString(String name, boolean consume) {
		return (String)get(name, consume);
	}
	
	public Range getRange(String name) {
		return getRange(name, false);
	}
	
	public Range consumeRange(String name) {
		return getRange(name, true);
	}
	
	private Range getRange(String name, boolean consume) {
		return (Range)get(name, consume);
	}
	
	public Object get(String name) {
		return get(name, false);
	}
	
	public Object consume(String name) {
		return get(name, false);
	}
	
	public Object get(String name, boolean consume) {
		for (ComplexParserArgument.ParseResult val : values) {
			for (String argName : val.argument.names) {
				if (argName.equalsIgnoreCase(name)) {
					Object ret = val.value;
					if (consume)
						values.remove(val);
					return ret;
				}
			}
		}
		return null;
	}
}
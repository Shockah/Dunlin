package pl.shockah.dunlin.commands;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pl.shockah.util.UnexpectedException;

public final class ArgumentSetParseProcess {
	public final ArgumentSet argumentSet;
	
	protected final Map<Field, Argument> modifiableFieldToArgument = new HashMap<>();
	protected final Map<String, Argument> modifiableNameToArgument = new HashMap<>();
	
	public final Map<Field, Argument> fieldToArgument = Collections.unmodifiableMap(modifiableFieldToArgument);
	public final Map<String, Argument> nameToArgument = Collections.unmodifiableMap(modifiableNameToArgument);
	public final Argument defaultArgument;
	
	@SuppressWarnings("deprecation")
	public ArgumentSetParseProcess(ArgumentSet argumentSet) {
		this.argumentSet = argumentSet;
		Argument defaultArgument = null;
		
		for (Field field : argumentSet.getClass().getFields()) {
			ArgumentSet.Argument argumentAnnotation = field.getAnnotation(ArgumentSet.Argument.class);
			if (argumentAnnotation == null)
				continue;
			
			String argumentName = argumentAnnotation.value();
			if (argumentName.isEmpty())
				argumentName = field.getName();
			argumentName = argumentName.toLowerCase();
			
			ArgumentSet.ArgumentType argumentType = argumentAnnotation.type();
			if (argumentType == ArgumentSet.ArgumentType.Auto)
				argumentType = findType(field);
			
			Argument argument;
			if (Collection.class.isAssignableFrom(field.getType()))
				argument = new CollectionArgument(field, argumentSet, argumentName, argumentType);
			else
				argument = new ValueArgument(field, argumentSet, argumentName, argumentType);
			
			if (argumentAnnotation.isDefault()) {
				if (defaultArgument != null)
					throw new IllegalArgumentException("Cannot have multiple default arguments.");
				defaultArgument = argument;
			} else {
				modifiableFieldToArgument.put(field, argument);
				modifiableNameToArgument.put(argumentName, argument);
			}
		}
		
		this.defaultArgument = defaultArgument;
	}
	
	private ArgumentSet.ArgumentType findType(Field field) {
		Class<?> clazz = field.getType();
		if (clazz == String.class)
			return ArgumentSet.ArgumentType.String;
		else if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class || clazz == BigInteger.class)
			return ArgumentSet.ArgumentType.Integer;
		else if (clazz == float.class || clazz == Float.class || clazz == double.class || clazz == Double.class || clazz == BigDecimal.class)
			return ArgumentSet.ArgumentType.Decimal;
		else if (clazz == boolean.class || clazz == Boolean.class)
			return ArgumentSet.ArgumentType.Bool;
		else if (Enum.class.isAssignableFrom(clazz))
			return ArgumentSet.ArgumentType.Enum;
		throw new IllegalArgumentException(String.format("Couldn't automatically find argument type for %s.%s.", field.getDeclaringClass().getName(), field.getName()));
	}
	
	protected static abstract class Argument {
		public final Field field;
		public final ArgumentSet argumentSet;
		public final String name;
		public final ArgumentSet.ArgumentType type;
		
		public Argument(Field field, ArgumentSet argumentSet, String name, ArgumentSet.ArgumentType type) {
			this.field = field;
			this.argumentSet = argumentSet;
			this.name = name;
			this.type = type;
			field.setAccessible(true);
		}
		
		public abstract void put(Object value);
	}
	
	protected static class ValueArgument extends Argument {
		public ValueArgument(Field field, ArgumentSet argumentSet, String name, ArgumentSet.ArgumentType type) {
			super(field, argumentSet, name, type);
		}
		
		@Override
		public void put(Object value) {
			try {
				field.set(argumentSet, value);
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
	}
	
	protected static class CollectionArgument extends Argument {
		private Collection<Object> collection = null;
		
		public CollectionArgument(Field field, ArgumentSet argumentSet, String name, ArgumentSet.ArgumentType type) {
			super(field, argumentSet, name, type);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void put(Object value) {
			if (collection == null) {
				try {
					collection = (Collection<Object>)field.get(argumentSet);
					if (collection == null) {
						collection = new ArrayList<>();
						field.set(argumentSet, collection);
					} else {
						collection.clear();
					}
				} catch (Exception e) {
					throw new UnexpectedException(e);
				}
			}
			collection.add(value);
		}
	}
}
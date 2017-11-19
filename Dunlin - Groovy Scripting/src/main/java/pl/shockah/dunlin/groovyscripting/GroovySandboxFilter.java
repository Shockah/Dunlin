package pl.shockah.dunlin.groovyscripting;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroovySandboxFilter extends AbstractGroovySandbox {
	@Nonnull protected final List<Package> whitelistedPackages = new ArrayList<>();
	@Nonnull protected final List<Class<?>> whitelistedClasses = new ArrayList<>();
	@Nonnull protected final List<Method> whitelistedMethods = new ArrayList<>();

	@Nonnull protected final List<Field> blacklistedFields = new ArrayList<>();

	protected boolean isPackageAllowed(@Nonnull Package pkg) {
		return whitelistedPackages.contains(pkg);
	}

	protected boolean isClassAllowed(@Nonnull Class<?> clazz) {
		if (whitelistedClasses.contains(clazz))
			return true;
		if (isPackageAllowed(clazz.getPackage()))
			return true;

		if (clazz.getSuperclass() != null && isClassAllowed(clazz.getSuperclass()))
			return true;
		if (clazz.getDeclaringClass() != null && isClassAllowed(clazz.getDeclaringClass()))
			return true;

		return false;
	}

	protected boolean isMethodAllowed(@Nonnull Method method) {
		if (whitelistedMethods.contains(method))
			return true;
		if (isClassAllowed(method.getDeclaringClass()))
			return true;
		if (method.getDeclaringClass().getInterfaces().length != 0) {
			for (Method whitelistedMethod : whitelistedMethods) {
				if (!whitelistedMethod.getDeclaringClass().isInterface())
					continue;
				if (!Arrays.asList(method.getDeclaringClass().getInterfaces()).contains(whitelistedMethod.getDeclaringClass()))
					continue;
				if (methodMatchesWithTypes(whitelistedMethod, method.getName(), method.getParameterTypes()))
					return true;
			}
		}
		return false;
	}

	protected boolean isFieldAllowed(@Nonnull Field field) {
		if (blacklistedFields.contains(field))
			return false;
		return true;
	}

	private boolean methodMatches(@Nonnull Method method, @Nonnull String methodName, @Nonnull Object... args) {
		if (!method.getName().equals(methodName))
			return false;
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters.length != args.length)
			return false;

		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].isInstance(args[i]))
				continue;
			if (parameters[i] == boolean.class && args[i] instanceof Boolean)
				continue;
			if (parameters[i] == byte.class && args[i] instanceof Byte)
				continue;
			if (parameters[i] == short.class && args[i] instanceof Short)
				continue;
			if (parameters[i] == int.class && args[i] instanceof Integer)
				continue;
			if (parameters[i] == char.class && args[i] instanceof Character)
				continue;
			if (parameters[i] == float.class && args[i] instanceof Float)
				continue;
			if (parameters[i] == double.class && args[i] instanceof Double)
				continue;
			if (parameters[i] == long.class && args[i] instanceof Long)
				continue;
			return false;
		}

		return true;
	}

	private boolean methodMatchesWithTypes(@Nonnull Method method, @Nonnull String methodName, @Nonnull Class<?>... args) {
		if (!method.getName().equals(methodName))
			return false;
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters.length != args.length)
			return false;

		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].isInstance(args[i]))
				continue;
			if (parameters[i] == boolean.class && args[i] == Boolean.class)
				continue;
			if (parameters[i] == byte.class && args[i] == Byte.class)
				continue;
			if (parameters[i] == short.class && args[i] == Short.class)
				continue;
			if (parameters[i] == int.class && args[i] == Integer.class)
				continue;
			if (parameters[i] == char.class && args[i] == Character.class)
				continue;
			if (parameters[i] == float.class && args[i] == Float.class)
				continue;
			if (parameters[i] == double.class && args[i] == Double.class)
				continue;
			if (parameters[i] == long.class && args[i] == Long.class)
				continue;
			return false;
		}

		return true;
	}

	private List<Method> getMethods(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nonnull Object... args) {
		List<Method> methods = new ArrayList<>();

		for (Method method : clazz.getMethods()) {
			if (methodMatches(method, methodName, args))
				methods.add(method);
		}

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			methods.addAll(getMethods(interfaceClass, methodName, args));
		}

		return methods;
	}

	@Override
	public boolean isConstructorAllowed(@Nonnull Class<?> clazz, @Nonnull Object... args) {
		return isClassAllowed(clazz);
	}

	@Override
	public boolean isClassMethodAllowed(@Nonnull Class<?> clazz, @Nonnull String method, @Nonnull Object... args) {
		try {
			if (isClassAllowed(clazz))
				return true;
			return getMethods(clazz, method, args).stream().anyMatch(this::isMethodAllowed);
		} catch (Exception ignored) {
		}
		return super.isClassMethodAllowed(clazz, method, args);
	}

	@Override
	public boolean isInstanceMethodAllowed(@Nonnull Object obj, @Nonnull String method, @Nonnull Object... args) {
		try {
			if (isClassAllowed(obj.getClass()))
				return true;
			return getMethods(obj.getClass(), method, args).stream().anyMatch(this::isMethodAllowed);
		} catch (Exception ignored) {
		}
		return super.isInstanceMethodAllowed(obj, method, args);
	}

	@Override
	public boolean isClassFieldGetAllowed(@Nonnull Class<?> clazz, @Nonnull String field) {
		try {
			return isFieldAllowed(clazz.getField(field));
		} catch (Exception ignored) {
		}
		return super.isClassFieldGetAllowed(clazz, field);
	}

	@Override
	public boolean isClassFieldSetAllowed(@Nonnull Class<?> clazz, @Nonnull String field, @Nonnull Object value) {
		try {
			return isFieldAllowed(clazz.getField(field));
		} catch (Exception ignored) {
		}
		return super.isClassFieldSetAllowed(clazz, field, value);
	}

	@Override
	public boolean isInstanceFieldGetAllowed(@Nonnull Object obj, @Nonnull String field) {
		try {
			return isFieldAllowed(obj.getClass().getField(field));
		} catch (Exception ignored) {
		}
		return super.isInstanceFieldGetAllowed(obj, field);
	}

	@Override
	public boolean isInstanceFieldSetAllowed(@Nonnull Object obj, @Nonnull String field, @Nonnull Object value) {
		try {
			return isFieldAllowed(obj.getClass().getField(field));
		} catch (Exception ignored) {
		}
		return super.isInstanceFieldSetAllowed(obj, field, value);
	}
}
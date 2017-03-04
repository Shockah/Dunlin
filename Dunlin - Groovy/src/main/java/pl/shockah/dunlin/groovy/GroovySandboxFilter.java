package pl.shockah.dunlin.groovy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GroovySandboxFilter extends AbstractGroovySandbox {
	protected final List<Package> whitelistedPackages = new ArrayList<>();
	protected final List<Class<?>> whitelistedClasses = new ArrayList<>();
	protected final List<Method> whitelistedMethods = new ArrayList<>();
	protected final List<Field> whitelistedFields = new ArrayList<>();

	protected final List<Package> blacklistedPackages = new ArrayList<>();
	protected final List<Class<?>> blacklistedClasses = new ArrayList<>();
	protected final List<Method> blacklistedMethods = new ArrayList<>();
	protected final List<Field> blacklistedFields = new ArrayList<>();

	private boolean isPackageWhitelisted(Package pkg) {
		return whitelistedPackages.contains(pkg);
	}

	private boolean isClassWhitelisted(Class<?> clazz) {
		if (whitelistedClasses.contains(clazz))
			return true;
		if (isPackageWhitelisted(clazz.getPackage()))
			return true;

		clazz = clazz.getSuperclass();
		if (clazz != null)
			return isClassWhitelisted(clazz);

		return false;
	}

	private boolean isMethodWhitelisted(Method method) {
		if (whitelistedMethods.contains(method))
			return true;
		if (isClassWhitelisted(method.getDeclaringClass()))
			return true;
		return false;
	}

	private boolean isFieldWhitelisted(Field field) {
		if (whitelistedFields.contains(field))
			return true;
		if (isClassWhitelisted(field.getDeclaringClass()))
			return true;
		return false;
	}

	private boolean isPackageBlacklisted(Package pkg) {
		return blacklistedPackages.contains(pkg);
	}

	private boolean isClassBlacklisted(Class<?> clazz) {
		if (blacklistedClasses.contains(clazz))
			return true;
		if (isPackageBlacklisted(clazz.getPackage()))
			return true;

		clazz = clazz.getSuperclass();
		if (clazz != null)
			return isClassBlacklisted(clazz);

		return false;
	}

	private boolean isMethodBlacklisted(Method method) {
		if (blacklistedMethods.contains(method))
			return true;
		if (isClassBlacklisted(method.getDeclaringClass()))
			return true;
		return false;
	}

	private boolean isFieldBlacklisted(Field field) {
		if (blacklistedFields.contains(field))
			return true;
		if (isClassBlacklisted(field.getDeclaringClass()))
			return true;
		return false;
	}

	private Method getMethod(Class<?> clazz, String methodName, Object... args) throws NoSuchMethodException {
		L: for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(methodName))
				continue;
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != args.length)
				continue;

			for (int i = 0; i < parameters.length; i++) {
				if (!parameters[i].isInstance(args[i]))
					continue L;
			}
			return method;
		}

		throw new NoSuchMethodException();
	}

	@Override
	public boolean isConstructorAllowed(Class<?> clazz, Object... args) {
		if (isClassBlacklisted(clazz) && !isClassWhitelisted(clazz))
			return false;
		return super.isConstructorAllowed(clazz, args);
	}

	@Override
	public boolean isClassMethodAllowed(Class<?> clazz, String method, Object... args) {
		try {
			Method actualMethod = getMethod(clazz, method, args);
			if (isMethodBlacklisted(actualMethod) && !isMethodWhitelisted(actualMethod))
				return false;
		} catch (Exception e) {
		}
		return super.isClassMethodAllowed(clazz, method, args);
	}

	@Override
	public boolean isInstanceMethodAllowed(Object obj, String method, Object... args) {
		try {
			Method actualMethod = getMethod(obj.getClass(), method, args);
			if (isMethodBlacklisted(actualMethod) && !isMethodWhitelisted(actualMethod))
				return false;
		} catch (Exception e) {
		}
		return super.isInstanceMethodAllowed(obj, method, args);
	}

	public boolean isFieldAllowed(Field field) {
		if (isFieldBlacklisted(field) && !isFieldWhitelisted(field))
			return false;
		return true;
	}

	@Override
	public boolean isClassFieldGetAllowed(Class<?> clazz, String field) {
		try {
			return isFieldAllowed(clazz.getField(field));
		} catch (Exception e) {
		}
		return super.isClassFieldGetAllowed(clazz, field);
	}

	@Override
	public boolean isClassFieldSetAllowed(Class<?> clazz, String field, Object value) {
		try {
			return isFieldAllowed(clazz.getField(field));
		} catch (Exception e) {
		}
		return super.isClassFieldSetAllowed(clazz, field, value);
	}

	@Override
	public boolean isInstanceFieldGetAllowed(Object obj, String field) {
		try {
			return isFieldAllowed(obj.getClass().getField(field));
		} catch (Exception e) {
		}
		return super.isInstanceFieldGetAllowed(obj, field);
	}

	@Override
	public boolean isInstanceFieldSetAllowed(Object obj, String field, Object value) {
		try {
			return isFieldAllowed(obj.getClass().getField(field));
		} catch (Exception e) {
		}
		return super.isInstanceFieldSetAllowed(obj, field, value);
	}
}
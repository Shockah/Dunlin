package pl.shockah.dunlin.groovy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GroovySandboxFilter extends AbstractGroovySandbox {
	protected final List<Package> whitelistedPackages = new ArrayList<>();
	protected final List<Class<?>> whitelistedClasses = new ArrayList<>();
	protected final List<Method> whitelistedMethods = new ArrayList<>();

	protected final List<Field> blacklistedFields = new ArrayList<>();

	protected boolean isPackageAllowed(Package pkg) {
		return whitelistedPackages.contains(pkg);
	}

	protected boolean isClassAllowed(Class<?> clazz) {
		if (whitelistedClasses.contains(clazz))
			return true;
		if (isPackageAllowed(clazz.getPackage()))
			return true;

		clazz = clazz.getSuperclass();
		if (clazz != null)
			return isClassAllowed(clazz);

		return false;
	}

	protected boolean isMethodAllowed(Method method) {
		if (whitelistedMethods.contains(method))
			return true;
		if (isClassAllowed(method.getDeclaringClass()))
			return true;
		return false;
	}

	protected boolean isFieldAllowed(Field field) {
		if (blacklistedFields.contains(field))
			return false;
		return true;
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
		return isClassAllowed(clazz);
	}

	@Override
	public boolean isClassMethodAllowed(Class<?> clazz, String method, Object... args) {
		try {
			return isMethodAllowed(getMethod(clazz, method, args));
		} catch (Exception e) {
		}
		return super.isClassMethodAllowed(clazz, method, args);
	}

	@Override
	public boolean isInstanceMethodAllowed(Object obj, String method, Object... args) {
		try {
			return isMethodAllowed(getMethod(obj.getClass(), method, args));
		} catch (Exception e) {
		}
		return super.isInstanceMethodAllowed(obj, method, args);
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
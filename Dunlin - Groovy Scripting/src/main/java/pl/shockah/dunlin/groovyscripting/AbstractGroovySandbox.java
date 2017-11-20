package pl.shockah.dunlin.groovyscripting;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractGroovySandbox extends GroovyInterceptor {
	public boolean isConstructorAllowed(@Nonnull Class<?> clazz, @Nonnull Object... args) {
		return true;
	}

	public boolean isInstanceMethodAllowed(@Nonnull Object obj, @Nonnull String method, @Nonnull Object... args) {
		return true;
	}

	public boolean isClassMethodAllowed(@Nonnull Class<?> clazz, @Nonnull String method, @Nonnull Object... args) {
		return true;
	}

	public boolean isInstanceFieldGetAllowed(@Nonnull Object obj, @Nonnull String field) {
		return true;
	}

	public boolean isInstanceFieldSetAllowed(@Nonnull Object obj, @Nonnull String field, @Nonnull Object value) {
		return true;
	}

	public boolean isClassFieldGetAllowed(@Nonnull Class<?> clazz, @Nonnull String field) {
		return true;
	}

	public boolean isClassFieldSetAllowed(@Nonnull Class<?> clazz, @Nonnull String field, @Nonnull Object value) {
		return true;
	}

	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
		if (!isInstanceMethodAllowed(receiver, method, args))
			throw new SecurityException(String.format("%s.%s instance method call not allowed.", receiver.getClass().getName(), method));
		return super.onMethodCall(invoker, receiver, method, args);
	}

	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
		if (!isClassMethodAllowed(receiver, method, args))
			throw new SecurityException(String.format("%s.%s class method call not allowed.", receiver.getName(), method));
		return super.onStaticCall(invoker, receiver, method, args);
	}

	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		if (!isConstructorAllowed(receiver, args))
			throw new SecurityException(String.format("%s constructor not allowed.", receiver.getName()));
		return super.onNewInstance(invoker, receiver, args);
	}

	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		if (receiver instanceof Class<?>) {
			Class<?> clazz = (Class<?>)receiver;
			try {
				clazz.getField(property);
				if (!isClassFieldGetAllowed(clazz, property))
					throw new SecurityException(String.format("%s.%s class field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e1) {
				String methodName = String.format("get%s", StringUtils.capitalize(property));
				try {
					Method method = clazz.getMethod(methodName);
					if (Modifier.isStatic(method.getModifiers())) {
						if (!isClassMethodAllowed(clazz, methodName))
							throw new SecurityException(String.format("%s class method call not allowed.", method));
					} else {
						clazz = clazz.getClass();
						if (!isInstanceMethodAllowed(clazz, methodName))
							throw new SecurityException(String.format("%s instance method call not allowed.", method));
					}
				} catch (NoSuchMethodException ignored) {
				}
			}
		} else {
			Class<?> clazz = receiver.getClass();
			try {
				clazz.getField(property);
				if (!isInstanceFieldGetAllowed(receiver, property))
					throw new SecurityException(String.format("%s.%s instance field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e1) {
				String methodName = String.format("get%s", StringUtils.capitalize(property));
				try {
					Method method = clazz.getMethod(methodName);
					if (Modifier.isStatic(method.getModifiers())) {
						if (!isClassMethodAllowed(clazz, methodName))
							throw new SecurityException(String.format("%s class method call not allowed.", method));
					} else {
						if (!isInstanceMethodAllowed(receiver, methodName))
							throw new SecurityException(String.format("%s instance method call not allowed.", method));
					}
				} catch (NoSuchMethodException ignored) {
				}
			}
		}
		return super.onGetProperty(invoker, receiver, property);
	}

	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
		if (receiver instanceof Class<?>) {
			Class<?> clazz = (Class<?>)receiver;
			try {
				clazz.getField(property);
				if (!isClassFieldSetAllowed(clazz, property, value))
					throw new SecurityException(String.format("%s.%s class field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e1) {
				String methodName = String.format("set%s", StringUtils.capitalize(property));
				try {
					Method method = clazz.getMethod(methodName);
					if (Modifier.isStatic(method.getModifiers())) {
						if (!isClassMethodAllowed(clazz, methodName, value))
							throw new SecurityException(String.format("%s class method call not allowed.", method));
					} else {
						clazz = clazz.getClass();
						if (!isInstanceMethodAllowed(clazz, methodName, value))
							throw new SecurityException(String.format("%s instance method call not allowed.", method));
					}
				} catch (NoSuchMethodException ignored) {
				}
			}
		} else {
			Class<?> clazz = receiver.getClass();
			try {
				clazz.getField(property);
				if (!isInstanceFieldSetAllowed(receiver, property, value))
					throw new SecurityException(String.format("%s.%s instance field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e1) {
				String methodName = String.format("set%s", StringUtils.capitalize(property));
				try {
					Method method = clazz.getMethod(methodName);
					if (Modifier.isStatic(method.getModifiers())) {
						if (!isClassMethodAllowed(clazz, methodName, value))
							throw new SecurityException(String.format("%s class method call not allowed.", method));
					} else {
						if (!isInstanceMethodAllowed(receiver, methodName, value))
							throw new SecurityException(String.format("%s instance method call not allowed.", method));
					}
				} catch (NoSuchMethodException ignored) {
				}
			}
		}
		return super.onSetProperty(invoker, receiver, property, value);
	}
}
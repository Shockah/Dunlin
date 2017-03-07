package pl.shockah.dunlin.groovy;

import pl.shockah.util.UnexpectedException;

import java.lang.reflect.Method;
import java.util.Arrays;

public class BasicGroovySandboxFilter extends GroovySandboxFilter {
    public BasicGroovySandboxFilter() {
        super();
        addBasicWhitelist();
        addReflectionReadWhitelist();
    }

    private void addBasicWhitelist() {
	    try {
		    whitelistedMethods.addAll(Arrays.asList(
				    System.class.getDeclaredMethod("nanoTime"),
				    System.class.getDeclaredMethod("currentTimeMillis"),
				    Thread.class.getDeclaredMethod("sleep", long.class),
				    Object.class.getDeclaredMethod("toString"),
				    Object.class.getDeclaredMethod("hashCode"),
				    Object.class.getDeclaredMethod("equals", Object.class)
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }

    private void addReflectionReadWhitelist() {
    	addClassWhitelist();
    	addMethodWhitelist();
    }

    private void addClassWhitelist() {
	    try {
		    whitelistedMethods.addAll(Arrays.asList(
				    Object.class.getDeclaredMethod("getClass"),
				    Class.class.getDeclaredMethod("asSubclass", Class.class),
				    Class.class.getDeclaredMethod("cast", Object.class),
				    Class.class.getDeclaredMethod("desiredAssertionStatus"),
				    Class.class.getDeclaredMethod("getAnnotatedInterfaces"),
				    Class.class.getDeclaredMethod("getAnnotatedSuperclass"),
				    Class.class.getDeclaredMethod("getAnnotation", Class.class),
				    Class.class.getDeclaredMethod("getAnnotations"),
				    Class.class.getDeclaredMethod("getAnnotationsByType", Class.class),
				    Class.class.getDeclaredMethod("getCanonicalName"),
				    Class.class.getDeclaredMethod("getClasses"),
				    Class.class.getDeclaredMethod("getClassLoader"),
				    Class.class.getDeclaredMethod("getComponentType"),
				    Class.class.getDeclaredMethod("getConstructor", Class[].class),
				    Class.class.getDeclaredMethod("getConstructors"),
				    Class.class.getDeclaredMethod("getDeclaredAnnotations"),
				    Class.class.getDeclaredMethod("getDeclaredAnnotation", Class.class),
				    Class.class.getDeclaredMethod("getDeclaredAnnotationsByType", Class.class),
				    Class.class.getDeclaredMethod("getDeclaredClasses"),
				    Class.class.getDeclaredMethod("getDeclaredConstructor", Class[].class),
				    Class.class.getDeclaredMethod("getDeclaredConstructors"),
				    Class.class.getDeclaredMethod("getDeclaredField", String.class),
				    Class.class.getDeclaredMethod("getDeclaredFields"),
				    Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class),
				    Class.class.getDeclaredMethod("getDeclaredMethods"),
				    Class.class.getDeclaredMethod("getDeclaringClass"),
				    Class.class.getDeclaredMethod("getEnclosingClass"),
				    Class.class.getDeclaredMethod("getEnclosingConstructor"),
				    Class.class.getDeclaredMethod("getEnclosingMethod"),
				    Class.class.getDeclaredMethod("getEnumConstants"),
				    Class.class.getDeclaredMethod("getField", String.class),
				    Class.class.getDeclaredMethod("getFields"),
				    Class.class.getDeclaredMethod("getGenericInterfaces"),
				    Class.class.getDeclaredMethod("getGenericSuperclass"),
				    Class.class.getDeclaredMethod("getInterfaces"),
				    Class.class.getDeclaredMethod("getMethod", String.class, Class[].class),
				    Class.class.getDeclaredMethod("getMethods"),
				    Class.class.getDeclaredMethod("getModifiers"),
				    Class.class.getDeclaredMethod("getName"),
				    Class.class.getDeclaredMethod("getPackage"),
				    Class.class.getDeclaredMethod("getProtectionDomain"),
				    Class.class.getDeclaredMethod("getSigners"),
				    Class.class.getDeclaredMethod("getSimpleName"),
				    Class.class.getDeclaredMethod("getSuperclass"),
				    Class.class.getDeclaredMethod("getTypeName"),
				    Class.class.getDeclaredMethod("getTypeParameters"),
				    Class.class.getDeclaredMethod("isAnnotation"),
				    Class.class.getDeclaredMethod("isAnnotationPresent", Class.class),
				    Class.class.getDeclaredMethod("isAnonymousClass"),
				    Class.class.getDeclaredMethod("isArray"),
				    Class.class.getDeclaredMethod("isAssignableFrom", Class.class),
				    Class.class.getDeclaredMethod("isEnum"),
				    Class.class.getDeclaredMethod("isInstance", Object.class),
				    Class.class.getDeclaredMethod("isInterface"),
				    Class.class.getDeclaredMethod("isLocalClass"),
				    Class.class.getDeclaredMethod("isMemberClass"),
				    Class.class.getDeclaredMethod("isPrimitive"),
				    Class.class.getDeclaredMethod("isSynthetic"),
				    Class.class.getDeclaredMethod("toGenericString")
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }

    private void addMethodWhitelist() {
	    try {
		    whitelistedMethods.addAll(Arrays.asList(
				    Method.class.getDeclaredMethod("getAnnotatedReturnType"),
				    Method.class.getDeclaredMethod("getDeclaredAnnotations"),
				    Method.class.getDeclaredMethod("getDeclaringClass"),
				    Method.class.getDeclaredMethod("getDefaultValue"),
				    Method.class.getDeclaredMethod("getExceptionTypes"),
				    Method.class.getDeclaredMethod("getGenericExceptionTypes"),
				    Method.class.getDeclaredMethod("getGenericParameterTypes"),
				    Method.class.getDeclaredMethod("getGenericReturnType"),
				    Method.class.getDeclaredMethod("getModifiers"),
				    Method.class.getDeclaredMethod("getName"),
				    Method.class.getDeclaredMethod("getParameterAnnotations"),
				    Method.class.getDeclaredMethod("getParameterCount"),
				    Method.class.getDeclaredMethod("getParameterTypes"),
				    Method.class.getDeclaredMethod("getReturnType"),
				    Method.class.getDeclaredMethod("getTypeParameters"),
				    Method.class.getDeclaredMethod("isBridge"),
				    Method.class.getDeclaredMethod("isDefault"),
				    Method.class.getDeclaredMethod("isSynthetic"),
				    Method.class.getDeclaredMethod("isVarArgs"),
				    Method.class.getDeclaredMethod("toGenericString")
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }
}
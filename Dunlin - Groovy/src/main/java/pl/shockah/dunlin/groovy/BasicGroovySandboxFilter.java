package pl.shockah.dunlin.groovy;

import pl.shockah.util.UnexpectedException;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BasicGroovySandboxFilter extends GroovySandboxFilter {
    public BasicGroovySandboxFilter() {
        super();
        addBasicWhitelist();
        addReflectionReadWhitelist();
    }

    private void addBasicWhitelist() {
	    try {
	    	whitelistedPackages.addAll(Arrays.asList(
	                Package.getPackage("java.util"),
                    Package.getPackage("java.text"),
                    Package.getPackage("java.math"),
                    Package.getPackage("java.time"),

                    Package.getPackage("pl.shockah.util"),
                    Package.getPackage("pl.shockah.json"),
                    Package.getPackage("pl.shockah.plugin"),

                    Package.getPackage("groovy.json"),
                    Package.getPackage("org.codehaus.groovy.runtime")
            ));

	        whitelistedClasses.addAll(Arrays.asList(
	        		Comparable.class,
					TimeUnit.class
			));

		    whitelistedMethods.addAll(Arrays.asList(
				    System.class.getMethod("nanoTime"),
				    System.class.getMethod("currentTimeMillis"),

				    Thread.class.getMethod("sleep", long.class),

				    Object.class.getMethod("toString"),
				    Object.class.getMethod("hashCode"),
				    Object.class.getMethod("equals", Object.class),

					Long.class.getMethod("compareTo", Long.class),
					Long.class.getMethod("bitCount", long.class),
					Long.class.getMethod("compare", long.class, long.class),
					Long.class.getMethod("compareUnsigned", long.class, long.class),
					Long.class.getMethod("divideUnsigned", long.class, long.class),
					Long.class.getMethod("getLong", String.class),
					Long.class.getMethod("getLong", String.class, long.class),
					Long.class.getMethod("getLong", String.class, Long.class),
					Long.class.getMethod("highestOneBit", long.class),
					Long.class.getMethod("lowestOneBit", long.class),
					Long.class.getMethod("max", long.class, long.class),
					Long.class.getMethod("min", long.class, long.class),
					Long.class.getMethod("numberOfLeadingZeros", long.class),
					Long.class.getMethod("numberOfTrailingZeros", long.class),
					Long.class.getMethod("parseLong", String.class),
					Long.class.getMethod("parseLong", String.class, int.class),
					Long.class.getMethod("parseUnsignedLong", String.class),
					Long.class.getMethod("parseUnsignedLong", String.class, int.class),
					Long.class.getMethod("remainderUnsigned", long.class, long.class),
					Long.class.getMethod("reverse", long.class),
					Long.class.getMethod("reverseBytes", long.class),
					Long.class.getMethod("rotateLeft", long.class, int.class),
					Long.class.getMethod("rotateRight", long.class, int.class),
					Long.class.getMethod("signum", long.class),
					Long.class.getMethod("sum", long.class, long.class),
					Long.class.getMethod("toBinaryString", long.class),
					Long.class.getMethod("toHexString", long.class),
					Long.class.getMethod("toOctalString", long.class),
					Long.class.getMethod("toString", long.class),
					Long.class.getMethod("toString", long.class, int.class),
					Long.class.getMethod("toUnsignedString", long.class),
					Long.class.getMethod("toUnsignedString", long.class, int.class),
					Long.class.getMethod("valueOf", long.class),
					Long.class.getMethod("valueOf", String.class),
					Long.class.getMethod("valueOf", String.class, int.class)
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }

    private void addReflectionReadWhitelist() {
        addMemberWhitelist();
    	addClassWhitelist();
    	addMethodWhitelist();
    	addFieldWhitelist();
    }

    private void addMemberWhitelist() {
        try {
            whitelistedMethods.addAll(Arrays.asList(
                    Member.class.getMethod("getDeclaringClass"),
                    Member.class.getMethod("getModifiers"),
                    Member.class.getMethod("getName")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }

    private void addClassWhitelist() {
	    try {
		    whitelistedMethods.addAll(Arrays.asList(
				    Object.class.getMethod("getClass"),
				    Class.class.getMethod("asSubclass", Class.class),
				    Class.class.getMethod("cast", Object.class),
				    Class.class.getMethod("desiredAssertionStatus"),
				    Class.class.getMethod("getAnnotatedInterfaces"),
				    Class.class.getMethod("getAnnotatedSuperclass"),
				    Class.class.getMethod("getAnnotation", Class.class),
				    Class.class.getMethod("getAnnotations"),
				    Class.class.getMethod("getAnnotationsByType", Class.class),
				    Class.class.getMethod("getCanonicalName"),
				    Class.class.getMethod("getClasses"),
				    Class.class.getMethod("getClassLoader"),
				    Class.class.getMethod("getComponentType"),
				    Class.class.getMethod("getConstructor", Class[].class),
				    Class.class.getMethod("getConstructors"),
				    Class.class.getMethod("getDeclaredAnnotations"),
				    Class.class.getMethod("getDeclaredAnnotation", Class.class),
				    Class.class.getMethod("getDeclaredAnnotationsByType", Class.class),
				    Class.class.getMethod("getDeclaredClasses"),
				    Class.class.getMethod("getDeclaredConstructor", Class[].class),
				    Class.class.getMethod("getDeclaredConstructors"),
				    Class.class.getMethod("getDeclaredField", String.class),
				    Class.class.getMethod("getDeclaredFields"),
				    Class.class.getMethod("getMethod", String.class, Class[].class),
				    Class.class.getMethod("getMethods"),
				    Class.class.getMethod("getDeclaringClass"),
				    Class.class.getMethod("getEnclosingClass"),
				    Class.class.getMethod("getEnclosingConstructor"),
				    Class.class.getMethod("getEnclosingMethod"),
				    Class.class.getMethod("getEnumConstants"),
				    Class.class.getMethod("getField", String.class),
				    Class.class.getMethod("getFields"),
				    Class.class.getMethod("getGenericInterfaces"),
				    Class.class.getMethod("getGenericSuperclass"),
				    Class.class.getMethod("getInterfaces"),
				    Class.class.getMethod("getMethod", String.class, Class[].class),
				    Class.class.getMethod("getMethods"),
				    Class.class.getMethod("getModifiers"),
				    Class.class.getMethod("getName"),
				    Class.class.getMethod("getPackage"),
				    Class.class.getMethod("getProtectionDomain"),
				    Class.class.getMethod("getSigners"),
				    Class.class.getMethod("getSimpleName"),
				    Class.class.getMethod("getSuperclass"),
				    Class.class.getMethod("getTypeName"),
				    Class.class.getMethod("getTypeParameters"),
				    Class.class.getMethod("isAnnotation"),
				    Class.class.getMethod("isAnnotationPresent", Class.class),
				    Class.class.getMethod("isAnonymousClass"),
				    Class.class.getMethod("isArray"),
				    Class.class.getMethod("isAssignableFrom", Class.class),
				    Class.class.getMethod("isEnum"),
				    Class.class.getMethod("isInstance", Object.class),
				    Class.class.getMethod("isInterface"),
				    Class.class.getMethod("isLocalClass"),
				    Class.class.getMethod("isMemberClass"),
				    Class.class.getMethod("isPrimitive"),
				    Class.class.getMethod("isSynthetic"),
				    Class.class.getMethod("toGenericString")
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }

    private void addMethodWhitelist() {
	    try {
		    whitelistedMethods.addAll(Arrays.asList(
				    Method.class.getMethod("getAnnotatedReturnType"),
				    Method.class.getMethod("getDeclaredAnnotations"),
				    Method.class.getMethod("getDeclaringClass"),
				    Method.class.getMethod("getDefaultValue"),
				    Method.class.getMethod("getExceptionTypes"),
				    Method.class.getMethod("getGenericExceptionTypes"),
				    Method.class.getMethod("getGenericParameterTypes"),
				    Method.class.getMethod("getGenericReturnType"),
				    Method.class.getMethod("getModifiers"),
				    Method.class.getMethod("getName"),
				    Method.class.getMethod("getParameterAnnotations"),
				    Method.class.getMethod("getParameterCount"),
				    Method.class.getMethod("getParameterTypes"),
				    Method.class.getMethod("getReturnType"),
				    Method.class.getMethod("getTypeParameters"),
				    Method.class.getMethod("isBridge"),
				    Method.class.getMethod("isDefault"),
				    Method.class.getMethod("isSynthetic"),
				    Method.class.getMethod("isVarArgs"),
				    Method.class.getMethod("toGenericString")
		    ));
	    } catch (NoSuchMethodException e) {
		    throw new UnexpectedException(e);
	    }
    }

    private void addFieldWhitelist() {
        try {
            whitelistedMethods.addAll(Arrays.asList(
                    Field.class.getMethod("getAnnotatedType"),
                    Field.class.getMethod("getAnnotationsByType", Class.class),
                    Field.class.getMethod("getAnnotation", Class.class),
                    Field.class.getMethod("getDeclaredAnnotations"),
                    Field.class.getMethod("getDeclaringClass"),
                    Field.class.getMethod("getGenericType"),
                    Field.class.getMethod("getModifiers"),
                    Field.class.getMethod("getName"),
                    Field.class.getMethod("getType"),
                    Field.class.getMethod("isEnumConstant"),
                    Field.class.getMethod("isSynthetic"),
                    Field.class.getMethod("toGenericString")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}
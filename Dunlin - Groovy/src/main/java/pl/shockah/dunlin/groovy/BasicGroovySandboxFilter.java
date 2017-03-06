package pl.shockah.dunlin.groovy;

import pl.shockah.util.UnexpectedException;

import java.util.Arrays;

public class BasicGroovySandboxFilter extends GroovySandboxFilter {
    public BasicGroovySandboxFilter() {
        super();

        try {
            whitelistedMethods.addAll(Arrays.asList(
                    System.class.getDeclaredMethod("nanoTime"),
                    System.class.getDeclaredMethod("currentTimeMillis"),
                    Thread.class.getDeclaredMethod("sleep", long.class),
                    Object.class.getDeclaredMethod("toString"),
                    Object.class.getDeclaredMethod("hashCode"),
                    Object.class.getDeclaredMethod("equals", Object.class),
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
                    Class.class.getDeclaredMethod("getDeclaredClasses")
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}
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
                    Object.class.getDeclaredMethod("equals", Object.class)
            ));
        } catch (NoSuchMethodException e) {
            throw new UnexpectedException(e);
        }
    }
}
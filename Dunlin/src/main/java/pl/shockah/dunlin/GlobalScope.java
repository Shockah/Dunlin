package pl.shockah.dunlin;

import javax.annotation.Nonnull;

public class GlobalScope extends Scope {
    @Override
    @Nonnull public String name() {
        return "Global";
    }
}

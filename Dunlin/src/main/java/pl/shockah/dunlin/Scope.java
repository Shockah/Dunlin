package pl.shockah.dunlin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Scope {
    @Nullable public Scope downscope() {
        return null;
    }

    @Nonnull public abstract String name();
}
package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SettingScope {
    @Nullable public final Scope scope;

    public SettingScope(@Nonnull Scope scope) {
        this.scope = scope;
    }

    @Nullable public SettingScope downscope() {
        return null;
    }

    @Nullable protected abstract Object getRaw(@Nonnull Setting<?> setting);

    protected abstract void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw);

    @Nonnull public final String getName() {
        return scope.name();
    }
}
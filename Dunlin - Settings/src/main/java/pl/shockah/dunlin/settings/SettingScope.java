package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.Scope;

public abstract class SettingScope {
    public final Scope scope;

    public SettingScope(Scope scope) {
        this.scope = scope;
    }

    public SettingScope downscope() {
        return null;
    }

    protected abstract Object getRaw(Setting<?> setting);

    protected abstract void setRaw(Setting<?> setting, Object raw);

    public final String name() {
        return scope.name();
    }
}
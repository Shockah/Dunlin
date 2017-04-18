package pl.shockah.dunlin.settings;

public abstract class SettingScope {
    public SettingScope downscope() {
        return null;
    }

    protected abstract Object getRaw(Setting<?> setting);

    protected abstract void setRaw(Setting<?> setting, Object raw);

    public abstract String name();
}
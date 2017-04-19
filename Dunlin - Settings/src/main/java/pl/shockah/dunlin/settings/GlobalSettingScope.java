package pl.shockah.dunlin.settings;

public class GlobalSettingScope extends SettingScope {
    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("global").get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("global").put(setting.getFullName(), raw);
    }

    @Override
    public String name() {
        return "Global";
    }
}
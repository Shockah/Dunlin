package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.GlobalScope;

public class GlobalSettingScope extends SettingScope {
    public GlobalSettingScope() {
        super(new GlobalScope());
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("global").get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("global").put(setting.getFullName(), raw);
    }
}
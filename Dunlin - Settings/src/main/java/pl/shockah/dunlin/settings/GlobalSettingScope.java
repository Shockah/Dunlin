package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.GlobalScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GlobalSettingScope extends SettingScope {
    public GlobalSettingScope() {
        super(new GlobalScope());
    }

    @Override
    @Nullable protected Object getRaw(@Nonnull Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("global").get(setting.getFullName());
    }

    @Override
    protected void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("global").put(setting.getFullName(), raw);
    }
}
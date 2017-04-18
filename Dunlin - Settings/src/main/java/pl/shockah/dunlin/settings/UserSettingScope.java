package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.User;

public class UserSettingScope extends SettingScope {
    public final User user;

    public UserSettingScope(User user) {
        this.user = user;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("user").getObjectOrEmpty(user.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("user").getObjectOrEmpty(user.getId()).put(setting.getFullName(), raw);
    }

    @Override
    public SettingScope downscope() {
        return new GlobalSettingScope();
    }
}
package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.User;

public class PrivateUserSetting<T> extends UserSetting<T> {
    protected final UserSetting<T> baseSetting;

    public PrivateUserSetting(UserSetting<T> setting) {
        super(setting.settingsPlugin, setting.type, setting.plugin, setting.name, setting.defaultValue);
        baseSetting = setting;
    }

    @Override
    public T get(User user) {
        throw new SecurityException("Cannot retrieve a private setting.");
    }
}
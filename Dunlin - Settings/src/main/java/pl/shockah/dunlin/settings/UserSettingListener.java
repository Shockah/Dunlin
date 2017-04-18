package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.User;

public interface UserSettingListener<T> {
    void onSettingSet(UserSetting<T> setting, T value, User user);
}
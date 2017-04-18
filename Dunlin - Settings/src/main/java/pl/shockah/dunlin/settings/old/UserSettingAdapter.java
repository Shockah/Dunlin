package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.User;

public class UserSettingAdapter<T> implements UserSettingListener<T> {
    @Override
    public void onSettingSet(UserSetting<T> setting, T value, User user) {
    }
}
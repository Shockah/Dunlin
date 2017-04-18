package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.User;

public interface UserSettingsListener {
    void onSettingSet(String fullSettingName, Object value, User user);
}
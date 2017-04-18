package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;

public interface SettingListener {
    void onSettingSet(String fullSettingName, Object value, Scope scope, TextChannel channel);
}
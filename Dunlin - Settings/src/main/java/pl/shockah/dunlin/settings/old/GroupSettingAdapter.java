package pl.shockah.dunlin.settings.old;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;

public class GroupSettingAdapter<T> implements GroupSettingListener<T> {
    @Override
    public void onSettingSet(GroupSetting<T> setting, T value, Scope scope, TextChannel channel) {
    }
}
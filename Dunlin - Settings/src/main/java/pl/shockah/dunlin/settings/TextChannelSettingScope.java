package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.TextChannelScope;

public class TextChannelSettingScope extends SettingScope {
    public final TextChannelScope textChannelScope;

    public TextChannelSettingScope(TextChannel textChannel) {
        super(new TextChannelScope(textChannel));
        textChannelScope = (TextChannelScope)scope;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("textChannel").getObjectOrEmpty(textChannelScope.textChannel.getGuild().getId()).getObjectOrEmpty(textChannelScope.textChannel.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(textChannelScope.textChannel.getGuild().getId()).getObjectOrEmpty(textChannelScope.textChannel.getId()).put(setting.getFullName(), raw);
    }

    @Override
    public SettingScope downscope() {
        return new GuildSettingScope(textChannelScope.textChannel.getGuild());
    }
}
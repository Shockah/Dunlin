package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;

public class TextChannelSettingScope extends SettingScope {
    public final TextChannel textChannel;

    public TextChannelSettingScope(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("textChannel").getObjectOrEmpty(textChannel.getGuild().getId()).getObjectOrEmpty(textChannel.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(textChannel.getGuild().getId()).getObjectOrEmpty(textChannel.getId()).put(setting.getFullName(), raw);
    }

    @Override
    public SettingScope downscope() {
        return new GuildSettingScope(textChannel.getGuild());
    }
}
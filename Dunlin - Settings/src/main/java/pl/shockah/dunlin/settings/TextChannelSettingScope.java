package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.TextChannelScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TextChannelSettingScope extends SettingScope {
    @Nonnull public final TextChannelScope textChannelScope;

    public TextChannelSettingScope(@Nonnull TextChannel textChannel) {
        super(new TextChannelScope(textChannel));
        textChannelScope = (TextChannelScope)scope;
    }

    @Override
    @Nullable protected Object getRaw(@Nonnull Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("textChannel").getObjectOrEmpty(textChannelScope.textChannel.getGuild().getId()).getObjectOrEmpty(textChannelScope.textChannel.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(textChannelScope.textChannel.getGuild().getId()).getObjectOrEmpty(textChannelScope.textChannel.getId()).put(setting.getFullName(), raw);
    }

    @Override
    @Nullable public SettingScope downscope() {
        return new GuildSettingScope(textChannelScope.textChannel.getGuild());
    }
}
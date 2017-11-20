package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.Guild;
import pl.shockah.dunlin.GuildScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuildSettingScope extends SettingScope {
    @Nonnull public final GuildScope guildScope;

    public GuildSettingScope(@Nonnull Guild guild) {
        super(new GuildScope(guild));
        guildScope = (GuildScope)scope;
    }

    @Override
    @Nullable protected Object getRaw(@Nonnull Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("guild").getObjectOrEmpty(guildScope.guild.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(guildScope.guild.getId()).put(setting.getFullName(), raw);
    }

    @Override
    @Nullable public SettingScope downscope() {
        return new GlobalSettingScope();
    }
}
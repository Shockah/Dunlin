package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.Guild;
import pl.shockah.dunlin.GuildScope;

public class GuildSettingScope extends SettingScope {
    public final GuildScope guildScope;

    public GuildSettingScope(Guild guild) {
        super(new GuildScope(guild));
        guildScope = (GuildScope)scope;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("guild").getObjectOrEmpty(guildScope.guild.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(guildScope.guild.getId()).put(setting.getFullName(), raw);
    }

    @Override
    public SettingScope downscope() {
        return new GlobalSettingScope();
    }
}
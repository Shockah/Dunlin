package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.Guild;

public class GuildSettingScope extends SettingScope {
    public final Guild guild;

    public GuildSettingScope(Guild guild) {
        this.guild = guild;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("guild").getObjectOrEmpty(guild.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("guild").getObjectOrEmpty(guild.getId()).put(setting.getFullName(), raw);
    }

    @Override
    public SettingScope downscope() {
        return new GlobalSettingScope();
    }
}
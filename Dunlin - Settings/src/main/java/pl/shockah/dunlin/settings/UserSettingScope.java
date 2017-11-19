package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UserSettingScope extends SettingScope {
    @Nonnull public final User user;

    public UserSettingScope(@Nonnull User user) {
        super(new Scope() {
            @Override
            @Nonnull public String name() {
                return String.format("User: %s#%s", user.getName(), user.getDiscriminator());
            }
        });
        this.user = user;
    }

    @Override
    @Nullable protected Object getRaw(@Nonnull Setting<?> setting) {
        return setting.settingsPlugin.settingsJson.getObjectOrEmpty("user").getObjectOrEmpty(user.getId()).get(setting.getFullName());
    }

    @Override
    protected void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw) {
        setting.settingsPlugin.settingsJson.getObjectOrNew("user").getObjectOrEmpty(user.getId()).put(setting.getFullName(), raw);
    }

    @Override
    @Nullable public SettingScope downscope() {
        return new GlobalSettingScope();
    }
}
package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageSettingScope extends SettingScope {
    @Nonnull public final Message message;

    public MessageSettingScope(@Nonnull Message message) {
        super(null);
        this.message = message;
    }

    @Override
    protected Object getRaw(@Nonnull Setting<?> setting) {
        SettingScope scope;
        Object value;

        scope = downscopeToUser();
        value = scope.getRaw(setting);
        if (value != null)
            return value;

        scope = downscope();
        if (scope != null) {
            value = scope.getRaw(setting);
            if (value != null)
                return value;
        }

        throw new IllegalArgumentException();
    }

    @Override
    protected void setRaw(@Nonnull Setting<?> setting, @Nullable Object raw) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable public SettingScope downscope() {
        return new TextChannelSettingScope(message.getTextChannel());
    }

    @Nonnull private UserSettingScope downscopeToUser() {
        return new UserSettingScope(message.getAuthor());
    }
}
package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.Message;

public class MessageSettingScope extends SettingScope {
    public final Message message;

    public MessageSettingScope(Message message) {
        super(null);
        this.message = message;
    }

    @Override
    protected Object getRaw(Setting<?> setting) {
        Object value = downscopeToUser().getRaw(setting);
        if (value != null)
            return value;
        return downscope().getRaw(setting);
    }

    @Override
    protected void setRaw(Setting<?> setting, Object raw) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SettingScope downscope() {
        return new TextChannelSettingScope(message.getTextChannel());
    }

    private UserSettingScope downscopeToUser() {
        return new UserSettingScope(message.getAuthor());
    }
}
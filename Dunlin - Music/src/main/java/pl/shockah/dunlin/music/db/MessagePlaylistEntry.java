package pl.shockah.dunlin.music.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.db.DbObject.TableVersion;

import javax.annotation.Nonnull;

@DatabaseTable(tableName = "pl_shockah_dunlin_music_db_MessagePlaylistEntry")
@TableVersion(1)
public class MessagePlaylistEntry extends DbObject<MessagePlaylistEntry> {
    @DatabaseField(columnName = GUILD_ID, canBeNull = false)
    private String guildId;
    @Nonnull public static final String GUILD_ID = "guildId";

    @DatabaseField(columnName = CHANNEL_ID, canBeNull = false)
    private String channelId;
    @Nonnull public static final String CHANNEL_ID = "channelId";

    @DatabaseField(columnName = MESSAGE_ID, canBeNull = false)
    private String messageId;
    @Nonnull public static final String MESSAGE_ID = "messageId";

    @Deprecated //ORMLite-only
    MessagePlaylistEntry() {
        super();
    }

    public MessagePlaylistEntry(@Nonnull Dao<MessagePlaylistEntry, Integer> dao) {
        super(dao);
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(@Nonnull String guildId) {
        this.guildId = guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(@Nonnull String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@Nonnull String messageId) {
        this.messageId = messageId;
    }

    public Guild getGuild(@Nonnull ShardManager manager) {
        return manager.getGuildById(guildId);
    }

    public TextChannel getChannel(@Nonnull ShardManager manager) {
        return getGuild(manager).getTextChannelById(channelId);
    }

    public RestAction<Message> getMessage(@Nonnull ShardManager manager) {
        return getChannel(manager).getMessageById(messageId);
    }

    public void setMessage(@Nonnull Message message) {
        guildId = message.getGuild().getId();
        channelId = message.getTextChannel().getId();
        messageId = message.getId();
    }
}
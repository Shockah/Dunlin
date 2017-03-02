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

@DatabaseTable(tableName = "pl_shockah_dunlin_music_db_MessagePlaylistEntry")
@TableVersion(1)
public class MessagePlaylistEntry extends DbObject<MessagePlaylistEntry> {
    @DatabaseField(columnName = GUILD_ID, canBeNull = false)
    private String guildId;
    public static final String GUILD_ID = "guildId";

    @DatabaseField(columnName = CHANNEL_ID, canBeNull = false)
    private String channelId;
    public static final String CHANNEL_ID = "channelId";

    @DatabaseField(columnName = MESSAGE_ID, canBeNull = false)
    private String messageId;
    public static final String MESSAGE_ID = "messageId";

    @Deprecated //ORMLite-only
    MessagePlaylistEntry() {
        super();
    }

    public MessagePlaylistEntry(Dao<MessagePlaylistEntry, Integer> dao) {
        super(dao);
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Guild getGuild(ShardManager manager) {
        return manager.getGuildById(guildId);
    }

    public TextChannel getChannel(ShardManager manager) {
        return getGuild(manager).getTextChannelById(channelId);
    }

    public RestAction<Message> getMessage(ShardManager manager) {
        return getChannel(manager).getMessageById(messageId);
    }

    public void setMessage(Message message) {
        guildId = message.getGuild().getId();
        channelId = message.getTextChannel().getId();
        messageId = message.getId();
    }
}
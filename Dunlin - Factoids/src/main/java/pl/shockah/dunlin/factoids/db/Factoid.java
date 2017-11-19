package pl.shockah.dunlin.factoids.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.factoids.FactoidScope;
import pl.shockah.dunlin.factoids.GlobalFactoidScope;
import pl.shockah.dunlin.factoids.GuildFactoidScope;
import pl.shockah.dunlin.factoids.TextChannelFactoidScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

@DatabaseTable(tableName = "pl_shockah_dunlin_factoids_db_Factoid")
@DbObject.TableVersion(1)
public class Factoid extends DbObject<Factoid> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	@Nonnull public static final String NAME = "name";

	@DatabaseField(columnName = GUILD_ID)
	private Long guildId;
	@Nonnull public static final String GUILD_ID = "guildId";

	@DatabaseField(columnName = CHANNEL_ID)
	private Long channelId;
	@Nonnull public static final String CHANNEL_ID = "channelId";

	@DatabaseField(columnName = AUTHOR_USER_ID)
	private long authorUserId;
	@Nonnull public static final String AUTHOR_USER_ID = "authorUserId";

	@DatabaseField(columnName = CONTENT, canBeNull = false)
	private String content;
	@Nonnull public static final String CONTENT = "content";

	@DatabaseField(columnName = FORGOTTEN, canBeNull = false)
	private boolean forgotten;
	@Nonnull public static final String FORGOTTEN = "forgotten";

	@DatabaseField(columnName = TYPE, canBeNull = false)
	private String type;
	@Nonnull public static final String TYPE = "type";

	@DatabaseField(columnName = DATE, canBeNull = false)
	private Date date;
	@Nonnull public static final String DATE = "date";

	@DatabaseField(columnName = SCOPE_TYPE, canBeNull = false)
	private String scopeType;
	@Nonnull public static final String SCOPE_TYPE = "scopeType";

	@Deprecated //ORMLite-only
	Factoid() {
		super();
	}

	public Factoid(@Nonnull Dao<Factoid, Integer> dao) {
		super(dao);
		date = new Date();
	}

	public String getName() {
		return name;
	}

	public void setName(@Nonnull String name) {
		this.name = name;
	}

	@Nullable public Long getGuildId() {
		return guildId;
	}

	public void setGuildId(@Nullable Long guildId) {
		this.guildId = guildId;
	}

	@Nullable public Guild getGuild(@Nonnull ShardManager manager) {
		return manager.getGuildById(guildId);
	}

	@Nullable public Guild getGuild(@Nonnull JDA jda) {
		return jda.getGuildById(guildId);
	}

	public void setGuild(@Nullable Guild guild) {
		setGuildId(guild == null ? null : guild.getIdLong());
	}

	@Nullable public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(@Nullable Long channelId) {
		this.channelId = channelId;
	}

	@Nullable public TextChannel getChannel(@Nonnull ShardManager manager) {
		return channelId == null ? null : manager.getTextChannelById(channelId);
	}

	@Nullable public TextChannel getChannel(@Nonnull JDA jda) {
		return jda.getTextChannelById(channelId);
	}

	public void setChannel(@Nullable TextChannel channel) {
		setGuild(channel == null ? null : channel.getGuild());
		setChannelId(channel == null ? null : channel.getIdLong());
	}

	public long getAuthorUserId() {
		return authorUserId;
	}

	public void setAuthorUserId(long authorUserId) {
		this.authorUserId = authorUserId;
	}

	public User getAuthor(@Nonnull ShardManager manager) {
		return manager.getUserById(authorUserId);
	}

	public User getAuthor(@Nonnull JDA jda) {
		return jda.getUserById(authorUserId);
	}

	public void setAuthor(@Nonnull User user) {
		setAuthorUserId(user.getIdLong());
	}

	public String getContent() {
		return content;
	}

	public void setContent(@Nonnull String content) {
		this.content = content;
	}

	public boolean isForgotten() {
		return forgotten;
	}

	public void setForgotten(boolean forgotten) {
		this.forgotten = forgotten;
	}

	public String getType() {
		return type;
	}

	public void setType(@Nonnull String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(@Nonnull Date date) {
		this.date = date;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(@Nonnull String scopeType) {
		this.scopeType = scopeType;
	}

	public FactoidScope getScope(@Nonnull ShardManager manager) {
		switch (scopeType) {
			case "Global":
				return new GlobalFactoidScope();
			case "Guild":
				Guild guild = manager.getGuildById(guildId);
				if (guild == null)
					throw new IllegalArgumentException();
				return new GuildFactoidScope(guild);
			case "TextChannel":
				TextChannel channel = manager.getTextChannelById(channelId);
				if (channel == null)
					throw new IllegalArgumentException();
				return new TextChannelFactoidScope(channel);
		}
		throw new IllegalStateException();
	}

	@Nonnull public FactoidScope getScope(@Nonnull JDA jda) {
		switch (scopeType) {
			case "Global":
				return new GlobalFactoidScope();
			case "Guild":
				return new GuildFactoidScope(jda.getGuildById(guildId));
			case "TextChannel":
				return new TextChannelFactoidScope(jda.getTextChannelById(channelId));
		}
		throw new IllegalStateException();
	}

	public void setScope(@Nonnull FactoidScope scope) {
		//TODO: code smell
		if (scope instanceof TextChannelFactoidScope) {
			setChannel(((TextChannelFactoidScope)scope).textChannelScope.textChannel);
			setScopeType("TextChannel");
		} else if (scope instanceof GuildFactoidScope) {
			setGuild(((GuildFactoidScope)scope).guildScope.guild);
			setChannelId(null);
			setScopeType("Guild");
		} else if (scope instanceof GlobalFactoidScope) {
			setGuildId(null);
			setChannelId(null);
			setScopeType("Global");
		} else {
			throw new IllegalArgumentException();
		}
	}
}
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

import java.util.Date;

@DatabaseTable(tableName = "pl_shockah_dunlin_factoids_db_Factoid")
@DbObject.TableVersion(1)
public class Factoid extends DbObject<Factoid> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	public static final String NAME = "name";

	@DatabaseField(columnName = GUILD_ID, canBeNull = true)
	private String guildId;
	public static final String GUILD_ID = "guildId";

	@DatabaseField(columnName = CHANNEL_ID, canBeNull = true)
	private String channelId;
	public static final String CHANNEL_ID = "channelId";

	@DatabaseField(columnName = AUTHOR_USER_ID, canBeNull = false)
	private String authorUserId;
	public static final String AUTHOR_USER_ID = "authorUserId";

	@DatabaseField(columnName = CONTENT, canBeNull = false)
	private String content;
	public static final String CONTENT = "content";

	@DatabaseField(columnName = FORGOTTEN, canBeNull = false)
	private boolean forgotten;
	public static final String FORGOTTEN = "forgotten";

	@DatabaseField(columnName = TYPE, canBeNull = false)
	private String type;
	public static final String TYPE = "type";

	@DatabaseField(columnName = DATE, canBeNull = false)
	private Date date;
	public static final String DATE = "date";

	@DatabaseField(columnName = SCOPE_TYPE, canBeNull = false)
	private String scopeType;
	public static final String SCOPE_TYPE = "scopeType";

	@Deprecated //ORMLite-only
	Factoid() {
		super();
	}

	public Factoid(Dao<Factoid, Integer> dao) {
		super(dao);
		date = new Date();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuildId() {
		return guildId;
	}

	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}

	public Guild getGuild(ShardManager manager) {
		return manager.getGuildById(guildId);
	}

	public Guild getGuild(JDA jda) {
		return jda.getGuildById(guildId);
	}

	public void setGuild(Guild guild) {
		setGuildId(guild == null ? null : guild.getId());
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public TextChannel getChannel(ShardManager manager) {
		return manager.getTextChannelById(channelId);
	}

	public TextChannel getChannel(JDA jda) {
		return jda.getTextChannelById(channelId);
	}

	public void setChannel(TextChannel channel) {
		setGuild(channel == null ? null : channel.getGuild());
		setChannelId(channel == null ? null : channel.getId());
	}

	public String getAuthorUserId() {
		return authorUserId;
	}

	public void setAuthorUserId(String authorUserId) {
		this.authorUserId = authorUserId;
	}

	public User getAuthor(ShardManager manager) {
		return manager.getUserById(authorUserId);
	}

	public User getAuthor(JDA jda) {
		return jda.getUserById(authorUserId);
	}

	public void setAuthor(User user) {
		setAuthorUserId(user.getId());
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
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

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public FactoidScope getScope(ShardManager manager) {
		switch (scopeType) {
			case "Global":
				return new GlobalFactoidScope();
			case "Guild":
				return new GuildFactoidScope(manager.getGuildById(guildId));
			case "TextChannel":
				return new TextChannelFactoidScope(manager.getTextChannelById(channelId));
		}
		throw new IllegalStateException();
	}

	public FactoidScope getScope(JDA jda) {
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

	public void setScope(FactoidScope scope) {
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
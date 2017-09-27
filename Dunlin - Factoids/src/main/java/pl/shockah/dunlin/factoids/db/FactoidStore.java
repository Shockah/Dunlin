package pl.shockah.dunlin.factoids.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.ShardManager;
import pl.shockah.dunlin.db.DbObject;
import pl.shockah.dunlin.factoids.FactoidScope;
import pl.shockah.dunlin.factoids.GlobalFactoidScope;
import pl.shockah.dunlin.factoids.GuildFactoidScope;
import pl.shockah.dunlin.factoids.TextChannelFactoidScope;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrinter;

@DatabaseTable(tableName = "pl_shockah_dunlin_factoids_db_Factoid")
@DbObject.TableVersion(1)
public class FactoidStore extends DbObject<FactoidStore> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	public static final String NAME = "name";

	@DatabaseField(columnName = GUILD_ID)
	private Long guildId;
	public static final String GUILD_ID = "guildId";

	@DatabaseField(columnName = CHANNEL_ID)
	private Long channelId;
	public static final String CHANNEL_ID = "channelId";

	@DatabaseField(columnName = SCOPE_TYPE, canBeNull = false)
	private String scopeType;
	public static final String SCOPE_TYPE = "scopeType";

	@DatabaseField(columnName = JSON, canBeNull = false)
	private String json;
	public static final String JSON = "json";

	@Deprecated //ORMLite-only
	FactoidStore() {
		super();
	}

	public FactoidStore(Dao<FactoidStore, Integer> dao) {
		super(dao);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getGuildId() {
		return guildId;
	}

	public void setGuildId(Long guildId) {
		this.guildId = guildId;
	}

	public Guild getGuild(ShardManager manager) {
		return manager.getGuildById(guildId);
	}

	public Guild getGuild(JDA jda) {
		return jda.getGuildById(guildId);
	}

	public void setGuild(Guild guild) {
		setGuildId(guild == null ? null : guild.getIdLong());
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public TextChannel getChannel(ShardManager manager) {
		return channelId == null ? null : manager.getTextChannelById(channelId);
	}

	public TextChannel getChannel(JDA jda) {
		return jda.getTextChannelById(channelId);
	}

	public void setChannel(TextChannel channel) {
		setGuild(channel == null ? null : channel.getGuild());
		setChannelId(channel == null ? null : channel.getIdLong());
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public JSONObject getJsonObject() {
		return json == null ? null : new JSONParser().parseObject(json);
	}

	public void setJsonObject(JSONObject object) {
		setJson(object == null ? null : new JSONPrinter().toString(object));
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
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@DatabaseTable(tableName = "pl_shockah_dunlin_factoids_db_Factoid")
@DbObject.TableVersion(1)
public class FactoidStore extends DbObject<FactoidStore> {
	@DatabaseField(columnName = NAME, canBeNull = false)
	private String name;
	@Nonnull public static final String NAME = "name";

	@DatabaseField(columnName = GUILD_ID)
	private Long guildId;
	@Nonnull public static final String GUILD_ID = "guildId";

	@DatabaseField(columnName = CHANNEL_ID)
	private Long channelId;
	@Nonnull public static final String CHANNEL_ID = "channelId";

	@DatabaseField(columnName = SCOPE_TYPE, canBeNull = false)
	private String scopeType;
	@Nonnull public static final String SCOPE_TYPE = "scopeType";

	@DatabaseField(columnName = JSON, canBeNull = false)
	private String json;
	@Nonnull public static final String JSON = "json";

	@Deprecated //ORMLite-only
	FactoidStore() {
		super();
	}

	public FactoidStore(@Nonnull Dao<FactoidStore, Integer> dao) {
		super(dao);
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

	public Guild getGuild(@Nonnull JDA jda) {
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

	public TextChannel getChannel(@Nonnull JDA jda) {
		return jda.getTextChannelById(channelId);
	}

	public void setChannel(@Nullable TextChannel channel) {
		setGuild(channel == null ? null : channel.getGuild());
		setChannelId(channel == null ? null : channel.getIdLong());
	}

	@Nullable public String getJson() {
		return json;
	}

	public void setJson(@Nullable String json) {
		this.json = json;
	}

	@Nullable public JSONObject getJsonObject() {
		return json == null ? null : new JSONParser().parseObject(json);
	}

	public void setJsonObject(@Nullable JSONObject object) {
		setJson(object == null ? null : new JSONPrinter().toString(object));
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
					throw new IllegalStateException();
				return new GuildFactoidScope(guild);
			case "TextChannel":
				TextChannel channel = manager.getTextChannelById(channelId);
				if (channel == null)
					throw new IllegalStateException();
				return new TextChannelFactoidScope(channel);
		}
		throw new IllegalStateException();
	}

	public FactoidScope getScope(@Nonnull JDA jda) {
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
		scope.setInFactoidStore(this);
	}

	public void setFactoid(@Nonnull Factoid factoid) {
		setScopeType(factoid.getScopeType());
		setGuildId(factoid.getGuildId());
		setChannelId(factoid.getChannelId());
		setName(factoid.getName());
	}
}
package pl.shockah.dunlin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.json.JSONObject;

import javax.security.auth.login.LoginException;

public class ShardManager {
	public final App app;
	public final ThreadedEventListenerManager eventListenerManager = new ThreadedEventListenerManager();
	public final JDA[] shards;

	public ShardManager(App app) {
		this.app = app;
		this.shards = new JDA[app.getConfig().getObject("api").getInt("shards", 1)];
	}

	public void connect() throws LoginException, RateLimitedException {
		JDABuilder builder = createBuilder();
		for (int i = 0; i < shards.length; i++) {
			if (shards.length > 1)
				builder.useSharding(i, shards.length);
			shards[i] = builder.buildAsync();
		}
	}

	private JDABuilder createBuilder() {
		JSONObject apiConfig = app.getConfig().getObject("api");

		String accountTypeStr = apiConfig.getString("accountType", "BOT");
		AccountType accountType = null;
		for (AccountType o : AccountType.values()) {
			if (o.name().equalsIgnoreCase(accountTypeStr)) {
				accountType = o;
				break;
			}
		}
		if (accountType == null)
			throw new IllegalArgumentException(String.format("Unknown account type `%s`.", accountTypeStr));

		JDABuilder builder = new JDABuilder(accountType);
		builder.setToken(apiConfig.getString("token"));
		apiConfig.onString("game", game -> builder.setGame(Game.of(game)));
		builder.addEventListener(eventListenerManager);
		return builder;
	}

	public Guild getGuildById(String guildId) {
		if (shards.length == 0)
			return shards[0].getGuildById(guildId);
		return shards[(int)((Long.parseLong(guildId) >>> 22) % shards.length)].getGuildById(guildId);
	}

	public Guild getGuildById(long guildId) {
		if (shards.length == 0)
			return shards[0].getGuildById(guildId);
		return shards[(int)((guildId >>> 22) % shards.length)].getGuildById(guildId);
	}

	public User getUserById(String userId) {
		for (JDA jda : shards) {
			User user = jda.getUserById(userId);
			if (user != null)
				return user;
		}
		return null;
	}

	public User getUserById(long userId) {
		for (JDA jda : shards) {
			User user = jda.getUserById(userId);
			if (user != null)
				return user;
		}
		return null;
	}

	public TextChannel getTextChannelById(String textChannelId) {
		for (JDA jda : shards) {
			TextChannel textChannel = jda.getTextChannelById(textChannelId);
			if (textChannel != null)
				return textChannel;
		}
		return null;
	}

	public TextChannel getTextChannelById(long textChannelId) {
		for (JDA jda : shards) {
			TextChannel textChannel = jda.getTextChannelById(textChannelId);
			if (textChannel != null)
				return textChannel;
		}
		return null;
	}
}
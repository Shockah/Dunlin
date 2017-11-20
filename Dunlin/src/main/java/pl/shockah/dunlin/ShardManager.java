package pl.shockah.dunlin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.jay.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

public class ShardManager {
	@Nonnull public final App app;
	@Nonnull public final ThreadedEventListenerManager eventListenerManager = new ThreadedEventListenerManager();
	@Nonnull public final JDA[] shards;

	public ShardManager(@Nonnull App app) {
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

	@Nonnull private JDABuilder createBuilder() {
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

	@Nullable public Guild getGuildById(@Nonnull String guildId) {
		return shards[(int)((Long.parseLong(guildId) >>> 22) % shards.length)].getGuildById(guildId);
	}

	@Nullable public Guild getGuildById(long guildId) {
		return shards[(int)((guildId >>> 22) % shards.length)].getGuildById(guildId);
	}

	@Nullable public User getUserById(@Nonnull String userId) {
		for (JDA jda : shards) {
			User user = jda.getUserById(userId);
			if (user != null)
				return user;
		}
		return null;
	}

	@Nullable public User getUserById(long userId) {
		for (JDA jda : shards) {
			User user = jda.getUserById(userId);
			if (user != null)
				return user;
		}
		return null;
	}

	@Nullable public TextChannel getTextChannelById(@Nonnull String textChannelId) {
		for (JDA jda : shards) {
			TextChannel textChannel = jda.getTextChannelById(textChannelId);
			if (textChannel != null)
				return textChannel;
		}
		return null;
	}

	@Nullable public TextChannel getTextChannelById(long textChannelId) {
		for (JDA jda : shards) {
			TextChannel textChannel = jda.getTextChannelById(textChannelId);
			if (textChannel != null)
				return textChannel;
		}
		return null;
	}
}
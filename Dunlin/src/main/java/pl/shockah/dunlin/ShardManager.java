package pl.shockah.dunlin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

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
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(app.getConfig().getObject("api").getString("token"));
		app.getConfig().getObject("api").onString("game", game -> builder.setGame(Game.of(game)));
		builder.addListener(eventListenerManager);
		return builder;
	}

	public Guild getGuildById(String guildId) {
		if (shards.length == 0)
			return shards[0].getGuildById(guildId);
		return shards[(int)((Long.parseLong(guildId) >>> 22) % shards.length)].getGuildById(guildId);
	}

	public User getUserById(String userId) {
		for (JDA jda : shards) {
			User user = jda.getUserById(userId);
			if (user != null)
				return user;
		}
		return null;
	}
}
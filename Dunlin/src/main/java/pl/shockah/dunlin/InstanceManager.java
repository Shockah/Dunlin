package pl.shockah.dunlin;

import java.util.ArrayList;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.util.ReadWriteList;

public class InstanceManager {
	public final App app;
	public final ReadWriteList<JDA> instances = new ReadWriteList<>(new ArrayList<>());
	public final ThreadedEventListenerManager eventListenerManager = new ThreadedEventListenerManager();
	
	public InstanceManager(App app) {
		this.app = app;
	}
	
	protected void connect(int shards) throws LoginException, IllegalArgumentException, RateLimitedException {
		for (int i = 0; i < shards; i++) {
			connectShard(i, shards);
		}
	}
	
	private JDABuilder createBuilder() {
		return new JDABuilder(AccountType.BOT).setToken(app.getConfig().getObject("api").getString("token")).addListener(eventListenerManager);
	}
	
	private JDABuilder createBuilder(int shard, int shards) {
		return createBuilder().useSharding(shard, shards);
	}
	
	private void connectShard(int shard, int shards) throws LoginException, IllegalArgumentException, RateLimitedException {
		JDABuilder builder;
		if (shards == 1)
			builder = createBuilder();
		else
			builder = createBuilder(shard, shards);
		instances.add(builder.buildAsync());
	}
}
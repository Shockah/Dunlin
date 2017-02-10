package pl.shockah.dunlin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.security.auth.login.LoginException;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;

public class App {
	public static final Path CONFIG_PATH = Paths.get("config.json");
	
	public static void main(String[] args) {
		/*Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();
		});*/
		/*System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "[yyyy-MM-dd HH:mm:ss]");
		System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
		System.setProperty("org.slf4j.simpleLogger.showLogName", "false");
		System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false");*/
		new App().run();
	}
	
	private JSONObject config;
	private PluginManager pluginManager;
	private InstanceManager instanceManager;
	
	protected Path getConfigPath() {
		return CONFIG_PATH;
	}
	
	public void run() {
		try {
			initialize();
			main();
		} catch (IOException | LoginException | IllegalArgumentException | RateLimitedException e) {
			e.printStackTrace();
		} finally {
			deinitialize();
		}
	}
	
	private void initialize() throws IOException {
		loadConfig(getConfigPath());
		instanceManager = new InstanceManager(this);
		pluginManager = new PluginManager(this);
	}
	
	private EbeanServer createServer() {
		ServerConfig config = new ServerConfig();
		config.setName("db");
		
		Properties props = new Properties();
		props.putAll(this.config.getObject("database"));
		config.loadFromProperties(props);
		
		config.setDefaultServer(true);
		config.setRegister(true);
		
		return EbeanServerFactory.create(config);
	}
	
	private void main() throws LoginException, IllegalArgumentException, RateLimitedException {
		createServer();
		pluginManager.reload();
		instanceManager.connect(config.getObject("api").getInt("shards", 1));
	}
	
	private void deinitialize() {
	}
	
	protected void loadConfig(Path path) throws IOException {
		config = new JSONParser().parseObject(new String(Files.readAllBytes(path), "UTF-8"));
	}
	
	public JSONObject getConfig() {
		return config;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public InstanceManager getInstanceManager() {
		return instanceManager;
	}
}
package io.shockah.dunlin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.shockah.dunlin.db.DatabaseManager;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.util.UnexpectedException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class App {
	public static final Path CONFIG_PATH = Paths.get("config.json");
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();
		});
		/*System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "[yyyy-MM-dd HH:mm:ss]");
		System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
		System.setProperty("org.slf4j.simpleLogger.showLogName", "false");
		System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false");*/
		new App().run();
	}
	
	private JSONObject config;
	private DatabaseManager databaseManager;
	private PluginManager pluginManager;
	private JDA jda;
	private ThreadedEventListenerManager eventListenerManager;
	
	protected Path getConfigPath() {
		return CONFIG_PATH;
	}
	
	public void run() {
		try {
			loadConfig(getConfigPath());
			
			databaseManager = new DatabaseManager(this);
			pluginManager = new PluginManager(this);
			
			eventListenerManager = new ThreadedEventListenerManager();
			jda = new JDABuilder(AccountType.BOT).setToken(config.getObject("api").getString("token")).addListener(eventListenerManager).buildBlocking();
			pluginManager.reload();
		} catch (Exception e) {
			throw new UnexpectedException("Failed to initialize.", e);
		}
		
		try {
			if (databaseManager != null) {
				databaseManager.close();
			}
		} catch (Exception e) {
			throw new UnexpectedException("Failed to deinitialize.", e);
		}
	}
	
	protected void loadConfig(Path path) throws IOException {
		config = new JSONParser().parseObject(new String(Files.readAllBytes(path), "UTF-8"));
	}
	
	public JSONObject getConfig() {
		return config;
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public JDA getJDA() {
		return jda;
	}
	
	public ThreadedEventListenerManager getListenerManager() {
		return eventListenerManager;
	}
}
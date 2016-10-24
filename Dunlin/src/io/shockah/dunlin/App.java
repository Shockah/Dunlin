package io.shockah.dunlin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.shockah.dunlin.db.DatabaseManager;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class App extends ListenerAdapter {
	public static final Path CONFIG_PATH = Paths.get("config.json");
	
	public static void main(String[] args) {
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
	
	protected Path getConfigPath() {
		return CONFIG_PATH;
	}
	
	public void run() {
		try {
			loadConfig(getConfigPath());
			
			databaseManager = new DatabaseManager(this);
			pluginManager = new PluginManager(this);
			jda = new JDABuilder().setBotToken(config.getObject("api").getString("token")).addListener(this).buildBlocking();
			
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
}
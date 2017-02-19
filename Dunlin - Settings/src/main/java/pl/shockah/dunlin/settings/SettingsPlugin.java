package pl.shockah.dunlin.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrettyPrinter;
import pl.shockah.util.ReadWriteMap;

public class SettingsPlugin extends Plugin {
	public static final Path SETTINGS_PATH = Paths.get("pluginSettings.json");
	public static final TimeUnit DELAY_UNIT = TimeUnit.SECONDS;
	public static final long DELAY_UNITS = 3;
	
	protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	protected ScheduledFuture<?> scheduledSaveSettings;
	
	protected JSONObject settingsJson;
	protected boolean dirty = false;
	
	protected final ReadWriteMap<String, Setting<?>> settings = new ReadWriteMap<>(new HashMap<>());
	
	public SettingsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		settingsJson = new JSONObject();
		dirty = true;
		
		if (Files.exists(SETTINGS_PATH)) {
			try {
				settingsJson = new JSONParser().parseObject(new String(Files.readAllBytes(SETTINGS_PATH), "UTF-8"));
				dirty = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onUnload() {
		if (dirty) {
			saveSettings();
		}
	}
	
	public void register(Setting<?> setting) {
		settings.put(String.format("%s.%s", setting.plugin.info.packageName(), setting.name).toLowerCase(), setting);
	}
	
	public void unregister(Setting<?> setting) {
		settings.remove(String.format("%s.%s", setting.plugin.info.packageName(), setting.name).toLowerCase());
	}
	
	public Setting<?> getSetting(Plugin plugin, String name) {
		return settings.get(String.format("%s.%s", plugin.info.packageName(), name).toLowerCase());
	}
	
	public Setting<?> getSettingByName(String name) {
		return settings.get(name.toLowerCase());
	}
	
	protected JSONObject getSettingsObjectForReading(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings = null;
		for (int i = scope.ordinal(); i >= 0; i--) {
			scope = Scope.values()[i];
			settings = getSettingsObjectForReadingInOneScopeOrNull(scope, channel);
			if (settings != null)
				break;
		}
		if (settings == null)
			settings = new JSONObject();
		return settings.getObjectOrEmpty(plugin.info.packageName());
	}
	
	protected JSONObject getSettingsObjectForReadingInOneScopeOrNull(Scope scope, TextChannel channel) {
		JSONObject settings;
		if (scope == Scope.Global) {
			settings = settingsJson.getObject("global", null);
		} else {
			settings = settingsJson.getObjectOrEmpty("server").getObject(channel.getGuild().getId(), null);
			if (settings == null)
				return null;
			if (scope == Scope.Channel) {
				settings = settings.getObjectOrEmpty("channel").getObject(channel.getId(), null);
			}
		}
		return settings;
	}
	
	protected JSONObject getSettingsObjectForReadingInOneScope(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings;
		if (scope == Scope.Global) {
			settings = settingsJson.getObjectOrEmpty("global");
		} else {
			settings = settingsJson.getObjectOrEmpty("server").getObjectOrEmpty(channel.getGuild().getId());
			if (scope == Scope.Channel) {
				settings = settings.getObjectOrEmpty("channel").getObjectOrEmpty(channel.getId());
			}
		}
		return settings.getObjectOrEmpty(plugin.info.packageName());
	}
	
	protected JSONObject getSettingsObjectForWriting(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings;
		if (scope == Scope.Global) {
			settings = settingsJson.getObjectOrNew("global");
		} else {
			settings = settingsJson.getObjectOrNew("server").getObjectOrNew(channel.getGuild().getId());
			if (scope == Scope.Channel) {
				settings = settings.getObjectOrNew("channel").getObjectOrNew(channel.getId());
			}
		}
		return settings.getObjectOrNew(plugin.info.packageName());
	}
	
	protected synchronized void onSettingChange(Setting<?> setting) {
		if (scheduledSaveSettings != null)
			scheduledSaveSettings.cancel(false);
		scheduledSaveSettings = executor.schedule(this::saveSettings, DELAY_UNITS, DELAY_UNIT);
	}
	
	protected synchronized void saveSettings() {
		try {
			Files.write(SETTINGS_PATH, new JSONPrettyPrinter().toString(settingsJson).getBytes("UTF-8"));
			dirty = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		scheduledSaveSettings = null;
	}
}
package pl.shockah.dunlin.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.shockah.dunlin.Scope;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrettyPrinter;

public class SettingsPlugin extends ListenerPlugin {
	public static final Path SETTINGS_PATH = Paths.get("pluginSettings.json");
	public static final TimeUnit DELAY_UNIT = TimeUnit.SECONDS;
	public static final long DELAY_UNITS = 3;
	
	protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	protected ScheduledFuture<?> scheduledSaveSettings;
	
	protected JSONObject settings;
	protected boolean dirty = false;
	
	public SettingsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		settings = new JSONObject();
		dirty = true;
		
		if (Files.exists(SETTINGS_PATH)) {
			try {
				settings = new JSONParser().parseObject(new String(Files.readAllBytes(SETTINGS_PATH), "UTF-8"));
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
	
	protected JSONObject getSettingsObjectForReading(Scope scope, TextChannel channel, Plugin plugin) {
		for (int i = scope.ordinal(); i >= 0; i--) {
			scope = Scope.values()[i];
			JSONObject settings = getSettingsObjectForReadingInOneScopeOrNull(scope, channel, plugin);
			if (settings != null)
				return settings;
		}
		return null;
	}
	
	protected JSONObject getSettingsObjectForReadingInOneScopeOrNull(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings = null;
		if (scope == Scope.Global) {
			settings = this.settings.getObject("global", null);
		} else {
			settings = this.settings.getObjectOrEmpty("server").getObject(channel.getGuild().getId(), null);
			if (settings == null)
				return null;
			if (scope == Scope.Channel) {
				settings = settings.getObjectOrEmpty("channel").getObject(channel.getId(), null);
			}
		}
		if (settings == null)
			return null;
		return settings.getObjectOrEmpty(plugin.info.packageName());
	}
	
	protected JSONObject getSettingsObjectForReadingInOneScope(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings = null;
		if (scope == Scope.Global) {
			settings = this.settings.getObjectOrEmpty("global");
		} else {
			settings = this.settings.getObjectOrEmpty("server").getObjectOrEmpty(channel.getGuild().getId());
			if (scope == Scope.Channel) {
				settings = settings.getObjectOrEmpty("channel").getObjectOrEmpty(channel.getId());
			}
		}
		return settings.getObjectOrEmpty(plugin.info.packageName());
	}
	
	protected JSONObject getSettingsObjectForWriting(Scope scope, TextChannel channel, Plugin plugin) {
		JSONObject settings = null;
		if (scope == Scope.Global) {
			settings = this.settings.getObjectOrNew("global");
		} else {
			settings = this.settings.getObjectOrNew("server").getObjectOrNew(channel.getGuild().getId());
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
			Files.write(SETTINGS_PATH, new JSONPrettyPrinter().toString(settings).getBytes("UTF-8"));
			dirty = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		scheduledSaveSettings = null;
	}
}
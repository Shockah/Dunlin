package io.shockah.dunlin.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import io.shockah.dunlin.plugin.ListenerPlugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.json.JSONPrettyPrinter;

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
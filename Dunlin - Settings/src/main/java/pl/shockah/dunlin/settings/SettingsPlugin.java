package pl.shockah.dunlin.settings;

import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrettyPrinter;
import pl.shockah.util.ReadWriteMap;
import pl.shockah.util.ReadWriteSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SettingsPlugin extends Plugin {
	public static final Path SETTINGS_PATH = Paths.get("pluginSettings.json");
	public static final TimeUnit DELAY_UNIT = TimeUnit.SECONDS;
	public static final long DELAY_UNITS = 3;
	
	protected final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	protected ScheduledFuture<?> scheduledSaveSettings;
	
	protected JSONObject settingsJson;
	protected boolean dirty = false;
	
	protected final ReadWriteMap<String, Setting<?>> settings = new ReadWriteMap<>(new HashMap<>());
	protected final ReadWriteSet<SettingsListener> listeners = new ReadWriteSet<>(new LinkedHashSet<>());
	
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
		settings.put(setting.getFullName().toLowerCase(), setting);
	}
	
	public void unregister(Setting<?> setting) {
		settings.remove(setting.getFullName().toLowerCase());
	}

	public void registerListener(SettingsListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(SettingsListener listener) {
		listeners.remove(listener);
	}
	
	public Setting<?> getSetting(Plugin plugin, String name) {
		return getSettingByName(String.format("%s.%s", plugin.info.packageName(), name));
	}
	
	public Setting<?> getSettingByName(String name) {
		return settings.get(name.toLowerCase());
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
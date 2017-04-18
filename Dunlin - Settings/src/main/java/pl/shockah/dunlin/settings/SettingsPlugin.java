package pl.shockah.dunlin.settings;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import pl.shockah.dunlin.Scope;
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
	protected final ReadWriteSet<SettingListener> listeners = new ReadWriteSet<>(new LinkedHashSet<>());
	
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

	public void registerListener(SettingListener listener) {
		listeners.add(listener);
	}

	public void unregisterListener(SettingListener listener) {
		listeners.remove(listener);
	}
	
	public Setting<?> getSetting(Plugin plugin, String name) {
		return getSettingByName(String.format("%s.%s", plugin.info.packageName(), name));
	}
	
	public Setting<?> getSettingByName(String name) {
		return settings.get(name.toLowerCase());
	}

	public Object getSettingValue(Scope scope, TextChannel channel, Plugin plugin, String setting) {
		return getSettingValue(scope, channel, String.format("%s.%s", plugin.info.packageName(), setting));
	}

	public Object getSettingValue(Scope scope, TextChannel channel, String fullSettingName) {
		for (int i = scope.ordinal(); i >= 0; i--) {
			Object value = getSettingValueForScope(Scope.values()[i], channel, fullSettingName);
			if (value != null)
				return value;
		}
		return null;
	}

	public Object getSettingValueForScope(Scope scope, TextChannel channel, Plugin plugin, String setting) {
		return getSettingValueForScope(scope, channel, String.format("%s.%s", plugin.info.packageName(), setting));
	}

	public Object getSettingValueForScope(Scope scope, TextChannel channel, String fullSettingName) {
		if (!settingsJson.containsKey(fullSettingName))
			return null;

		JSONObject settingJson = settingsJson.getObject(fullSettingName);

		switch (scope) {
			case Channel: {
				if (channel == null)
					return null;

				String key = String.format("%s.%s", channel.getGuild().getId(), channel.getId());
				if (settingJson.containsKey(key))
					return settingJson.get(key);
			} break;
			case Server: {
				if (channel == null)
					return null;

				String key = channel.getGuild().getId();
				if (settingJson.containsKey(key))
					return settingJson.get(key);
			} break;
			case Global: {
				if (settingJson.containsKey("global"))
					return settingJson.get("global");
			} break;
		}

		return null;
	}

	public void setSettingValueForScope(Scope scope, TextChannel channel, Plugin plugin, String setting, Object value) {
		setSettingValueForScope(scope, channel, String.format("%s.%s", plugin.info.packageName(), setting), value);
	}

	public void setSettingValueForScope(Scope scope, TextChannel channel, String fullSettingName, Object value) {
		JSONObject settingJson = settingsJson.getObjectOrNew(fullSettingName);

		switch (scope) {
			case Channel: {
				if (channel == null)
					throw new IllegalArgumentException("Missing `channel` argument.");

				String key = String.format("%s.%s", channel.getGuild().getId(), channel.getId());
				settingJson.put(key, value);
				listeners.iterate(listener -> {
					listener.onSettingSet(fullSettingName, value, Scope.Channel, channel);
				});
			} break;
			case Server: {
				if (channel == null)
					throw new IllegalArgumentException("Missing `channel` argument.");

				String key = channel.getGuild().getId();
				settingJson.put(key, value);
				listeners.iterate(listener -> {
					listener.onSettingSet(fullSettingName, value, Scope.Server, channel);
				});
			} break;
			case Global: {
				settingJson.put("global", value);
				listeners.iterate(listener -> {
					listener.onSettingSet(fullSettingName, value, Scope.Global, null);
				});
			} break;
		}
	}

	public Object getUserSettingValue(User user, Plugin plugin, String setting) {
		return getUserSettingValue(user, String.format("%s.%s", plugin.info.packageName(), setting));
	}

	public Object getUserSettingValue(User user, String fullSettingName) {
		return settingsJson.getObjectOrEmpty(fullSettingName).getOrDefault(user.getId(), null);
	}

	public void setUserSettingValue(User user, Plugin plugin, String setting, Object value) {
		setUserSettingValue(user, String.format("%s.%s", plugin.info.packageName(), setting), value);
	}

	public void setUserSettingValue(User user, String fullSettingName, Object value) {
		settingsJson.getObjectOrNew(fullSettingName).put(user.getId(), value);
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
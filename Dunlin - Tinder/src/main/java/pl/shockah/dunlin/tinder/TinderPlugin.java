package pl.shockah.dunlin.tinder;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import pl.shockah.dunlin.commands.CommandsPlugin;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.dunlin.settings.GuildSettingScope;
import pl.shockah.dunlin.settings.Setting;
import pl.shockah.dunlin.settings.SettingsPlugin;
import pl.shockah.pintail.PluginInfo;
import pl.shockah.util.ReadWriteMap;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class TinderPlugin extends ListenerPlugin {
	@Nonnull private final CommandsPlugin commandsPlugin;
	@Nonnull private final SettingsPlugin settingsPlugin;

	@Nonnull final Setting<String> facebookTokenSetting;
	@Nonnull final Setting<String> categorySetting;
	@Nonnull final Setting<String> voteChannelSetting;
	@Nonnull final Setting<Boolean> voteHistoryEnabledSetting;
	@Nonnull final Setting<String> voteHistoryChannelSetting;

	private final ReadWriteMap<Guild, TinderInstance> tinderInstances = new ReadWriteMap<>(new HashMap<>());
	
	public TinderPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info, @Nonnull @RequiredDependency CommandsPlugin commandsPlugin, @Nonnull @RequiredDependency SettingsPlugin settingsPlugin) {
		super(manager, info);
		this.commandsPlugin = commandsPlugin;
		this.settingsPlugin = settingsPlugin;

		settingsPlugin.register(facebookTokenSetting = Setting.ofString(settingsPlugin, this, "facebookToken", null));
		settingsPlugin.register(categorySetting = Setting.ofString(settingsPlugin, this, "category", "Tinder"));
		settingsPlugin.register(voteChannelSetting = Setting.ofString(settingsPlugin, this, "voteChannel", "vote"));
		settingsPlugin.register(voteHistoryEnabledSetting = Setting.ofBool(settingsPlugin, this, "voteHistoryEnabled", true));
		settingsPlugin.register(voteHistoryChannelSetting = Setting.ofString(settingsPlugin, this, "voteHistoryChannel", "vote-history"));

		facebookTokenSetting.registerListener((setting, scope, value) -> {
			if (scope instanceof GuildSettingScope)
				setup(((GuildSettingScope)scope).guildScope.guild);
		});
	}

	@Override
	protected void onAllPluginsLoaded() {
		setupOnReady();
	}

	@Override
	protected void onReady(ReadyEvent event) {
		setupOnReady();
	}

	private void setupOnReady() {
		Arrays.stream(manager.app.getShardManager().shards)
				.filter(Objects::nonNull)
				.flatMap(shard -> shard.getGuilds().stream())
				.forEach(this::setup);
	}

	public void setup(Guild guild) {
		String facebookToken = facebookTokenSetting.get(new GuildSettingScope(guild));
		if (facebookToken == null)
			return;

		tinderInstances.writeOperation(tinderInstances -> {
			try {
				TinderInstance instance = tinderInstances.get(guild);
				if (instance == null) {
					instance = new TinderInstance(this, guild, facebookToken);
					tinderInstances.put(guild, instance);
				}
				instance.setup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
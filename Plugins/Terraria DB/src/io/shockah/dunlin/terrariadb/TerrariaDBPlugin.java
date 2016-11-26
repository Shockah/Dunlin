package io.shockah.dunlin.terrariadb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.shockah.dunlin.commands.CommandsPlugin;
import io.shockah.dunlin.plugin.ListenerPlugin;
import io.shockah.dunlin.plugin.PluginManager;

public class TerrariaDBPlugin extends ListenerPlugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	private ItemCommand itemCommand;
	
	private final List<Item> items = new ArrayList<>();
	
	public TerrariaDBPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommands(
			itemCommand = new ItemCommand(this)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommands(
			itemCommand
		);
	}
	
	public ItemCommand getItemCommand() {
		return itemCommand;
	}
	
	public List<Item> getItems() {
		return Collections.unmodifiableList(items);
	}
}
package io.shockah.dunlin.botcontrol;

import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.ErrorCommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.commands.ValueCommandResult;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ReloadPluginsCommand extends NamedCommand<Void, String> {
	private final BotControlPlugin plugin;
	
	public ReloadPluginsCommand(BotControlPlugin plugin) {
		super("reload");
		this.plugin = plugin;
	}

	@Override
	public Void parseInput(GenericMessageEvent e, String input) {
		return null;
	}

	@Override
	public CommandResult<String> call(CommandCall call, Void input) {
		//if (call.outputMedium == null)
		//	call.outputMedium = CommandCall.Medium.Private;
		if (!plugin.permissionsPlugin.permissionGranted(call.event.getAuthor(), plugin, names[0]))
			return new ErrorCommandResult<>("Permission required.");
		
		plugin.manager.reload();
		//if (call.outputMedium == null)
		//	call.outputMedium = CommandCall.Medium.Notice;
		return new ValueCommandResult<>("Done.");
	}
}
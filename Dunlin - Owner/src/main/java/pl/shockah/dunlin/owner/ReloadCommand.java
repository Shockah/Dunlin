package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ErrorCommandResultImpl;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;

public class ReloadCommand extends NamedCommand<Void, Void> {
    protected final OwnerPlugin ownerPlugin;

    public ReloadCommand(OwnerPlugin ownerPlugin) {
        super("reload");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public CommandResult<Void> parseInput(Message message, String textInput) {
        return new ParseCommandResultImpl<>(this, null);
    }

    @Override
    public CommandResult<Void> execute(Message message, Void aVoid) {
	    if (!ownerPlugin.permissionsPlugin.hasPermission(message, ownerPlugin, names[0]))
            return new ErrorCommandResultImpl<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        ownerPlugin.manager.reload();
        message.addReaction("\uD83D\uDC4C").queue();
        return new ValueCommandResultImpl<>(this, null);
    }

    @Override
    public Message formatOutput(Void aVoid) {
        return null;
    }
}
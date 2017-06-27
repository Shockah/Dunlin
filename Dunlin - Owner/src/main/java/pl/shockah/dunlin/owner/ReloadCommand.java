package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;

public class ReloadCommand extends NamedCommand<Void, Void> {
    protected final OwnerPlugin ownerPlugin;

    public ReloadCommand(OwnerPlugin ownerPlugin) {
        super("reload");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public ParseResult<Void> parseInput(Message message, String textInput) {
        return new ValueParseResult<>(this, null);
    }

    @Override
    public CommandResult<Void> execute(Message message, Void aVoid) {
	    if (!ownerPlugin.permissionsPlugin.hasPermission(message, ownerPlugin, names[0]))
            return new ErrorCommandResult<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        ownerPlugin.manager.reload();
        message.addReaction("\uD83D\uDC4C").queue();
        return new ValueCommandResult<>(this, null);
    }

    @Override
    public Message formatOutput(Void aVoid) {
        return null;
    }
}
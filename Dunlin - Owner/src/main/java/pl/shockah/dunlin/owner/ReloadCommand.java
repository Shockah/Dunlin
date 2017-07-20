package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;

public class ReloadCommand extends NamedCommand<Void, Void> {
    protected final OwnerPlugin ownerPlugin;

    public ReloadCommand(OwnerPlugin ownerPlugin) {
        super("reload");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    public ParseResult<Void> parseInput(CommandContext context, String textInput) {
        return new ValueParseResult<>(this, null);
    }

    @Override
    public CommandResult<Void, Void> execute(CommandContext context, Void aVoid) {
	    if (!ownerPlugin.permissionsPlugin.hasPermission(context.message, ownerPlugin, names[0]))
            return new ErrorCommandResult<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        ownerPlugin.manager.reloadAll();
        return new ValueCommandResult<>(this, null);
    }

    @Override
    public void output(CommandContext context, Void aVoid, CommandResult<Void, Void> outputResult) {
        context.message.addReaction("\uD83D\uDC4C").queue();
    }
}
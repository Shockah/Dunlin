package pl.shockah.dunlin.owner;

import pl.shockah.dunlin.commands.CommandContext;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReloadCommand extends NamedCommand<Void, Void> {
    @Nonnull protected final OwnerPlugin ownerPlugin;

    public ReloadCommand(@Nonnull OwnerPlugin ownerPlugin) {
        super("reload");
        this.ownerPlugin = ownerPlugin;
    }

    @Override
    @Nonnull public ParseResult<Void> parseInput(@Nonnull CommandContext context, @Nonnull String textInput) {
        return new ValueParseResult<>(this, null);
    }

    @Override
    @Nonnull public CommandResult<Void, Void> execute(@Nonnull CommandContext context, @Nullable Void aVoid) {
	    if (!ownerPlugin.permissionsPlugin.hasPermission(context.message, ownerPlugin, names[0]))
            return new ErrorCommandResult<>(this, ownerPlugin.permissionsPlugin.buildMissingPermissionMessage(ownerPlugin, names[0]));

        ownerPlugin.manager.reloadAll();
        return new ValueCommandResult<>(this, null);
    }

    @Override
    public void output(@Nonnull CommandContext context, @Nullable Void aVoid, @Nonnull CommandResult<Void, Void> outputResult) {
        context.message.addReaction("\uD83D\uDC4C").queue();
    }
}
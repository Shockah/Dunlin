package pl.shockah.dunlin.owner;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.shockah.dunlin.commands.NamedCommand;
import pl.shockah.dunlin.commands.result.CommandResult;
import pl.shockah.dunlin.commands.result.ParseCommandResultImpl;
import pl.shockah.dunlin.commands.result.ValueCommandResultImpl;
import pl.shockah.dunlin.plugin.PluginManager;

public class ReloadCommand extends NamedCommand<Void, Void> {
    protected final PluginManager pluginManager;

    public ReloadCommand(PluginManager pluginManager) {
        super("reload");
        this.pluginManager = pluginManager;
    }

    @Override
    public CommandResult<Void> parseInput(Message message, String textInput) {
        return new ParseCommandResultImpl<>(this, null);
    }

    @Override
    public CommandResult<Void> execute(Message message, Void aVoid) {
        //TODO: add permission checking
        pluginManager.reload();
        return new ValueCommandResultImpl<>(this, null);
    }

    @Override
    public Message formatOutput(Void aVoid) {
        return new MessageBuilder().append("Done.").build();
    }
}
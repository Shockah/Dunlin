package pl.shockah.dunlin.commands.result;

public interface ParseCommandResult<Output> extends CommandResult<Output> {
	Output get();
}
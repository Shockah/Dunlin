package pl.shockah.dunlin.commands.result;

public interface ValueCommandResult<Output> extends CommandResult<Output> {
	Output get();
}
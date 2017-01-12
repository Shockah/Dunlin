package io.shockah.dunlin.poll;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import io.shockah.dunlin.commands.CommandsPlugin;
import io.shockah.dunlin.plugin.ListenerPlugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.dunlin.poll.db.Poll;

public class PollPlugin extends ListenerPlugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;

	protected ScheduledExecutorService scheduler;
	
	private PollCommand pollCommand;
	
	public PollPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommands(
			pollCommand = new PollCommand(this)
		);
		
		scheduler = Executors.newScheduledThreadPool(4);
		
		for (Poll poll : manager.app.getDatabaseManager().query(Poll.class, qb -> { })) {
			poll.finishOrSchedule(this);
		}
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommands(
			pollCommand
		);
	}
	
	public PollCommand getPollCommand() {
		return pollCommand;
	}
	
	public void schedule(Poll poll) {
		scheduler.schedule(() -> {
			poll.finish(manager.app.getJDA());
		}, poll.date.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
	}
}
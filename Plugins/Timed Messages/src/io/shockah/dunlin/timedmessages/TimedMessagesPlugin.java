package io.shockah.dunlin.timedmessages;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import io.shockah.dunlin.MessageMedium;
import io.shockah.dunlin.plugin.Plugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.dunlin.timedmessages.db.TimedMessageEntry;

public class TimedMessagesPlugin extends Plugin {
	protected ScheduledExecutorService scheduler;
	
	public TimedMessagesPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		scheduler = Executors.newScheduledThreadPool(4);
		
		for (TimedMessageEntry entry : manager.app.getDatabaseManager().query(TimedMessageEntry.class, qb -> { })) {
			entry.removeOrSchedule(this);
		}
	}
	
	public void schedule(TimedMessageEntry entry) {
		scheduler.schedule(() -> {
			entry.remove(manager.app.getJDA());
		}, entry.date.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
	}
	
	public TimedMessageMedium createTimedMessageMedium(MessageMedium baseMedium, long delay) {
		return TimedMessageMedium.of(baseMedium, this, delay);
	}
}
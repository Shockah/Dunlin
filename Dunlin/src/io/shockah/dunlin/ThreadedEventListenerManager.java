package io.shockah.dunlin;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.shockah.dunlin.util.ReadWriteList;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public class ThreadedEventListenerManager extends ReadWriteList<EventListener> implements EventListener {
	private ExecutorService threadPool;
	
	public ThreadedEventListenerManager() {
		super(new ArrayList<>());
		threadPool = Executors.newCachedThreadPool();
	}

	@Override
	public void onEvent(Event event) {
		iterate(listener -> {
			threadPool.execute(() -> listener.onEvent(event));
		});
	}
}
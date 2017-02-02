package pl.shockah.dunlin.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import pl.shockah.util.UnexpectedException;
import pl.shockah.util.func.Func0;

public class Lazy<T> extends LazyInitializer<T> {
	public static <R> Lazy<R> of(Func0<R> func) {
		return new Lazy<R>(func);
	}
	
	protected final Func0<T> func;
	
	public Lazy(Func0<T> func) {
		this.func = func;
	}
	
	protected T initialize() throws ConcurrentException {
		return func.call();
	}
	
	public synchronized T get() {
		T obj = null;
		try {
			obj = super.get();
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
		return obj;
	}
}
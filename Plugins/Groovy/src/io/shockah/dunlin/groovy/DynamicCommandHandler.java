package io.shockah.dunlin.groovy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import groovy.lang.GroovyObjectSupport;
import io.shockah.dunlin.commands.Command;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.skylark.func.Func1;
import io.shockah.util.UnexpectedException;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class DynamicCommandHandler extends GroovyObjectSupport implements Map<String, Func1<Object, Object>> {
	protected final GroovyPlugin plugin;
	protected final GenericMessageEvent event;
	
	public DynamicCommandHandler(GroovyPlugin plugin, GenericMessageEvent event) {
		this.plugin = plugin;
		this.event = event;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		String skey = key.toString();
		return plugin.commandsPlugin.findCommand(event, skey) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Func1<Object, Object>>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Func1<Object, Object> get(Object key) {
		String skey = key.toString();
		return input -> {
			Command<Object, Object> objectCommand = (Command<Object, Object>)plugin.commandsPlugin.findCommand(event, skey);
			Object actualInput = input;
			try {
				actualInput = objectCommand.convertToInput(event, input);
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
			CommandResult<Object> result = objectCommand.call(new CommandCall(event), actualInput);
			if (result.error != null)
				throw new UnexpectedException(result.error);
			return result.value;
		};
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Func1<Object, Object> put(String key, Func1<Object, Object> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Func1<Object, Object>> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Func1<Object, Object> remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Func1<Object, Object>> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object invokeMethod(String methodName, Object args) {
		return super.invokeMethod(methodName, args);
		
		
		//TODO: this thing is broken, getting exceptions like ClassCastException: [Ljava.lang.Object; cannot be cast to java.lang.String
		/*Object obj = get(methodName);
		if (obj == null)
			throw new UnexpectedException(new NoSuchMethodException());
		for (Class<?> clazz : obj.getClass().getInterfaces()) {
			Method[] methods = clazz.getDeclaredMethods();
			if (methods.length == 1) {
				MethodClosure method = new MethodClosure(obj, methods[0].getName());
				return method.call(args);
			}
		}
		throw new UnsupportedOperationException();*/
	}
}
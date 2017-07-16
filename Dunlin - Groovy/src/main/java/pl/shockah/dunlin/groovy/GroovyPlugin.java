package pl.shockah.dunlin.groovy;

import groovy.lang.GroovyClassLoader;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;

import java.net.URL;
import java.net.URLClassLoader;

public class GroovyPlugin extends Plugin {
	public GroovyPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}

	@ClassLoaderProvider("groovy")
	public URLClassLoader createGroovyPluginClassLoader(ClassLoader parentClassLoader, URL[] urls) {
		GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader);
		for (URL url : urls) {
			cl.addURL(url);
		}
		return cl;
	}
}
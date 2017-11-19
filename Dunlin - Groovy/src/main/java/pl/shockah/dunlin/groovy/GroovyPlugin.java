package pl.shockah.dunlin.groovy;

import groovy.lang.GroovyClassLoader;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.plugin.PluginInfo;

import javax.annotation.Nonnull;
import java.net.URL;
import java.net.URLClassLoader;

public class GroovyPlugin extends Plugin {
	public GroovyPlugin(@Nonnull PluginManager manager, @Nonnull PluginInfo info) {
		super(manager, info);
	}

	@ClassLoaderProvider("groovy")
	public URLClassLoader createGroovyPluginClassLoader(@Nonnull ClassLoader parentClassLoader, @Nonnull URL[] urls) {
		GroovyClassLoader cl = new GroovyClassLoader(parentClassLoader);
		for (URL url : urls) {
			cl.addURL(url);
		}
		return cl;
	}
}
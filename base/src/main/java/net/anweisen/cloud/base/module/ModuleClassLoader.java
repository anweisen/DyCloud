package net.anweisen.cloud.base.module;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ModuleClassLoader extends URLClassLoader {

	private static final Collection<ModuleClassLoader> loaders = new CopyOnWriteArrayList<>();

	private final ClassLoader parent;

	private Module module;

	public ModuleClassLoader(@Nonnull URL jarFileUrl, @Nonnull ClassLoader parent) {
		super(new URL[] { jarFileUrl });
		this.parent = parent;
		loaders.add(this);
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			return this.loadClass0(name, resolve);
		} catch (ClassNotFoundException ex) {
		}

		for (ModuleClassLoader loader : loaders) {
			if (loader == this) continue;
			try {
				return loader.loadClass0(name, resolve);
			} catch (ClassNotFoundException ex) {
			}
		}

		try {
			return parent.loadClass(name);
		} catch (ClassNotFoundException ex) {
		}

		throw new ClassNotFoundException(name);
	}

	private Class<?> loadClass0(@Nonnull String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}

	@Override
	public void close() throws IOException {
		loaders.remove(this);
		super.close();
	}

	void setModule(@Nonnull Module module) {
		this.module = module;
	}

	@Nonnull
	public Module getModule() {
		return module;
	}
}

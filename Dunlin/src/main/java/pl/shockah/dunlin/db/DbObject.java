package pl.shockah.dunlin.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import pl.shockah.util.func.Action1;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;

public class DbObject<T> extends BaseDaoEnabled<T, Integer> {
	@DatabaseField(generatedId = true, columnName = ID)
	private int id;
	public static final String ID = "id";
	
	WeakReference<DatabaseManager> manager;
	
	@Deprecated //ORMLite-only
	protected DbObject() {
	}
	
	public DbObject(@Nonnull Dao<T, Integer> dao) {
		setDao(dao);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		DbObject<T> obj = (DbObject<T>)other;
		return id == obj.id;
	}
	
	public final int getId() {
		return id;
	}
	
	@Nonnull public final DatabaseManager getDatabaseManager() {
		DatabaseManager manager = this.manager.get();
		if (manager == null)
			throw new IllegalStateException();
		return manager;
	}
	
	@SuppressWarnings("unchecked")
	public final void update(@Nonnull Action1<T> func) {
		try {
			func.call((T)this);
			update();
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	public final int refresh() {
		try {
			return super.refresh();
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface TableVersion {
		int value() default 1;
	}
}
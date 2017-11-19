package pl.shockah.dunlin.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import pl.shockah.dunlin.App;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrettyPrinter;
import pl.shockah.util.UnexpectedException;
import pl.shockah.util.func.Action1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements Closeable {
	@Nonnull public static final Path TABLE_VERSIONS_PATH = Paths.get("dbTableVersions.json");
	
	@Nonnull public final App app;
	@Nonnull protected final ConnectionSource connection;
	
	private final Object lock = new Object();
	private final List<Class<?>> createdTables = new ArrayList<>();
	private final JSONObject tableVersions;
	
	public DatabaseManager(@Nonnull App app) {
		this.app = app;
		try {
			connection = new JdbcConnectionSource("jdbc:" + app.getConfig().getObject("database").getString("path"));
			DataPersisterManager.registerDataPersisters(new PatternPersister());
			DataPersisterManager.registerDataPersisters(new JSONObjectPersister());
			DataPersisterManager.registerDataPersisters(new JSONListPersister());
			
			if (Files.exists(TABLE_VERSIONS_PATH))
				tableVersions = new JSONParser().parseObject(new String(Files.readAllBytes(TABLE_VERSIONS_PATH), "UTF-8"));
			else
				tableVersions = new JSONObject();
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}
	
	@Nonnull public <T extends DbObject<T>> Dao<T, Integer> getDao(@Nonnull Class<T> clazz) {
		try {
			synchronized (lock) {
				Dao<T, Integer> dao = DaoManager.lookupDao(connection, clazz);
				if (dao == null)
					dao = DaoManager.createDao(connection, clazz);
				createTableIfNeeded(dao, clazz);
				return dao;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	private <T extends DbObject<T>> void createTableIfNeeded(@Nonnull Dao<T, Integer> dao, @Nonnull Class<T> clazz) {
		synchronized (lock) {
			if (createdTables.contains(clazz))
				return;
			try {
				TableUtils.createTableIfNotExists(connection, clazz);
				String databaseTable = getDatabaseTable(clazz);
				int tableVersion = getTableVersion(clazz);
				int oldTableVersion = tableVersions.getInt(databaseTable, 0);
				if (tableVersion > oldTableVersion) {
					if (oldTableVersion != 0) {
						try {
							Method method = clazz.getMethod("migrate", Dao.class, int.class, int.class);
							if (Modifier.isStatic(method.getModifiers())) {
								method.invoke(null, dao, oldTableVersion, tableVersion);
							}
						} catch (Exception e) {
							throw new UnexpectedException(e);
						}
					}
					tableVersions.put(databaseTable, tableVersion);
					saveTableVersions();
				}
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
			createdTables.add(clazz);
		}
	}
	
	@Nonnull private <T extends DbObject<T>> String getDatabaseTable(@Nonnull Class<T> clazz) {
		DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
		return databaseTable == null ? clazz.getSimpleName().toLowerCase() : databaseTable.tableName();
	}
	
	private <T extends DbObject<T>> int getTableVersion(@Nonnull Class<T> clazz) {
		DbObject.TableVersion tableVersion = clazz.getAnnotation(DbObject.TableVersion.class);
		return tableVersion == null ? 1 : tableVersion.value();
	}
	
	private void saveTableVersions() {
		try {
			Files.write(TABLE_VERSIONS_PATH, new JSONPrettyPrinter().toString(tableVersions).getBytes("UTF-8"));
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	@Nonnull private <T extends DbObject<T>> T createInternal(@Nonnull Class<T> clazz) {
		try {
			T obj = clazz.getConstructor(Dao.class).newInstance(getDao(clazz));
			obj.manager = new WeakReference<>(this);
			return obj;
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	@Nonnull public <T extends DbObject<T>> T create(@Nonnull Class<T> clazz) {
		try {
			T obj = createInternal(clazz);
			obj.create();
			return obj;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	@Nonnull public <T extends DbObject<T>> T create(@Nonnull Class<T> clazz, @Nonnull Action1<T> f) {
		try {
			T obj = createInternal(clazz);
			f.call(obj);
			obj.create();
			return obj;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	@Nullable public <T extends DbObject<T>> T select(@Nonnull Class<T> clazz, int id) {
		try {
			T obj = getDao(clazz).queryForId(id);
			if (obj != null)
				obj.manager = new WeakReference<>(this);
			return obj;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	@Nonnull public <T extends DbObject<T>> List<T> select(@Nonnull Class<T> clazz, @Nullable SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> q = getDao(clazz).queryBuilder();
			if (f != null)
				f.call(q);
			return q.query();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	@Nullable public <T extends DbObject<T>> T selectFirst(@Nonnull Class<T> clazz, @Nullable SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> q = getDao(clazz).queryBuilder();
			if (f != null)
				f.call(q);
			return q.queryForFirst();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public int delete(@Nonnull DbObject<?> obj) {
		try {
			return obj.delete();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public <T extends DbObject<T>> int delete(@Nonnull Class<T> clazz, @Nullable SQLExceptionWrappedAction1<DeleteBuilder<T, Integer>> f) {
		try {
			DeleteBuilder<T, Integer> q = getDao(clazz).deleteBuilder();
			if (f != null)
				f.call(q);
			return q.delete();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	public <T extends DbObject<T>> long count(@Nonnull Class<T> clazz) {
		try {
			return getDao(clazz).countOf();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		connection.close();
	}
}
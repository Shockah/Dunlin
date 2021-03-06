package io.shockah.dunlin.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Joiner;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import io.shockah.dunlin.util.Lazy;

public class StringListToSpaceDelimitedStringPersister extends BaseDataType {
	private static final Lazy<StringListToSpaceDelimitedStringPersister> singleton = Lazy.of(() -> new StringListToSpaceDelimitedStringPersister());
	
	public static StringListToSpaceDelimitedStringPersister getSingleton() {
		return singleton.get();
	}
	
	private StringListToSpaceDelimitedStringPersister() {
		super(SqlType.LONG_STRING, new Class<?>[] { List.class });
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		return defaultStr;
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}
	
	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		if (fieldType == null)
			return sqlArg;
		String str = ((String)sqlArg).trim();
		if (str.isEmpty())
			return new ArrayList<>();
		return Arrays.asList(str.split(" "));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return Joiner.on(" ").join((List<String>)javaObject);
	}
}
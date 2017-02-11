package pl.shockah.dunlin.db;

import java.sql.SQLException;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrinter;

public class JSONListPersister extends BaseDataType {
	public JSONListPersister() {
		super(SqlType.LONG_STRING, new Class<?>[] { JSONList.class });
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
		return new JSONParser().parseList((String)sqlArg);
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return new JSONPrinter().toString((JSONList<?>)javaObject);
	}
}
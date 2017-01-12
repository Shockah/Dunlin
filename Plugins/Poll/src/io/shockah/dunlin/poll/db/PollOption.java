package io.shockah.dunlin.poll.db;

import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.dunlin.db.DbObject;
import io.shockah.util.UnexpectedException;

@DatabaseTable(tableName = "io_shockah_dunlin_poll_polloption")
public class PollOption extends DbObject<PollOption> {
	public static final String POLL_COLUMN = "poll_id";
	
	@DatabaseField(canBeNull = false)
	public String emote;
	
	@DatabaseField(canBeNull = true)
	public String name;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = POLL_COLUMN)
	private Poll poll;
	
	@Deprecated //ORMLite-only
	protected PollOption() {
	}
	
	public PollOption(Dao<PollOption, Integer> dao) {
		super(dao);
	}
	
	public Poll getPoll() throws SQLException {
		try {
			if (poll != null)
				poll.refresh();
			return poll;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public void setPoll(Poll poll) {
		this.poll = poll;
	}
}
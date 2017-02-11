package pl.shockah.dunlin.db;

import java.io.IOException;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import pl.shockah.util.ReadWriteList;
import pl.shockah.util.func.Action1;
import pl.shockah.util.func.Action2;

public class ForeignCollectionWrapper<T> {
	public final ForeignCollection<T> collection;
	
	public ForeignCollectionWrapper(ForeignCollection<T> collection) {
		this.collection = collection;
	}
	
	public void iterate(Action1<T> f) {
		try (CloseableWrappedIterable<T> wrappedIterable = collection.getWrappedIterable()) {
			for (T obj : wrappedIterable) {
				f.call(obj);
			}
		} catch (IOException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void iterate(Action2<T, ReadWriteList.ReadIterator<T>> f) {
		try (CloseableWrappedIterable<T> wrappedIterable = collection.getWrappedIterable()) {
			new ReadWriteList.ReadIterator<T>(wrappedIterable.iterator()).iterate(f);
		} catch (IOException e) {
			throw new DatabaseException(e);
		}
	}
}
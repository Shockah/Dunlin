package pl.shockah.dunlin.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pl.shockah.dunlin.plugin.Plugin;
import pl.shockah.dunlin.plugin.PluginManager;
import pl.shockah.util.func.Action1;

public class HibernatePlugin extends Plugin {
	private SessionFactory sessionFactory;

	private Session session;

	public HibernatePlugin(PluginManager manager, Info info) {
		super(manager, info);
	}

	@Override
	protected void onLoad() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		session = sessionFactory.openSession();
	}

	@Override
	protected void onUnload() {
		if (session != null) {
			session.close();
			session = null;
		}

		sessionFactory.close();
	}

	public void transaction(Action1<Transaction> f) {
		session.beginTransaction();
		f.call(session.getTransaction());

		Transaction transaction = session.getTransaction();
		if (transaction != null && transaction.isActive())
			transaction.commit();
	}
}
package superstar.db.handlerbean;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import superstar.db.entity.Journal;
import superstar.db.handler.JournalHandler;

@Stateless
public class JournalHandlerBean implements JournalHandler {

	@PersistenceContext
	EntityManager em;

	@Override
	public void persist(List<Journal> load) {
		for (Journal journal : load) {
			em.persist(journal);
		}
	}

}

package superstar.db.handlerbean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import superstar.db.entity.Author;
import superstar.db.entity.Publication;
import superstar.db.handler.PublicationHandler;

@Stateless
public class PublicationHandlerBean implements PublicationHandler {
	@PersistenceContext
	EntityManager em;

	public PublicationHandlerBean() {
		super();
	}

	@Override
	public List<Publication> getAll() {
		return em.createQuery("from Publication p").getResultList();
	}

	@Override
	public int getSize() {
		return Integer.parseInt(Long.toString((Long) em.createQuery(
				"select count(p) from Publication p").getSingleResult()));
	}

	@Override
	public void persist(Publication pu1) {
		em.persist(pu1);
	}

	@Override
	public List<Publication> saveAll(List<Publication> pubLst) {
		List<Author> auLst;
		Author existing;
		for (Publication publication : pubLst) {
			auLst = new ArrayList<Author>();
			for (Author author : publication.getAULst()) {
				if ((existing = em.find(Author.class, author.getID())) != null)
					auLst.add(existing);
			}
			publication.setAULst(auLst);
			if (em.find(Publication.class, publication.getPMID()) != null)
				em.merge(publication);
			else
				em.persist(publication);
		}
		return pubLst;
	}

	@Override
	public void saveAllFromFile(List<Publication> pubLst) {
		Publication existPub;
		Author a;
		for (Publication pub : pubLst) {
			// check for authors
			for (String id : pub.authors.split(";")) {
				if ((a = em.find(Author.class, id)) != null) {
					pub.getAULst().add(a);
				}
			}
			// if no author then go on
			if (pub.getAULst().size() == 0)
				continue;
			// new publication
			if ((existPub = em.find(Publication.class, pub.getPMID())) == null) {
				em.persist(pub);
			}
			// old publication
			else {
				System.out.print(pub.getAULst().size() + "+" + existPub.getAULst().size() + "=");
				if (existPub.getAULst().removeAll(pub.getAULst())) {
					existPub.getAULst().addAll(pub.getAULst());
				}
				System.out.println(existPub.getAULst().size());
				em.merge(existPub);
			}
		}
	}

	@Override
	public void remove(Publication p) {
		em.remove(p);
	}
}

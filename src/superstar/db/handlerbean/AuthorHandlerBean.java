package superstar.db.handlerbean;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import superstar.db.entity.Author;
import superstar.db.entity.Publication;
import superstar.db.handler.AuthorHandler;

@Stateless
public class AuthorHandlerBean implements AuthorHandler {
	@PersistenceContext
	EntityManager em;

	public AuthorHandlerBean() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Author> getAll() {
		return em.createQuery("from Author au").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Author> getAuthorsByStatus(int status) {
		Query q = em.createQuery("from Author a where a.status=:status");
		q.setParameter("status", status);
		return q.getResultList();
	}

	@Override
	public Author getByID(String id) {
		return em.find(Author.class, id);
	}

	@Override
	public Author getComplete(String id) {
		Author a = em.find(Author.class, id);
		a.getPubs().size();
		return a;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Author> getNullAuthors() {
		return em.createQuery("from Author a where family is null").getResultList();
	}

	@Override
	public Author getRandom(int status) {
		Query q = em.createQuery("from Author a where status=:status order by rand()");
		q.setParameter("status", status);
		Author r = (Author) q.setMaxResults(1).getSingleResult();
		r.getPubs().size();
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Author> getRandomAuthorsByStatus(int status) {

		Query q = em.createQuery("from Author a where status=:status order by rand()");
		q.setParameter("status", status);
		return q.getResultList();
	}

	@Override
	public void persist(Author author) {
		if (!em.contains(author))
			em.persist(author);
		else
			em.refresh(author);
	}

	@Override
	public void removeAuthor(String id) {
		System.out.println("remove author " + id);
		Author a = em.find(Author.class, id);
		for (Publication pub : (a).getPubs()) {
			pub.getAULst().remove(a);
		}
		em.flush();
		em.remove(a);
	}

	@Override
	public void removeNullAuthor() {
		for (Object a : em.createQuery("from Author a where family is null").getResultList()) {
			System.out.println(((Author) a).getID());
			for (Publication pub : ((Author) a).getPubs()) {
				pub.getAULst().remove(a);
			}
		}
		em.flush();
		System.out.println(em.createQuery("delete from Author a where a.family is null")
				.executeUpdate());
		for (Object o : em.createQuery("from Publication p").getResultList()) {
			if (((Publication) o).getAULst().size() == 0)
				em.remove(o);
		}
	}

	@Override
	public void saveAll(List<Author> list) {
		for (Author author : list) {
			if (em.find(Author.class, author.getID()) == null)
				em.persist(author);
			else {
				em.merge(author);
			}
		}
	}

	@Override
	public void updateAuthor(Author author) {
		em.merge(author);
	}
}

package superstar.logic;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import superstar.db.entity.Author;
import superstar.db.entity.Journal;
import superstar.db.entity.Publication;
import superstar.db.handler.AuthorHandler;
import superstar.db.handler.JournalHandler;
import superstar.db.handler.PublicationHandler;

public class DBConnector {

	private AuthorHandler ah;
	private PublicationHandler ph;
	private JournalHandler jh;

	public DBConnector(InitialContext ic) {
		try {
			ah = (AuthorHandler) ic.lookup("AuthorHandlerBean/local");
			ph = (PublicationHandler) ic.lookup("PublicationHandlerBean/local");
			jh = (JournalHandler) ic.lookup("JournalHandlerBean/local");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public List<Publication> getAllPublication() {
		return ph.getAll();
	}

	public Author getAuthorByID(String ID) {
		return ah.getByID(ID);
	}

	public List<Author> getAuthors() {
		return ah.getAll();
	}

	public List<Author> getAuthorsByStatus(int status) {
		return ah.getAuthorsByStatus(status);
	}

	public Author getCompleteAuthor(Author s) {
		return ah.getComplete(s.getID());
	}

	public List<Author> getNullAuthors() {
		return ah.getNullAuthors();
	}

	public Author getRandomAuthor(int status) {
		return ah.getRandom(status);
	}

	public List<Author> getRandomAuthors(int status) {
		return ah.getRandomAuthorsByStatus(status);
	}

	public void removeAuthor(String id) {
		ah.removeAuthor(id);
	}

	public void removeNullAuthor() {
		ah.removeNullAuthor();
	}

	public void saveAuthor(Author a) {
		ah.updateAuthor(a);
	}

	public void saveAuthors(List<Author> al) {
		ah.saveAll(al);
	}

	public void saveJournals(List<Journal> load) {
		jh.persist(load);
	}

	public void savePublication(Publication pu1) {
		ph.persist(pu1);
	}

	public List<Publication> savePublications(List<Publication> pubLst) {
		return ph.saveAll(pubLst);
	}

	public void savePublicationsFromFile(List<Publication> pubLst) {
		ph.saveAllFromFile(pubLst);
	}

	public void removePublication(Publication p) {
		ph.remove(p);
	}

}

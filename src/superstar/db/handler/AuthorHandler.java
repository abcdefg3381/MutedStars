package superstar.db.handler;

import java.util.List;

import superstar.db.entity.Author;

public interface AuthorHandler {

	List<Author> getAll();

	List<Author> getAuthorsByStatus(int status);

	Author getByID(String id);

	Author getComplete(String id);

	List<Author> getNullAuthors();

	Author getRandom(int status);

	List<Author> getRandomAuthorsByStatus(int status);

	void persist(Author author);

	void removeAuthor(String id);

	/**
	 * Cleaning up the database
	 */
	void removeNullAuthor();

	void saveAll(List<Author> al);

	void updateAuthor(Author author);
}

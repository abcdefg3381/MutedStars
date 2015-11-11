package superstar.db.handler;

import java.util.List;

import superstar.db.entity.Publication;

public interface PublicationHandler {

	List<Publication> getAll();

	int getSize();

	void persist(Publication pu1);

	List<Publication> saveAll(List<Publication> pubLst);

	void saveAllFromFile(List<Publication> pubLst);

	void remove(Publication p);

}

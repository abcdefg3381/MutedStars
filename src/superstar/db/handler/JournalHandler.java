package superstar.db.handler;

import java.util.List;

import superstar.db.entity.Journal;

public interface JournalHandler {

	void persist(List<Journal> load);

}

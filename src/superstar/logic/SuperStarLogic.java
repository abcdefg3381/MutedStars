package superstar.logic;

import java.util.ArrayList;
import java.util.List;

import superstar.MainProgram;
import superstar.db.entity.Author;
import superstar.db.entity.Publication;
import superstar.db.entity.StarNetwork;

public class SuperStarLogic {

	private DBConnector dbc;

	private StarNetwork network = new StarNetwork();
	private StarNetwork rootNetwork = new StarNetwork();
	private List<Publication> pubs;
	private List<Author> authors;

	public SuperStarLogic() {
		this.dbc = MainProgram.getInstance().getDBC();
	}

	public void formRootNetwork(Object[] stars) {
		pubs = new ArrayList<Publication>();
		authors = new ArrayList<Author>();
		// first tier links
		for (Object s : stars) {
			Author a = dbc.getCompleteAuthor((Author) s);
			for (Publication p : a.getPubs()) {
				if (!pubs.contains(p)) {
					pubs.add(p);
				}
			}
		}
		// second tier links
		for (Publication p : pubs) {
			for (Author a : p.getAULst()) {
				if (!authors.contains(a)) {
					authors.add(dbc.getCompleteAuthor(a));
				}
			}
		}
		for (Author a : authors) {
			for (Publication ap : a.getPubs()) {
				if (!pubs.contains(ap))
					pubs.add(ap);
			}
		}
		// third tier links
		for (Publication p : pubs) {
			for (Author a : p.getAULst()) {
				if (!authors.contains(a)) {
					authors.add(dbc.getCompleteAuthor(a));
				}
			}
		}
		for (Author a : authors) {
			for (Publication ap : a.getPubs()) {
				if (!pubs.contains(ap))
					pubs.add(ap);
			}
		}
		rootNetwork = new StarNetwork();
		rootNetwork.formNetwork(authors, pubs);
		network = new StarNetwork();
		network.formNetwork(authors, pubs);
	}

	public StarNetwork getNetwork() {
		return network;
	}

	public StarNetwork getRootNetwork() {
		return rootNetwork;
	}

	public List<Author> retrieveDiedStars() {
		List<Author> diedStars = new ArrayList<Author>();
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN_NEIGHBORED));
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN));
		return diedStars;
	}

	public void setTime(int yearStart, int yearEnd) {
		pubs = new ArrayList<Publication>();
		authors = new ArrayList<Author>();
		for (Publication p : rootNetwork.getPubs()) {
			if (p.getYear() >= yearStart && p.getYear() <= yearEnd) {
				pubs.add(p);
			}
		}
		for (Publication p : pubs) {
			for (Author a : p.getAULst()) {
				if (!authors.contains(a)) {
					authors.add(a);
				}
			}
		}
		System.out.println(authors.size());
		network = new StarNetwork();
		network.formNetwork(authors, pubs);
	}
}

package superstar.db.entity;

import java.util.ArrayList;
import java.util.List;

import maggie.network.entity.Edge;
import edu.uci.ics.jung.graph.util.Pair;

public class CoAuthorship extends Edge {

	private List<Publication> pubs;

	public CoAuthorship(Author author1, Author author2) {
		pair = new Pair<Author>(author1, author2);
		pubs = new ArrayList<Publication>();
	}

	public void addPub(Publication p) {
		pubs.add(p);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CoAuthorship) {
			CoAuthorship cas = (CoAuthorship) obj;
			if (cas.getPair().getFirst().equals(this.getPair().getFirst())
					&& cas.getPair().getSecond().equals(this.getPair().getSecond()))
				return true;
			if (cas.getPair().getSecond().equals(this.getPair().getFirst())
					&& cas.getPair().getFirst().equals(this.getPair().getSecond()))
				return true;
		}
		return false;
	}

	public int getCount() {
		return pubs.size();
	}

	public int getCount(int start, int end) {
		int count = 0;
		for (Publication p : pubs) {
			if (p.getYear() >= start && p.getYear() <= end)
				count++;
		}
		return count;
	}

	@Override
	public Pair<Author> getPair() {
		return (Pair<Author>) pair;
	}

	@Override
	public boolean getType() {
		return false;
	}
}

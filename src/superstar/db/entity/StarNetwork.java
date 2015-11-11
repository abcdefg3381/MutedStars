package superstar.db.entity;

import java.util.ArrayList;
import java.util.List;

import maggie.network.entity.Network;

public class StarNetwork extends Network {

	private List<Publication> pubs;
	private int year = 3000;
	private int[][] connectivityTable;

	public StarNetwork() {
		super();
		edgeList = new ArrayList<CoAuthorship>();
		nodeList = new ArrayList<Author>();
		pubs = new ArrayList<Publication>();
	}

	public StarNetwork(List<Author> nodeList, List<Publication> pubs) {
		super();
		edgeList = new ArrayList<CoAuthorship>();
		this.nodeList = nodeList;
		this.pubs = pubs;
		formEdgeAdjMatrix();
	}

	public float calcClustCoeff() {
		connectivityTable = new int[adjMatrix.length][adjMatrix.length];
		for (int i = 0; i < adjMatrix.length; i++) {
			for (int j = 0; j < adjMatrix.length; j++) {
				if (adjMatrix[i][j] > 0)
					connectivityTable[i][j] = 1;
			}
		}

		int n = getNodeList().size();
		if (n == 0)
			return 0f;
		// number of triangle and triples
		int[][] tt = new int[n][2];

		// iterate graph table to find triangles
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (connectivityTable[i][j] == 1) {
					for (int k = 0; k < n; k++) {
						if (connectivityTable[j][k] == 1) {
							tt[j][1]++;
							if (connectivityTable[k][i] == 1) {
								tt[j][0]++;
							}
						}
					}
				}
			}
		}

		float c1 = 0, c2 = 0, t1 = 0, t2 = 0;
		for (int[] element : tt) {
			int triangle = element[0];
			int triple = element[1];
			if (triple != 0) {
				c1 += (float) triangle / triple;
			}
			t1 += triangle;
			t2 += triple;
		}
		// first algorithm
		if (n == 0)
			c1 = 0;
		else
			c1 /= n;
		// second algorithm
		if (t2 == 0)
			c2 = 0;
		else
			c2 = t1 / t2;
		// return f.format(c1) + "\t" + f.format(c2);
		return c1;
		// XXX: use c.c. = 1/N*sum(tra i/tri i)
		// + "\t" + f.format(c2);
	}

	public float calcNetworkEffic() {
		if (adjMatrix.length < 2)
			return 0f;
		// prepare a binary matrix
		connectivityTable = new int[adjMatrix.length][adjMatrix.length];
		for (int i = 0; i < adjMatrix.length; i++) {
			for (int j = 0; j < adjMatrix.length; j++) {
				if (adjMatrix[i][j] > 0)
					connectivityTable[i][j] = 1;
				else
					connectivityTable[i][j] = 512;
			}
		}

		// Run Floyd's Algorithm to calculate shortest distance between each
		// pair of nodes
		for (int i = 0; i < connectivityTable.length; i++) {
			for (int j = 0; j < connectivityTable.length; j++) {
				int[] subTable = connectivityTable[i];
				if (i != j) // skip over the current row
				{
					for (int k = 0; k < subTable.length; k++) {
						if (k != i) // skip over the current column of
						// iteration
						{
							connectivityTable[j][k] = Math.min(connectivityTable[j][k],
									connectivityTable[j][i] + connectivityTable[i][k]);
						}
					}
				}
			}
		}

		// calculate network efficiency
		float ne = 0;
		for (int i = 0; i < connectivityTable.length; i++) {
			for (int j = 0; j < connectivityTable.length; j++) {
				ne += (float) 1 / connectivityTable[i][j];
			}
		}
		ne /= connectivityTable.length;
		ne /= (connectivityTable.length - 1);
		return ne;
	}

	private void formEdgeAdjMatrix() {
		// form edges and adj. matrix
		edgeList.clear();
		float[][] matrix = new float[getNodeList().size()][getNodeList().size()];
		CoAuthorship cas;
		int row, column;
		for (Publication pub : pubs) {
			for (int i = 0; i < pub.getAULst().size(); i++) {
				row = getNodeList().indexOf(pub.getAULst().get(i));
				if (row == -1)
					continue;
				pub.getAULst().set(i, getNodeList().get(row));
				for (int j = i + 1; j < pub.getAULst().size(); j++) {
					column = getNodeList().indexOf(pub.getAULst().get(j));
					if (column == -1)
						continue;
					pub.getAULst().set(j, getNodeList().get(column));
					cas = new CoAuthorship(pub.getAULst().get(i), pub.getAULst().get(j));
					if (!getEdgeList().contains(cas)) {
						cas.addPub(pub);
						getEdgeList().add(cas);
					} else
						getEdgeList().get(getEdgeList().indexOf(cas)).addPub(pub);
					// adjacency matrix
					matrix[row][column]++;
					matrix[column][row]++;
				}
			}
		}
		setAdjMatrix(matrix);
	}

	public void formNetwork(List<Author> authorLst, List<Publication> pubLst) {
		getNodeList().addAll(authorLst);
		getPubs().addAll(pubLst);
		formEdgeAdjMatrix();
	}

	/**
	 * @param authors
	 * @param pubs
	 * @param firstYear
	 *            inclusive
	 * @param window
	 *            firstYear+window exclusive
	 */
	public void formNetwork(List<Author> authors, List<Publication> pubs, int firstYear, int window) {
		getPubs().clear();
		getNodeList().clear();
		for (Publication pub : pubs) {
			if (pub.getYear() >= firstYear && pub.getYear() < firstYear + window)
				getPubs().add(pub);
		}
		for (Publication p : getPubs()) {
			for (Author a : p.getAULst()) {
				if (authors.contains(a) && !getNodeList().contains(a))
					getNodeList().add(authors.get(authors.lastIndexOf(a)));
			}
		}
		formEdgeAdjMatrix();
	}

	/**
	 * 1 publication is counted N times if there are N authors
	 * 
	 * @return
	 */
	public float getAvgPubCount() {
		int count = 0;
		for (Publication pub : getPubs()) {
			for (Author a : pub.getAULst()) {
				if (getNodeList().contains(a))
					count++;
			}
		}
		if (getNodeList().size() == 0)
			return 0;
		else
			return (float) count / getNodeList().size();
	}

	@Override
	public List<CoAuthorship> getEdgeList() {
		return (List<CoAuthorship>) edgeList;
	}

	public int getFirtYear() {
		if (year == 3000) {
			for (Publication p : pubs) {
				if (p.getYear() < year)
					year = p.getYear();
			}
		}
		return year;
	}

	public int getNetworkPubsCount() {
		int count = 0;
		for (Publication pub : pubs) {
			pub.getAULst().retainAll(getNodeList());
			if (pub.getAULst().size() > 1) {
				count++;
			}
		}
		return count;
	}

	@Override
	public List<Author> getNodeList() {
		return (List<Author>) nodeList;
	}

	public List<Publication> getPubs() {
		return pubs;
	}

	public void setPubs(List<Publication> pubs) {
		this.pubs = pubs;
	}
}

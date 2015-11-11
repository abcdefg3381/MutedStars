package superstar.logic;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import maggie.common.utils.ReportWriter;
import maggie.network.gui.GuiUtils;
import superstar.MainProgram;
import superstar.db.entity.Author;
import superstar.db.entity.CoAuthorship;
import superstar.db.entity.Publication;
import superstar.db.entity.StarNetwork;
import superstar.gui.GraphViewer;
import superstar.logic.util.PublicationUtils;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class Calculation extends ReportWriter implements ClipboardOwner {
	// database connection layer
	private DBConnector dbc;

	private GraphViewer gv;

	private NumberFormat f;

	List<Author> oldfirst = new ArrayList<Author>();

	List<Author> oldsecond = new ArrayList<Author>();

	public Calculation() {

		System.out.println("entered calculation mode");
		f = new DecimalFormat("0.00");
		dbc = MainProgram.getInstance().getDBC();

		// analyzeStarNeighborsNetwork();
		// analyzeStarNeighborsCollaboration();
		// longtermCollabStat();

		System.out.println("getting authors");
		List<Author> input = new ArrayList<Author>();
		input.addAll(dbc.getAuthorsByStatus(1));
		input.addAll(dbc.getAuthorsByStatus(2));
		input.addAll(dbc.getAuthorsByStatus(3));
		input.addAll(dbc.getAuthorsByStatus(4));
		input.addAll(dbc.getAuthorsByStatus(5));
		input.addAll(dbc.getAuthorsByStatus(7));
		input.addAll(dbc.getAuthorsByStatus(8));
		input.addAll(dbc.getAuthorsByStatus(11));
		System.out.println(input.size() + " authors retrieved");

		System.out.println("outputing degree distribution");
		analyzeSampleNetworkStatistics(input, 1961, 2011, 50);
	}

	/**
	 * calculates degree distribution, dynamic rewiring and publication change
	 * of a sample of randomly selected authors during a period of time.
	 * 
	 * @param authors
	 * @param yearStart
	 * @param yearEnd
	 * @param window
	 */
	private void analyzeSampleNetworkStatistics(List<Author> authors, int yearStart, int yearEnd,
			int window) {
		int[] degrees, pubs;
		int maxDegree = 0, interval = 5;

		// get degree distribution of all time
		degrees = new int[authors.size()];
		pubs = new int[authors.size()];
		int count = 0, sum = 0, degree;
		for (Author author : authors) {
			System.out.println(count + "/" + authors.size());
			author = dbc.getCompleteAuthor(author);
			degree = PublicationUtils.getAllAuthors(author.getPubs()).size();
			// if (degree > 20) {
			degrees[count] = degree;
			maxDegree = degrees[count] > maxDegree ? degrees[count] : maxDegree;
			sum += degrees[count];
			// }
			pubs[count] = author.getPubs().size();
			count++;
		}
		int[] distribution = new int[maxDegree / interval + 1];
		for (int d : degrees) {
			distribution[d / interval]++;
		}
		System.out.println("degree distribution:");
		for (int i = 0; i < distribution.length; i++) {
			System.out.print(i * interval + "~" + (i * interval + interval) + "\t");
		}
		System.out.println();
		for (int i : distribution) {
			System.out.print(i + "\t");
		}
		System.out.println();

		//
		// // average degree
		// System.out.println("average degree:\t" + (float) sum / count);
		//
		// // degree - publication correlation
		// for (int i = 0; i < authors.size(); i++) {
		// System.out.print(degrees[i] + "\t");
		// }
		// System.out.println();
		// for (int i = 0; i < authors.size(); i++) {
		// System.out.print(pubs[i] + "\t");
		// }
		// System.out.println();

		// // get degree distribution vs time
		// System.out.println("degree distribution by year:");
		// System.out.print("interval\t");
		// for (int i = 0; i < distribution.length; i++) {
		// System.out.print((interval * i) + "~" + (interval * i + interval) +
		// "\t");
		// }
		// System.out.println();
		// for (int year = yearStart; year < yearEnd; year++) {
		// distribution = new int[maxDegree / interval];
		// System.out.print("year " + year + ":\t");
		// for (Author author : authors) {
		// neighbors = new ArrayList<Author>();
		// for (Publication pub : author.getPubs()) {
		// if (pub.getYear() >= year && pub.getYear() < year + window) {
		// neighbors.removeAll(pub.getAULst());
		// neighbors.addAll(pub.getAULst());
		// }
		// }
		// distribution[neighbors.size() / interval] += 1;
		// }
		// for (int i = 0; i < distribution.length; i++) {
		// System.out.print(distribution[i] + "\t");
		// }
		// System.out.println();
		// }

		// // get connection change and publication vs time
		// System.out.println("change by year:");
		// neighbors = new ArrayList<Author>();
		// List<Author> lastNeighbors = neighbors;
		// List<Author> union, intersection;
		// int pubCount;
		// int[][] changeAndPublication = new int[yearEnd - yearStart +
		// window][6];
		// // for every author
		// for (Author author : authors) {
		// // calculate change and publication in every window
		// for (int year = yearStart; year < yearEnd; year++) {
		// // reset pub count and neighbors
		// pubCount = 0;
		// neighbors = new ArrayList<Author>();
		// // get new neighbors and pub count
		// for (Publication pub : author.getPubs()) {
		// if (pub.getYear() >= year && pub.getYear() < year + window) {
		// neighbors.removeAll(pub.getAULst());
		// neighbors.addAll(pub.getAULst());
		// pubCount++;
		// }
		// }
		//
		// // get dynamic rewiring
		// union = new ArrayList<Author>();
		// union.addAll(neighbors);
		// union.removeAll(lastNeighbors);
		// union.addAll(lastNeighbors);
		// intersection = new ArrayList<Author>();
		// intersection.addAll(neighbors);
		// intersection.retainAll(lastNeighbors);
		//
		// // add to year's data
		// changeAndPublication[year - yearStart][0] += neighbors.size();
		// changeAndPublication[year - yearStart][1] += (union.size() -
		// intersection.size());
		// changeAndPublication[year - yearStart][2] += pubCount;
		//
		// // update last neighbors
		// lastNeighbors = neighbors;
		// }
		// }
		// System.out.println("\tco-authors\trewiring\tpublications");
		// for (int year = yearStart; year < yearEnd; year++) {
		// System.out.println("year " + year + "\t" + changeAndPublication[year
		// - yearStart][0]
		// + "\t" + changeAndPublication[year - yearStart][1] + "\t"
		// + changeAndPublication[year - yearStart][2]);
		// }
	}

	private void analyzeStarNeighborsCollaboration() {
		starNeighborsCollaborationStat(dbc.getAuthorsByStatus(Author.SUPERSTAR_HARVESTED), "normal");
		starNeighborsCollaborationStat(dbc.getAuthorsByStatus(Author.SUPERSTAR_EMERITUS),
				"emeritus");
		// // died
		List<Author> diedStars = new ArrayList<Author>();
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_ANTICIPATED));
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_ANTICIPATED_NEIGHBORED));
		starNeighborsCollaborationStat(diedStars, "anticipated");
		diedStars.clear();
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN));
		diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN_NEIGHBORED));
		starNeighborsCollaborationStat(diedStars, "sudden");
	}

	private void analyzeStarNeighborsNetwork() {
		// living superstars
		// starNeighborsNetwork(dbc.getAuthorsByStatus(Author.SUPERSTAR_HARVESTED),
		// "normal");

		// high degree nodes
		egocentricNetwork(dbc.getAuthorsByStatus(Author.HIGH_DEGREE), "high");

		// emeritus superstars
		// starNeighborsNetwork(dbc.getAuthorsByStatus(Author.SUPERSTAR_EMERITUS),
		// "emeritus");

		// died superstars
		// List<Author> diedStars = new ArrayList<Author>();
		// diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN));
		// diedStars.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN_NEIGHBORED));
		// egocentricNetwork(diedStars, "died");
	}

	private void cleanDatabase() {

		StringBuilder toclipboard = new StringBuilder();
		toclipboard.append("x=[");
		Author s = dbc.getCompleteAuthor(dbc.getAuthorByID("fay fs"));
		for (Publication p : s.getPubs()) {
			toclipboard.append(p.getYear() + " ");
		}
		toclipboard.append("];hist(x,100);");
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(toclipboard.toString()), this);

		for (Publication p : s.getPubs()) {
			if (p.getYear() > 2003) {
				dbc.removePublication(p);
			}
		}

	}

	/**
	 * draws the network to file
	 * 
	 * @param filename
	 * 
	 * @param network
	 */
	private void draw(StarNetwork network) {
		// convert network to jung graph
		Graph<Author, CoAuthorship> g = new UndirectedSparseGraph<Author, CoAuthorship>();
		for (Author a : network.getNodeList()) {
			g.addVertex(a);
		}
		for (CoAuthorship fe : network.getEdgeList()) {
			g.addEdge(fe, fe.getPair());
		}

		// create graph viewer
		Layout<Author, CoAuthorship> layout = new KKLayout<Author, CoAuthorship>(g);
		gv = new GraphViewer(layout, network);
		gv.setSize(600, 600);
		gv.revalidate();
		gv.setBackground(Color.WHITE);

		// scale graph
		// final ScalingControl scaler = new CrossoverScalingControl();
		// gv.scaleToLayout(scaler);
		// scaler.scale(gv, 2.0f, gv.getCenter());
	}

	private File drawSnapshotToFile(String filename, int yearStart, int windowSize) {
		gv.setTime(yearStart, windowSize);
		File f = new File("./" + filename + ".bmp");
		GuiUtils.drawComponentToFile(gv, f, "bmp");
		return f;
	}

	/**
	 * draws the star's T-2 network. <br>
	 * e.g. drawStar("giorgi jv", -4, 5);
	 * 
	 * @param starname
	 *            the superstar's name
	 * @param offset
	 *            the offset in year of the star's last publication
	 * @param windowSize
	 *            the window size of the network
	 */
	private void drawStar(String starname, int offset, int windowSize) {
		// the star
		Author star = new Author(starname);
		star = dbc.getCompleteAuthor(star);
		int lastPub = PublicationUtils.getPubLastYear(star.getPubs());

		// the T-1 neighbors

		int yearStart = lastPub + offset;
		List<Author> authors1ring;
		if (offset < 0)
			authors1ring = PublicationUtils.getAllAuthors(star.getPubs(), yearStart, yearStart
					+ windowSize - 1);
		else
			authors1ring = PublicationUtils.getAllAuthors(star.getPubs(), yearStart - windowSize,
					yearStart - 1);
		authors1ring.remove(star);
		for (Author a1 : authors1ring) {
			a1.setPubs(dbc.getCompleteAuthor(a1).getPubs());
			if (oldfirst.contains(a1))
				a1.setOld(true);
		}
		List<Publication> pubs1ring = PublicationUtils.getAllPublications(authors1ring);

		// T-2 neighbors
		List<Author> authors2ring = PublicationUtils.getAllAuthors(pubs1ring);
		authors2ring.removeAll(authors1ring);
		authors2ring.remove(star);
		for (Author a2 : authors2ring) {
			a2.setDistance(2);
			if (oldsecond.contains(a2))
				a2.setOld(true);
		}

		// form network
		star.setDistance(0);
		authors2ring.add(star);
		for (Author author : authors1ring) {
			author.setDistance(1);
		}
		authors2ring.addAll(authors1ring);
		StarNetwork sn = new StarNetwork();
		sn.formNetwork(authors2ring, pubs1ring, yearStart, windowSize);
		for (Author author : sn.getNodeList()) {
			System.out.println(author.getDistance());
		}

		// draw network
		draw(sn);
		drawSnapshotToFile(star.getID() + "_" + offset + "_" + windowSize, yearStart, windowSize);

		// oldfirst.clear();
		// oldfirst.addAll(authors1ring);
		// oldsecond.clear();
		// oldsecond.addAll(authors2ring);
	}

	private void longtermCollabStat() {
		setPrintWriter(new File("stat.txt"));
		System.out.println("=== long term collaboration analysis ===");
		// assemble all normal and died superstars and their neighbors
		List<Author> authors = new ArrayList<Author>();
		// authors.addAll(dbc.getAuthorsByStatus(Author.SUPERSTAR_EMERITUS));
		authors.addAll(dbc.getAuthorsByStatus(Author.SUPERSTAR_HARVESTED));
		authors.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN));
		authors.addAll(dbc.getAuthorsByStatus(Author.DEAD_SUPERSTAR_SUDDEN_NEIGHBORED));
		authors.addAll(dbc.getAuthorsByStatus(Author.CO_AUTHOR_HARVESTED));
		System.out.println(authors.size());
		// for each author, find all long term collaborator
		for (Author author : authors) {
			System.out.println(author.getID());
			// get star's publications
			author = dbc.getCompleteAuthor(author);
			int last, first;
			last = PublicationUtils.getPubLastYear(author.getPubs());
			first = PublicationUtils.getPubFirstYear(author.getPubs());
			int life = last - first + 1;
			// extract 1-ring authors (exclude star)
			List<Author> authors1ring;
			authors1ring = PublicationUtils.getAllAuthors(author.getPubs());
			authors1ring.remove(author);
			// ignore too short life or too small size
			if (authors1ring.size() < 10 || life < 10)
				continue;
			int count = 0;
			for (Author neighbor : authors1ring) {
				first = 3000;
				last = 0;
				for (Publication pub : author.getPubs()) {
					if (pub.getAULst().contains(neighbor)) {
						first = first > pub.getYear() ? pub.getYear() : first;
						last = last < pub.getYear() ? pub.getYear() : last;
					}
				}
				int span = last - first + 1;
				if (span > life / 2)
					count++;
			}
			// plot nb. long to degree
			getPrintWriter().println(count + "\t" + authors1ring.size());
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	private void printNetworkStats(StarNetwork network) {
		getPrintWriter().print(
				network.getNodeList().size() + "\t" + network.getEdgeList().size() + "\t"
						+ f.format(network.getAvgPubCount()) + "\t"
						+ f.format(network.calcClustCoeff()) + "\t"
						+ f.format(network.calcNetworkEffic()) + "\t");
	}

	private void printNetworkStatsCompare(StarNetwork network1, StarNetwork network2) {
		getPrintWriter().print(
				(network2.getNodeList().size() - network1.getNodeList().size()) + "\t"
						+ (network2.getEdgeList().size() - network1.getEdgeList().size()) + "\t"
						+ f.format(network2.getAvgPubCount() - network1.getAvgPubCount()) + "\t"
						+ f.format(network2.calcClustCoeff() - network1.calcClustCoeff()) + "\t"
						+ f.format(network2.calcNetworkEffic() - network1.calcNetworkEffic())
						+ "\t");
	}

	private void printNetworkStatsPreambles() {
		getPrintWriter().println(
				"year:\tstarPub\tN\tM\tavgPub\tcc\teffi\t" + "d/N\td/M\td/avgPub\td/cc\td/effi\t"
						+ "N\tM\tavgPub\tcc\teffi\t" + "d/N\td/M\td/avgPub\td/cc\td/effi\t");
	}

	/**
	 * prints the collaboration spans and number of papers published
	 */
	private void starNeighborsCollaborationStat(List<Author> subjects, String type) {

		// some variables
		int first, last, span, count;
		List<Author> authors1ring;

		// set output
		File output = new File("report/" + type + "_collab.txt");
		setPrintWriter(output);

		// normal stars
		for (Author star : subjects) {

			System.out.println(star.getID());
			// get star's publications
			star = dbc.getCompleteAuthor(star);
			last = PublicationUtils.getPubLastYear(star.getPubs());
			last = last > 2005 ? 2005 : last;
			first = PublicationUtils.getPubFirstYear(star.getPubs());
			if (first + 5 > last || PublicationUtils.getAllAuthors(star.getPubs()).size() < 20)
				continue;

			// extract 1-ring authors (exclude star)
			authors1ring = PublicationUtils.getAllAuthors(star.getPubs());
			authors1ring.remove(star);

			// print collaboration span and number of papers published
			for (Author author : authors1ring) {
				first = 3000;
				last = 0;
				count = 0;
				for (Publication pub : star.getPubs()) {
					if (pub.getAULst().contains(author)) {
						first = first > pub.getYear() ? pub.getYear() : first;
						last = last < pub.getYear() ? pub.getYear() : last;
						count++;
					}
				}
				span = last - first + 1;
				getPrintWriter().println(span + "\t" + count);
			}
		}
		closePrintWriter();
	}

	/**
	 * return the clustering coefficient of neighborhood network before and
	 * after the star died.
	 */
	private void egocentricNetwork(List<Author> subjects, String type) {
		System.out.println("analyzing egocentric networks");
		// some variables
		int firstPub, lastPub, window = 5;
		StarNetwork fullsize, orphan1ring, during1ring, post1ring, orphan2ring, during2ring, post2ring;
		List<Author> authors1ring, authors2ring;
		List<Publication> pubs1ring;

		// make networks and calculate their properties for each of the authors
		for (Author star : subjects) {

			System.out.println(type + "\t" + star.getID());
			// get star's publications
			star = dbc.getCompleteAuthor(star);
			lastPub = PublicationUtils.getPubLastYear(star.getPubs());
			firstPub = PublicationUtils.getPubFirstYear(star.getPubs());
			if (firstPub + 5 > lastPub
					|| PublicationUtils.getAllAuthors(star.getPubs()).size() < 20)
				continue;

			// set output
			File output = new File("report/" + type + "/" + star.getID() + ".txt");
			setPrintWriter(output);
			// setPrintWriter(System.out);

			// getPrintWriter().println(
			// star.getID() + "\t\tpubs: " + star.getPubs().size() + "\tfirst:"
			// + firstPub
			// + "\tlast:" + lastPub + "\twindow size" + window);
			// printNetworkStatsPreambles();
			// for (int yearStart = lastPub + 1 - window; yearStart + window - 1
			// <= lastPub; yearStart++) {
			for (int yearStart = firstPub; yearStart + window - 1 <= lastPub; yearStart++) {
				// extract 1-ring authors (exclude star)
				authors1ring = PublicationUtils.getAllAuthors(star.getPubs(), yearStart, yearStart
						+ window - 1);
				authors1ring.remove(star);
				if (authors1ring.size() == 0)
					continue;

				// extract all publications from 1-ring authors
				for (Author au : authors1ring) {
					au.setPubs(dbc.getCompleteAuthor(au).getPubs());
				}
				pubs1ring = PublicationUtils.getAllPublications(authors1ring);

				// extract all 1-ring and 2-ring authors
				authors2ring = PublicationUtils.getAllAuthors(pubs1ring);
				authors2ring.remove(star);

				// // 2 X -4~0 year before death without star
				// orphan1ring = new StarNetwork();
				// orphan1ring.formNetwork(authors1ring, pubs1ring, yearStart,
				// window);
				// orphan2ring = new StarNetwork();
				// orphan2ring.formNetwork(authors2ring, pubs1ring, yearStart,
				// window);
				// 2 X 1~5 year after death
				post1ring = new StarNetwork();
				post1ring.formNetwork(authors1ring, pubs1ring, yearStart + window, window);
				post2ring = new StarNetwork();
				post2ring.formNetwork(authors2ring, pubs1ring, yearStart + window, window);
				// networks with star
				authors1ring.add(star);
				authors2ring.add(star);
				pubs1ring.removeAll(star.getPubs());
				pubs1ring.addAll(star.getPubs());
				// 2 X -4~0 year before death
				during1ring = new StarNetwork();
				during1ring.formNetwork(authors1ring, pubs1ring, yearStart, window);
				during2ring = new StarNetwork();
				during2ring.formNetwork(authors2ring, pubs1ring, yearStart, window);
				// // full size network
				// fullsize = new StarNetwork();
				// fullsize.formNetwork(authors2ring, pubs1ring);

				// print results
				getPrintWriter().print(
						yearStart + "\t"
								+ PublicationUtils.getPublicationsCount(star, yearStart, window)
								+ "\t");
				printNetworkStats(during1ring);
				printNetworkStatsCompare(during1ring, post1ring);
				printNetworkStats(during2ring);
				printNetworkStatsCompare(during2ring, post2ring);
				getPrintWriter().println();

				// // draw graphs
				// draw(fullsize);
				// drawSnapshotToFile(star.getStatus() + "_" + star.getID() +
				// "_all", 1911, 100);
				// drawSnapshotToFile(star.getStatus() + "_" + star.getID() +
				// "_during2ring", lastPub
				// - window + 1, window);
				// drawSnapshotToFile(star.getStatus() + "_" + star.getID() +
				// "_post2ring",
				// lastPub + 1, window);

				// garbage collection
				System.gc();
			}
			closePrintWriter();
		}
	}
}

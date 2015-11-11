package superstar.logic.preload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import superstar.db.entity.Author;
import superstar.db.entity.Journal;
import superstar.db.entity.Publication;
import superstar.logic.DBConnector;
import superstar.logic.util.Entrez;
import superstar.logic.util.PublicationUtils;

public class HarvestPubmedData {
	public static void main(String[] args) {
		HarvestPubmedData harvester = new HarvestPubmedData();

		harvester.bootstrapStart();
		harvester.init();
		// harvester.harvSuperstarNeighbors();
		harvester.harvTempAuthors();
		harvester.bootstrapStop();
	}

	private DBConnector dbc;

	private Entrez entrez;

	private List<Journal> jnlLst;
	private List<Publication> pubLstRaw;
	private List<Publication> pubLst;

	/**
	 * Boot the JBoss Microcontainer with EJB3 settings.
	 */
	private void bootstrapStart() {
		// Boot the JBoss Microcontainer with EJB3 settings, automatically
		// loads ejb3-interceptors-aop.xml and embedded-jboss-beans.xml
		EJB3StandaloneBootstrap.boot(null);

		// Deploy custom stateless beans (datasource, mostly)
		EJB3StandaloneBootstrap.deployXmlResource("META-INF/superStar-beans.xml");

		// Deploy all EJBs found on classpath (slow, scans all)
		// EJB3StandaloneBootstrap.scanClasspath();

		// Deploy all EJBs found on classpath (fast, scans only build directory)
		// This is a relative location, matching the substring end of one of
		// java.class.path locations!
		// Print out System.getProperty("java.class.path") to understand this...
		EJB3StandaloneBootstrap.scanClasspath("SuperStar/build".replace("/", File.separator));
	}

	/**
	 * Shutdown EJB container.
	 */
	private void bootstrapStop() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}

	// superstar neighbors
	private void harvSuperstarNeighbors() {
		// initialize values
		List<Author> seedAuthors = dbc.getAuthorsByStatus(Author.SUPERSTAR_STUB);
		List<Author> neighbors;
		System.out.println(seedAuthors.size());

		// harvest each seed author
		for (Author seed : seedAuthors) {
			if (seed.getYearBorn() == 0)
				continue;

			// harvest seed itself
			seed = dbc.getCompleteAuthor(seed);

			// prepare neighbor list
			neighbors = new ArrayList<Author>();
			int firstpub = 2010;
			for (Publication pub : seed.getPubs()) {
				neighbors.removeAll(pub.getAULst());
				neighbors.addAll(pub.getAULst());
				if (pub.getYear() < firstpub)
					firstpub = pub.getYear();
			}
			System.out.print("harvesting seed: " + seed.getID() + " first pub: " + firstpub
					+ " neighbor: " + neighbors.size() + "... ");

			// harvest each neighbor if not yet done
			for (Author neighbor : neighbors) {
				if (neighbor.getStatus() == Author.CO_AUTHOR_STUB) {
					dbc.savePublications(retrievePub(neighbor));
					neighbor.setStatus(Author.CO_AUTHOR_HARVESTED);
					dbc.saveAuthor(neighbor);
				}
			}

			// set seed as harvested
			seed.setStatus(Author.SUPERSTAR_HARVESTED);
			dbc.saveAuthor(seed);
			System.out.println("done!");
		}
	}

	private void harvTempAuthors() {
		// initialize values
		List<Author> seedAuthors = dbc.getAuthorsByStatus(Author.HIGH_DEGREE);
		List<Author> neighbors;
		System.out.println(seedAuthors.size());
		long time;
		// harvest each seed author
		for (Author seed : seedAuthors) {
			time = System.currentTimeMillis();
			// harvest seed itself
			seed = dbc.getCompleteAuthor(seed);
			// prepare neighbor list
			neighbors = PublicationUtils.getAllAuthors(seed.getPubs());

			int counter = 0;
			// harvest each neighbor if not yet done
			for (Author neighbor : neighbors) {
				System.out.println(seed.getID() + " neighbors:" + (counter++) + "/"
						+ neighbors.size());
				if (neighbor.getStatus() == Author.CO_AUTHOR_STUB) {
					System.out.println("harvesting " + neighbor.getID());
					dbc.savePublications(retrievePub(neighbor));
					neighbor.setStatus(Author.CO_AUTHOR_HARVESTED);
					dbc.saveAuthor(neighbor);
				} else if (neighbor.getStatus() == Author.SUPERSTAR_STUB) {
					System.out.println("harvesting " + neighbor.getID());
					dbc.savePublications(retrievePub(neighbor));
					neighbor.setStatus(Author.SUPERSTAR_HARVESTED);
					dbc.saveAuthor(neighbor);
				}
				System.out.println((System.currentTimeMillis() - time) + "ms");
			}
			// set seed as harvested
			System.out.println("done!");
		}
	}

	private void init() {

		try {
			dbc = new DBConnector(new InitialContext());
		} catch (NamingException e) {
			e.printStackTrace();
		}
		jnlLst = LoadJournal.load();
	}

	private List<Publication> retrievePub(Author a) {
		a = dbc.getCompleteAuthor(a);
		entrez = new Entrez();
		pubLstRaw = entrez.eFetch(entrez.eSearch(a));
		if (pubLstRaw == null)
			return null;
		pubLst = new ArrayList<Publication>();
		for (Publication publication : pubLstRaw) {
			if (!publication.getPaperType().equals("journal article"))
				continue;
			for (Journal journal : jnlLst) {
				if (journal.getTitleAbbrev().equals(publication.getJournal())) {
					pubLst.add(publication);
					break;
				}
			}
		}
		a.getPubs().removeAll(pubLst);
		a.getPubs().addAll(pubLst);
		System.out.println(a.getPubs().size() + " publications retrieved.");
		return a.getPubs();
	}

	private List<Publication> retrievePub(Author a, int yearStart, int yearEnd) {
		a = dbc.getCompleteAuthor(a);
		entrez = new Entrez();
		pubLstRaw = entrez.eFetch(entrez.eSearch(a, yearStart, yearEnd));
		if (pubLstRaw == null)
			return null;
		pubLst = new ArrayList<Publication>();
		for (Publication publication : pubLstRaw) {
			if (!publication.getPaperType().equals("journal article"))
				continue;
			for (Journal journal : jnlLst) {
				if (journal.getTitleAbbrev().equals(publication.getJournal())) {
					pubLst.add(publication);
					break;
				}
			}
		}
		a.getPubs().removeAll(pubLst);
		a.getPubs().addAll(pubLst);
		System.out.println(a.getPubs().size() + " publications retrieved.");
		return a.getPubs();
	}

}

package superstar.logic.util;

import java.util.ArrayList;
import java.util.List;

import superstar.db.entity.Author;
import superstar.db.entity.Publication;

public class PublicationUtils {
	/**
	 * retrieve all authors from a list of publications.
	 * 
	 * @param pubs
	 * @return
	 */
	public static List<Author> getAllAuthors(List<Publication> pubs) {
		List<Author> allAuthors = new ArrayList<Author>();
		for (Publication pub : pubs) {
			allAuthors.removeAll(pub.getAULst());
			allAuthors.addAll(pub.getAULst());
		}
		return allAuthors;
	}

	/**
	 * retrieve all authors from a list of publications, with year limitation.
	 * 
	 * @param pubs
	 * @param yearStart
	 *            inclusive
	 * @param yearEnd
	 *            inclusive
	 * @return
	 */
	public static List<Author> getAllAuthors(List<Publication> pubs, int yearStart, int yearEnd) {
		List<Author> allAuthors = new ArrayList<Author>();
		for (Publication pub : pubs) {
			// co-authored with star in -4~0 years
			if (pub.getYear() >= yearStart && pub.getYear() <= yearEnd) {
				allAuthors.removeAll(pub.getAULst());
				allAuthors.addAll(pub.getAULst());
			}
		}
		return allAuthors;
	}

	/**
	 * get all publications from a list of authors.
	 * 
	 * @param authors
	 * @return
	 */
	public static List<Publication> getAllPublications(List<Author> authors) {
		List<Publication> pubs = new ArrayList<Publication>();
		for (Author a : authors) {
			pubs.removeAll(a.getPubs());
			pubs.addAll(a.getPubs());
		}
		return pubs;
	}

	/**
	 * get the year of first publication
	 * 
	 * @param pubs
	 * @return
	 */
	public static int getPubFirstYear(List<Publication> pubs) {
		int fp = 2010;
		for (Publication p : pubs) {
			fp = fp > p.getYear() ? p.getYear() : fp;
		}
		return fp;
	}

	/**
	 * get the year of last publication
	 * 
	 * @param pubs
	 * @return
	 */
	public static int getPubLastYear(List<Publication> pubs) {
		int lastPub = 0;
		for (Publication pub : pubs) {
			lastPub = pub.getYear() > lastPub ? pub.getYear() : lastPub;
		}
		return lastPub;
	}

	public static int getPublicationsCount(Author star, int yearStart, int window) {
		int count = 0;
		for (Publication pub : star.getPubs()) {
			if (pub.getYear() >= yearStart && pub.getYear() < yearStart + window)
				count++;
		}
		return count;
	}
}

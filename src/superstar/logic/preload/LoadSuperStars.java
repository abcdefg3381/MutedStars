package superstar.logic.preload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import superstar.db.entity.Author;
import superstar.logic.DBConnector;

public class LoadSuperStars {
	private static DBConnector dbc;

	private static void boot() {
		// Boot the JBoss Microcontainer with EJB3 settings, automatically
		// loads ejb3-interceptors-aop.xml and embedded-jboss-beans.xml
		EJB3StandaloneBootstrap.boot(null);

		// Deploy custom stateless beans (datasource, mostly)
		EJB3StandaloneBootstrap.deployXmlResource("META-INF/superStar-beans.xml");

		// Deploy all EJBs found on classpath (slow, scans all)
		// EJB3StandaloneBootstrap.scanClasspath();

		// Deploy all EJBs found on classpath (fast, scans only build
		// directory)
		// This is a relative location, matching the substring end of one of
		// java.class.path locations!
		// Print out System.getProperty("java.class.path") to understand
		// this...
		EJB3StandaloneBootstrap.scanClasspath("SuperStar/build".replace("/", File.separator));
	}

	private static void loadEmeritus() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"superstars/emeritus nas members.txt")));
		String line, family, first, middle, other = null;
		String[] sgmt, names;

		while ((line = br.readLine()) != null) {
			// analyze name
			sgmt = line.split(";");
			names = sgmt[0].split(" ");
			if (names.length == 2) {
				first = names[0];
				family = names[1];
				middle = "";
				other = "";
			} else {
				first = names[0];
				middle = names[1];
				family = names[2];
				other = "";
			}
			Author au = new Author(family, first, middle, other);
			if (dbc.getAuthorByID(au.getID()) != null) {
				// au.setStatus(Author.SUPERSTAR_EMERITUS);
				// dbc.saveAuthor(au);
				// System.out.println(line);
			} else {
				au.setStatus(Author.SUPERSTAR_EMERITUS);
				dbc.saveAuthor(au);
			}
		}
	}

	public static void loadHighlyCited() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				new File("superstars/highlycited.txt")));
		String line, family, first, middle, other = null;
		String[] sgmt, names;

		while ((line = br.readLine()) != null) {
			// analyze name
			sgmt = line.split(", ");
			family = sgmt[0];
			names = sgmt[1].split(" ");
			if (names.length == 1) {
				first = names[0];
				middle = "";
				other = "";
			} else if (names.length == 2) {
				first = names[0];
				middle = names[1];
				other = "";
			} else {
				first = names[0];
				middle = names[1];
				other = names[2];
			}
			Author au;
			if ((au = dbc.getAuthorByID((new Author(family, first, middle, other)).getID())) != null) {
				updateStatus(au);
			} else if ((au = dbc.getAuthorByID((new Author(family, first, "", other)).getID())) != null) {
				updateStatus(au);
			}
		}
	}

	private static void loadNASMembers() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				new File("superstars/nas members.txt")));
		String line, family, first, middle, other = null;
		String[] sgmt, names;
		int count = 0;
		while ((line = br.readLine()) != null) {
			// analyze name
			sgmt = line.split(";");
			names = sgmt[0].split(" ");
			if (names.length == 2) {
				first = names[0];
				family = names[1];
				middle = "";
				other = "";
			} else {
				first = names[0];
				middle = names[1];
				family = names[2];
				other = "";
			}
			int year = Integer.parseInt(sgmt[3]);
			Author au = new Author(family, first, middle, other);
			count++;
			if (dbc.getAuthorByID(au.getID()) != null) {
				au.setStatus(Author.SUPERSTAR_STUB);
				au.setYearBorn(year);
				dbc.saveAuthor(au);
				System.out.println(count + "\t" + au.getID());
			} else {
				// au.setStatus(Author.SUPERSTAR_EMERITUS);
				// dbc.saveAuthor(au);
			}
		}
	}

	public static void main(String[] args) throws NamingException, IOException {
		// open JDBC slot
		boot();
		dbc = new DBConnector(new InitialContext());
		// loadHighlyCited();
		loadNASMembers();
		// close JDBC slot
		shutdown();
	}

	private static void shutdown() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}

	private static void updateStatus(Author au) {
		System.out.println(au.getStatus());
		if (au.getStatus() == Author.CO_AUTHOR_HARVESTED)
			au.setStatus(Author.SUPERSTAR_HARVESTED);
		else if (au.getStatus() == Author.CO_AUTHOR_STUB)
			au.setStatus(Author.SUPERSTAR_STUB);
		dbc.saveAuthor(au);
	}
}

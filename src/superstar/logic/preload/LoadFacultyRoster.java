package superstar.logic.preload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import superstar.db.entity.Author;
import superstar.logic.DBConnector;

public class LoadFacultyRoster {

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

	private static List<Author> load(File roaster) throws IOException {

		// open input stream
		System.out.println("reading " + roaster.getName());
		FileInputStream in = new FileInputStream(roaster);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// initiate Author list
		List<Author> flist = new ArrayList<Author>();

		// Read File Line By Line
		String strLine;
		while ((strLine = br.readLine()) != null) {
			// family_name, given_name_1 given_name_2;affiliation;
			String[] segments = strLine.split(";");
			String[] names = segments[0].split(", ");
			// String affiliation = segments[1];
			String given = names[1].replace(" ", ",").concat(",,,,end");
			String[] givens = given.split(",");
			// make Author and add to list
			Author Author = new Author(names[0], givens[0], givens[1], givens[2]);
			flist.add(Author);
			// TODO add new field affiliation
			// , affiliation);
		}

		// clear duplicate records
		List<String> duplicateIDs = new ArrayList<String>();
		Author au1, au2;
		for (int i = 0; i < flist.size(); i++) {
			au1 = flist.get(i);
			for (int j = i + 1; j < flist.size(); j++) {
				au2 = flist.get(j);
				if (au1.getID().equals(au2.getID()))
					duplicateIDs.add(au1.getID());
			}
		}
		List<Author> authorLst = new ArrayList<Author>();
		for (Author author : flist) {
			if (!duplicateIDs.contains(author.getID()))
				authorLst.add(author);
		}

		// Close the input stream
		in.close();

		return authorLst;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws NamingException
	 */
	public static void main(String[] args) throws IOException, NamingException {
		// for time recording
		long time = System.currentTimeMillis();

		// open JDBC slot
		boot();
		DBConnector dbc = new DBConnector(new InitialContext());

		// look in the file directory, read roaster files by initial letter
		for (File roaster : (new File("faculty_roaster").listFiles())) {

			// save Author list
			dbc.saveAuthors(load(roaster));

		}

		// close JDBC slot
		shutdown();

		// for time recording
		System.out.println("time used: " + (System.currentTimeMillis() - time) + " ms");
	}

	private static void shutdown() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}
}

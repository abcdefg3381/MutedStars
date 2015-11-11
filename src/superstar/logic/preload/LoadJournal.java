package superstar.logic.preload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import superstar.db.entity.Journal;
import superstar.logic.DBConnector;

public class LoadJournal {
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

	/**
	 * @return top 500 journals
	 */
	public static List<Journal> load() {
		List<Journal> jnlLst = new ArrayList<Journal>();
		BufferedReader br = null;
		String line = null;
		String[] sgmnt = null;
		try {
			br = new BufferedReader(new FileReader(new File("journals/journal list")));

			Journal j;

			// read
			while ((line = br.readLine()) != null) {
				if (line.startsWith("%"))
					continue;
				line = line.toLowerCase();
				sgmnt = line.split(";");
				j = new Journal(sgmnt[0], sgmnt[1], sgmnt[2], sgmnt[3], sgmnt[4], sgmnt[5],
						sgmnt[6], sgmnt[7], sgmnt[8], sgmnt[9]);
				jnlLst.add(j);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {

			System.out.println(line);
			System.out.println(sgmnt);
			e.printStackTrace();
		}

		return jnlLst;
	}

	public static void main(String[] args) throws NamingException {
		// open JDBC slot
		boot();
		DBConnector dbc = new DBConnector(new InitialContext());
		dbc.saveJournals(load());
		// close JDBC slot
		shutdown();
	}

	private static void shutdown() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}
}

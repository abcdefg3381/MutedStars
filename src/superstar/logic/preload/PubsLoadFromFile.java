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

import superstar.db.entity.Publication;
import superstar.logic.DBConnector;

public class PubsLoadFromFile {

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

	private static void loadAndSave() throws IOException, NamingException {
		// prepare DB connector
		DBConnector dbc = new DBConnector(new InitialContext());

		// for each file in the folder
		for (File pubs : (new File("publications").listFiles())) {

			// parameters
			List<Publication> pubLst = new ArrayList<Publication>();
			Publication pub = null;

			// open input stream
			System.out.println("reading " + pubs.getName());
			FileInputStream in = new FileInputStream(pubs);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// read each line and save to list
			String strLine;
			while ((strLine = br.readLine()) != null) {
				// initiate publication
				String[] sgmt = strLine.split(";;");
				pub = new Publication(Integer.parseInt(sgmt[0]), Integer.parseInt(sgmt[1]),
						sgmt[2], sgmt[3], sgmt[4]);
				if (sgmt.length > 5)
					pub.authors = sgmt[5];
				else
					continue;

				// add to list
				pubLst.add(pub);
			}
			br.close();
			
			// save publication
			dbc.savePublicationsFromFile(pubLst);
		}
	}

	/**
	 * @param args
	 * @throws NamingException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NamingException, IOException {
		// open JDBC slot
		boot();

		// save Author list
		loadAndSave();

		// close JDBC slot
		shutdown();
	}

	private static void shutdown() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}
}

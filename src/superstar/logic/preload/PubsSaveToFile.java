package superstar.logic.preload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import superstar.db.entity.Publication;
import superstar.logic.DBConnector;

public class PubsSaveToFile {

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
	 * @param args
	 * @throws NamingException
	 */
	public static void main(String[] args) throws NamingException {
		// open JDBC slot
		boot();
		DBConnector dbc = new DBConnector(new InitialContext());
		saveToFile(dbc.getAllPublication());
		// close JDBC slot
		shutdown();
	}

	private static void saveToFile(List<Publication> allPublication) {
		try {
			// Create file
			FileWriter fstream = new FileWriter("pubsSudden");
			BufferedWriter out = new BufferedWriter(fstream);

			for (Publication publication : allPublication) {
				out.write(publication.toString() + "\n");
			}

			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static void shutdown() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}

}

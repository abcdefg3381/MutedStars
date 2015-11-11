package superstar.logic;

import java.io.File;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

public class EJB3Container {

	// Connects database
	public static void bootstrapStart() {
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
	 * Disconnects database
	 */
	public static void bootstrapStop() {
		// Shutdown EJB container
		EJB3StandaloneBootstrap.shutdown();
	}
}

package superstar.logic.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import superstar.logic.DBConnector;
import superstar.logic.EJB3Container;

public class CleanDB {
	public static void main(String[] args) {
		EJB3Container.bootstrapStart();
		CleanDB robot = new CleanDB();
		robot.clean();
		EJB3Container.bootstrapStop();
	}

	// database connection layer
	private DBConnector dbc;

	public CleanDB() {
		try {
			dbc = new DBConnector(new InitialContext());
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private void clean() {
		dbc.removeAuthor("hill t");
	}

}

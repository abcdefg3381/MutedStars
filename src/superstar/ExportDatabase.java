package superstar;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import maggie.network.gui.GuiUtils;
import superstar.gui.SuperStarGUI;
import superstar.logic.DBConnector;
import superstar.logic.DatabaseExportor;
import superstar.logic.EJB3Container;
import superstar.logic.SuperStarLogic;

public class ExportDatabase {
	private static ExportDatabase instance;

	public static ExportDatabase getInstance() {
		if (instance == null) {
			instance = new ExportDatabase();
		}
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EJB3Container.bootstrapStart();
		getInstance().exportDatabase();
		getInstance().exit();
	}

	private DBConnector dbc;

	private SuperStarGUI gui;

	private SuperStarLogic logic;

	private DatabaseExportor calc;

	public void exit() {
		EJB3Container.bootstrapStop();
		System.exit(0);
	}

	private DatabaseExportor exportDatabase() {
		if (calc == null) {
			calc = new DatabaseExportor();
		}
		return calc;
	}

	public DBConnector getDBC() {
		if (dbc == null) {
			dbc = new DBConnector(getInitialContext());
		}
		return dbc;
	}

	public SuperStarGUI getGUI() {
		if (gui == null) {
			gui = new SuperStarGUI();
			gui.setStarList(getLogic().retrieveDiedStars());
			gui.pack();
			GuiUtils.maxFrame(gui);
			gui.setVisible(true);
		}
		return gui;
	}

	public InitialContext getInitialContext() {
		try {
			return new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public SuperStarLogic getLogic() {
		if (logic == null) {
			logic = new SuperStarLogic();
		}
		return logic;
	}
}

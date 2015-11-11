package superstar;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import maggie.network.gui.GuiUtils;
import superstar.gui.SuperStarGUI;
import superstar.logic.Calculation;
import superstar.logic.DBConnector;
import superstar.logic.EJB3Container;
import superstar.logic.SuperStarLogic;

public class MainProgram {
	private static MainProgram instance;

	public static MainProgram getInstance() {
		if (instance == null) {
			instance = new MainProgram();
		}
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		EJB3Container.bootstrapStart();
		System.out.println("SuperStar started in " + (System.currentTimeMillis() - time) + " ms.");
		if (args.length == 0) {
			getInstance().doCalculation();
			getInstance().exit();
		} else if (args[0].toLowerCase().equals("gui"))
			getInstance().getGUI();
	}

	private DBConnector dbc;

	private SuperStarGUI gui;

	private SuperStarLogic logic;

	private Calculation calc;

	public void exit() {
		EJB3Container.bootstrapStop();
		System.exit(0);
	}

	private Calculation doCalculation() {
		if (calc == null) {
			calc = new Calculation();
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

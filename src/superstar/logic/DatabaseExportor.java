package superstar.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import maggie.common.utils.ReportWriter;
import superstar.MainProgram;
import superstar.db.entity.Author;
import superstar.db.entity.CoAuthorship;
import superstar.db.entity.Publication;
import superstar.db.entity.StarNetwork;

public class DatabaseExportor extends ReportWriter {
	// database connection layer
	private DBConnector dbc;

	public DatabaseExportor() {

		System.out.println("exporting database");
		dbc = MainProgram.getInstance().getDBC();

		// star
		String id = "tager hs";

		// print author list
		System.out.println("download publication and extract authors");
		List<Publication> allPub = dbc.getAllPublication();
		List<Author> allAuthor = new ArrayList<Author>();
		for (Publication pub : allPub) {
			for (Author author : pub.getAULst()) {
				if (!allAuthor.contains(author))
					allAuthor.add(author);
			}
		}
		setPrintWriter(new File(id + ".usr"));
		for (Author author : allAuthor) {
			getPrintWriter().println(author.getID());
		}
		closePrintWriter();

		// form network
		System.out.println("forming network and printing edges");
		StarNetwork sn = new StarNetwork(allAuthor, allPub);

		// print edegs
		setPrintWriter(new File(id + ".net"));
		for (CoAuthorship co : sn.getEdgeList()) {
			getPrintWriter().println(
					co.getFrom().getID() + "\t" + co.getTo().getID() + "\t" + co.getCount());
		}
		closePrintWriter();
	}
}

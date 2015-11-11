package superstar.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections15.Transformer;

import superstar.db.entity.Author;
import superstar.db.entity.CoAuthorship;
import superstar.db.entity.Publication;
import superstar.db.entity.StarNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

@SuppressWarnings("rawtypes")
public class GraphViewer extends VisualizationViewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9111226419973329984L;
	private StarNetwork network;

	/**
	 * @param layout
	 * @param network
	 */
	@SuppressWarnings("unchecked")
	public GraphViewer(Layout<Author, CoAuthorship> layout, StarNetwork network) {
		super(layout);
		this.network = network;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		// background color
		setBackground(Color.WHITE);

		// node color
		getRenderContext().setVertexFillPaintTransformer(new Transformer<Author, Color>() {
			@Override
			public Color transform(Author s) {
				if (s.getDistance() == 0)
					return Color.red;
				else if (s.getDistance() == 1)
					return Color.blue;
				else
					return Color.gray;
			}
		});

		// edge shape
		getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Integer, Number>());

		// edge label
		// getRenderContext().setEdgeLabelTransformer(new
		// Transformer<MyEdge,
		// String>() {
		// @Override
		// public String transform(MyEdge e) {
		// return String.format("%1.2f", e.getWeight());
		// }
		// });

		// mouse control
		setGraphMouse(new DefaultModalGraphMouse<Integer, Number>());
	}

	@SuppressWarnings("unchecked")
	public void setTime(final int yearStart, final int windowSize) {

		// node size and shape
		getRenderContext().setVertexShapeTransformer(new Transformer<Author, Shape>() {
			@Override
			public Shape transform(Author s) {
				int scale = 0;
				for (Publication p : network.getPubs()) {
					if (p.getYear() >= yearStart && p.getYear() < yearStart + windowSize
							&& p.getAULst().contains(s))
						scale++;
				}
				scale = (int) Math.pow(scale, 0.33d);
				if (s.isOld())
					return new Rectangle2D.Float(-6 * scale, -6 * scale, 12 * scale, 12 * scale);
				else
					return new Ellipse2D.Float(-6 * scale, -6 * scale, 12 * scale, 12 * scale);
			}
		});

		// edge color
		getRenderContext().setEdgeDrawPaintTransformer(new Transformer<CoAuthorship, Color>() {
			@Override
			public Color transform(CoAuthorship e) {
				if (e.getCount(yearStart, yearStart + windowSize - 1) > 0)
					return Color.black;
				else
					return null;
			}
		});
		// edge stroke
		getRenderContext().setEdgeStrokeTransformer(new Transformer<CoAuthorship, Stroke>() {
			@Override
			public Stroke transform(CoAuthorship e) {
				return new BasicStroke(e.getCount(yearStart, yearStart + windowSize - 1) / 3 + 1);
			}
		});

		// node label
		getRenderContext().setVertexLabelTransformer(new Transformer<Author, String>() {
			@Override
			public String transform(Author i) {
				// if (i.isOld()||i.getDistance()==1)
				// return i.getID().toString();
				// else
				return null;
			}
		});

		repaint();
	}
}
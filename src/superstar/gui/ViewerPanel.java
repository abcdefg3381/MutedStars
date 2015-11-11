/**
 * 
 */
package superstar.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import maggie.network.entity.Node;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import superstar.MainProgram;
import superstar.db.entity.Author;
import superstar.db.entity.CoAuthorship;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

/**
 * @author LIU Xiaofan
 * 
 */
public class ViewerPanel extends JPanel {
	@SuppressWarnings("rawtypes")
	class GraphViewer extends VisualizationViewer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9111226419973329984L;

		/**
		 * @param layout
		 */
		@SuppressWarnings("unchecked")
		public GraphViewer(FRLayout<Author, CoAuthorship> layout) {
			super(layout);
			initialize();
		}

		@SuppressWarnings("unchecked")
		private void initialize() {
			// background color
			setBackground(Color.WHITE);

			// node size
			getRenderContext().setVertexShapeTransformer(
					new ConstantTransformer(new Ellipse2D.Float(-6, -6, 12, 12)));

			// node color
			getRenderContext().setVertexFillPaintTransformer(new Transformer<Author, Color>() {
				@Override
				public Color transform(Author s) {
					if (s.getStatus() > 1)
						return Color.red;
					else
						return Color.blue;
				}
			});
			getRenderContext().setVertexDrawPaintTransformer(new Transformer<Node, Paint>() {
				@Override
				public Paint transform(Node v) {
					if (getPickedVertexState().isPicked(v))
						return Color.cyan;
					else
						return Color.BLACK;
				}
			});

			// // node label
			// getRenderContext().setVertexLabelTransformer(
			// new Transformer<Author, String>() {
			// @Override
			// public String transform(Author i) {
			// return i.getFamily();
			// }
			// });

			// edge shape
			// vv.getRenderContext().setEdgeShapeTransformer(
			// new EdgeShape.Line<Integer, Number>());
			// edge color
			// vv.getRenderContext().setEdgeDrawPaintTransformer(
			// new Transformer<MyEdge, Color>() {
			// @Override
			// public Color transform(MyEdge edge) {
			// if (edge.getWeight() < year) {
			// return new Color(1, 1, 1, 1);
			// }
			// int color = 255 - (edge.getWeight() + 1) * 50;
			// if (color < 0) {
			// color = 0;
			// }
			// return new Color(color, color, color);
			//
			// }
			// });

			// edge label
			// getRenderContext().setEdgeLabelTransformer(new
			// Transformer<MyEdge,
			// String>() {
			// @Override
			// public String transform(MyEdge e) {
			// return String.format("%1.2f", e.getWeight());
			// }
			// });
			// arrow color
			// vv.getRenderContext().setArrowFillPaintTransformer(
			// new Transformer<MyEdge, Color>() {
			// @Override
			// public Color transform(MyEdge edge) {
			// if (edge.getWeight() < year) {
			// return new Color(1, 1, 1, 1);
			// } else {
			// return Color.GRAY;
			// }
			//
			// }
			// });
			// vv.getRenderContext().setArrowDrawPaintTransformer(
			// new Transformer<MyEdge, Color>() {
			// @Override
			// public Color transform(MyEdge edge) {
			// if (edge.getWeight() < year) {
			// return new Color(1, 1, 1, 1);
			// } else {
			// return Color.GRAY;
			// }
			// }
			// });

			// mouse control
			setGraphMouse(new DefaultModalGraphMouse<Integer, Number>());
		}

		@SuppressWarnings("unchecked")
		protected void setTime(final int start, final int end) {
			// edge color
			getRenderContext().setEdgeDrawPaintTransformer(new Transformer<CoAuthorship, Color>() {
				@Override
				public Color transform(CoAuthorship e) {
					if (e.getCount(start, end) > 0)
						return Color.black;
					else
						return null;
				}
			});
			// edge stroke
			getRenderContext().setEdgeStrokeTransformer(new Transformer<CoAuthorship, Stroke>() {
				@Override
				public Stroke transform(CoAuthorship e) {
					return new BasicStroke(e.getCount(start, end) / 2);
				}
			});
			// // node label
			// getRenderContext().setVertexLabelTransformer(
			// new Transformer<Author, String>() {
			// @Override
			// public String transform(Author i) {
			// return i.getPubs().size()+" "+i.getID();
			// }
			// });
			repaint();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3868654388119625964L;
	private JButton btnGraph;
	private JPanel buttonPanel = null;
	private Graph<Author, CoAuthorship> g;
	private FRLayout<Author, CoAuthorship> layout;
	private JButton snapshotButton = null;
	private GraphViewer gv;

	/**
 * 
 */
	public ViewerPanel() {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "View Network",
				TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		setLayout(new BorderLayout());
		add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JButton getBtnGraph() {
		if (btnGraph == null) {
			btnGraph = new JButton("Graph");
			btnGraph.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					vizGraph();
				}
			});
		}
		return btnGraph;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getBtnGraph());
			buttonPanel.add(getSnapshotButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes snapshotButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSnapshotButton() {
		if (snapshotButton == null) {
			snapshotButton = new JButton();
			snapshotButton.setText("Snap shot");
			snapshotButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					snapshot();
				}
			});
		}
		return snapshotButton;
	}

	public void setGraph(Graph<Author, CoAuthorship> g) {
		this.g = g;
		vizGraph();
	}

	public void setTime(int start, int end) {
		if (gv != null) {
			gv.setTime(start, end);
			gv.revalidate();
			revalidate();
		}
	}

	private void snapshot() {
		for (Component c : getComponents()) {
			if (c.getClass().equals(GraphViewer.class)) {
				Dimension dim = c.getSize();
				Image im = c.createImage(dim.width, dim.height);
				c.paint(im.getGraphics());
				try {
					File imageFile = new File("./report/image/1.jpg");
					ImageIO.write((RenderedImage) im, "jpg", imageFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private boolean vizCheckNetwork() {
		if (MainProgram.getInstance().getLogic().getNetwork() == null) {
			JOptionPane.showMessageDialog(null, "No MyNetwork Formed!", "MyNetwork Not Found",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	private boolean vizClearContent() {
		for (Component c : getComponents()) {
			if (c.getClass().equals(GraphViewer.class)) {
				remove(c);
			}
		}
		validate();
		return true;
	}

	private void vizGraph() {
		if (vizCheckNetwork() && vizClearContent()) {
			// layout = new AggregateLayout<Index, MyEdge>(new SpringLayout(g));
			layout = new FRLayout<Author, CoAuthorship>(g);
			gv = new GraphViewer(layout);
			add(gv, BorderLayout.CENTER);
			revalidate();
		}
	}
}

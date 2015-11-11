package fraternity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import maggie.network.entity.Edge;
import maggie.network.entity.Network;
import maggie.network.entity.Node;
import maggie.network.gui.GuiUtils;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

class Frat implements Node {
	int id, degree, indegree, outdegree;
	float strength, instrength, outstrength;

	public Frat(int id) {
		this.id = id;
	}

	@Override
	public void addDegree(int i) {
		degree += i;
	}

	@Override
	public void addInDegree() {
		indegree++;
	}

	public void addInStrength(float s) {
		instrength += s;
	}

	@Override
	public void addOutDegree() {
		outdegree++;
	}

	public void addOutStrength(float s) {
		outstrength += s;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Frat) {
			return ((Frat) obj).getID() == this.id;
		} else
			return false;
	}

	@Override
	public int getDegree() {
		return degree;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getID() {
		return id;
	}

	@Override
	public float getInStrength() {
		return instrength;
	}

	@Override
	public String getName() {
		return "Frat " + id;
	}

	@Override
	public float getOutStrength() {
		return outstrength;
	}

	@Override
	public float getStrength() {
		return strength;
	}

	@Override
	public void setInStrength(float f) {
		instrength = f;
	}

	@Override
	public void setOutStrength(float f) {
		outstrength = f;
	}

}

class FratEdge extends Edge {
	float[] weights = new float[15];

	public FratEdge(Frat frat1, Frat frat2, float[] weights) {
		pair = new Pair<Frat>(frat1, frat2);
		this.weights = weights;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pair<Frat> getPair() {
		return (Pair<Frat>) pair;
	}

	@Override
	public boolean getType() {
		return false;
	}

	public float getWeekWeight(int week) {
		return weights[week];
	}

	public void setWeekWeight(int week, float weight) {
		weights[week] = weight;
	}

	public void setWeight(int week) {
		this.weight = weights[week];
	}

}

class FratNetwork extends Network {
	float[][][] superAdjMatrix;

	public FratNetwork() {
		super();
		superAdjMatrix = new float[17][17][15];
		setNodeList(new ArrayList<Node>());
		for (int i = 0; i < 17; i++) {
			getNodeList().add(new Frat(i));
		}
		setEdgeList(new ArrayList<Edge>());
	}

	public void formNetwork() {
		for (int j = 0; j < superAdjMatrix.length; j++) {
			for (int k = 0; k < superAdjMatrix[j].length; k++) {
				getEdgeList().add(
						new FratEdge(getNodeList().get(j), getNodeList().get(k),
								superAdjMatrix[j][k]));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FratEdge> getEdgeList() {
		return (List<FratEdge>) edgeList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Frat> getNodeList() {
		return (List<Frat>) nodeList;
	}

	public void setMatrixEntry(int i, int j, int week, float weight) {
		superAdjMatrix[i][j][week] = weight;
	}
}

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
	public GraphViewer(Layout<Frat, FratEdge> layout) {
		super(layout);
		initialize();
	}

	protected Color findColor(int week, FratEdge e) {
		// if (e.getCount(start, end) > 0)
		int code = (int) (255 - 255 * e.getWeekWeight(week));
		if (e.getWeekWeight(week) > 0.3) {
			return new Color(code, code, code);
		} else
			return new Color(255, 255, 255, 0);
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		// background color
		setBackground(Color.WHITE);

		// node size
		getRenderContext().setVertexShapeTransformer(
				new ConstantTransformer(new Ellipse2D.Float(-6, -6, 12, 12)));

		// node color
		getRenderContext().setVertexFillPaintTransformer(new Transformer<Frat, Color>() {
			@Override
			public Color transform(Frat s) {
				return Color.red;
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

		// node label
		getRenderContext().setVertexLabelTransformer(new Transformer<Frat, String>() {
			@Override
			public String transform(Frat i) {
				return i.getID().toString();
			}
		});

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
	protected void setTime(final int week) {
		// edge color
		getRenderContext().setEdgeDrawPaintTransformer(new Transformer<FratEdge, Color>() {
			@Override
			public Color transform(FratEdge e) {
				return findColor(week, e);
			}
		});

		// arrow color
		getRenderContext().setArrowDrawPaintTransformer(new Transformer<FratEdge, Color>() {
			@Override
			public Color transform(FratEdge e) {
				return findColor(week, e);
			}
		});
		getRenderContext().setArrowFillPaintTransformer(new Transformer<FratEdge, Color>() {
			@Override
			public Color transform(FratEdge e) {
				return findColor(week, e);
			}
		});

		// edge stroke
		getRenderContext().setEdgeStrokeTransformer(new Transformer<FratEdge, Stroke>() {
			@Override
			public Stroke transform(FratEdge e) {
				return new BasicStroke(2);
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

public class NewcombFraternity {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NewcombFraternity dn = new NewcombFraternity();
		// dn.draw(dn.read());
		dn.calc(dn.read());
	}

	private GraphViewer gv;

	private void calc(FratNetwork net) {

		// // strength distribution
		calcStrengthDist(net);

		// // change of matrix
		calcMatrixChange(net);
		calcChangeVsStrength(net);

		// weighted clustering coefficient
		calcWeightedClusteringCoefficient(net);
	}

	private void calcChangeVsStrength(FratNetwork net) {
		// week, node, change and strength
		float[][][] diffsVsStrength = new float[14][17][2];

		// change of weights of the week. subtract one matrix from another
		for (int week = 0; week < 14; week++) {
			for (int i = 0; i < 17; i++) {
				for (int j = 0; j < 17; j++) {
					// in-strength change
					diffsVsStrength[week][i][0] += Math.abs(net.superAdjMatrix[j][i][week + 1]
							- net.superAdjMatrix[j][i][week]);
					// in-strength
					diffsVsStrength[week][i][1] += net.superAdjMatrix[j][i][week];
				}
			}
		}
		System.out.print("change\t");
		for (int week = 0; week < 14; week++) {
			for (int i = 0; i < 17; i++) {
				System.out.print(diffsVsStrength[week][i][0] + "\t");
			}
		}
		System.out.println();
		System.out.print("out-strength\t");
		for (int week = 0; week < 14; week++) {
			for (int i = 0; i < 17; i++) {
				System.out.print(diffsVsStrength[week][i][1] + "\t");
			}
		}
		System.out.println();
	}

	private void calcMatrixChange(FratNetwork net) {
		float[] diffs;

		// change of weights of the week. subtract one matrix from another
		diffs = new float[14];
		for (int week = 0; week < 14; week++) {
			for (int j = 0; j < 17; j++) {
				for (int k = 0; k < 17; k++) {
					diffs[week] += Math.abs(net.superAdjMatrix[k][j][week + 1]
							- net.superAdjMatrix[k][j][week]);
				}
			}
		}
		for (int i = 0; i < 14; i++) {
			System.out.print(diffs[i] + "\t");
		}
		System.out.println();
	}

	private void calcStrengthDist(FratNetwork net) {

		int bins = 16;
		float interval = 1.0f;
		float strength;
		int[] strengthDist;

		// print header
		System.out.print("\t");
		for (float i = 0; i < bins; i += interval) {
			System.out.print(i + "\t");
		}
		System.out.println();

		// strength distribution of the week
		for (int week = 0; week < 15; week++) {

			// for each node i
			strengthDist = new int[bins];
			for (int i = 0; i < 17; i++) {

				// calculate its in-strength, from j to i
				strength = 0;
				for (int j = 0; j < 17; j++) {
					strength += net.superAdjMatrix[j][i][week];
				}

				// add to distribution
				strengthDist[(int) (strength / interval)] += 1;
			}
			System.out.print("week " + (week + 1) + "\t");
			for (int i = 0; i < bins; i++) {
				System.out.print(strengthDist[i] + "\t");
			}
			System.out.println();
		}

	}

	private void calcWeightedClusteringCoefficient(FratNetwork net) {
		float[][] ccs = new float[17][15];

		float si, sum;

		// weighted clustering coefficient for each week
		// ref. see Barrat et al. PNAS 2004
		for (int week = 0; week < 15; week++) {
			// for each node i
			for (int i = 0; i < 17; i++) {
				si = 0;
				sum = 0;
				// calculate
				for (int j = 0; j < 17; j++) {
					for (int k = 0; k < 17; k++) {
						if (i != j && i != k && j != k) {
							sum += net.superAdjMatrix[i][j][week];
							sum += net.superAdjMatrix[j][i][week];
							sum += net.superAdjMatrix[i][k][week];
							sum += net.superAdjMatrix[k][i][week];
						}
					}
					si += net.superAdjMatrix[j][i][week];
					si += net.superAdjMatrix[i][j][week];
				}
				// add to ccs[i][week]
				ccs[i][week] = sum / 2.0f / si / 15;
			}
		}

		// print data per week
		System.out.print("\t");
		for (int i = 0; i < 17; i++) {
			System.out.print("node " + i + "\t");
		}
		System.out.println();
		for (int week = 0; week < 15; week++) {
			System.out.print("week" + (week + 1) + "\t");
			for (int i = 0; i < 17; i++) {
				System.out.print(ccs[i][week] + "\t");
			}
			System.out.println();
		}

	}

	/**
	 * draws the network to file
	 * 
	 * @param net
	 */
	private void draw(FratNetwork net) {
		// convert network to jung graph
		Graph<Frat, FratEdge> g = new DirectedSparseGraph<Frat, FratEdge>();
		for (FratEdge fe : net.getEdgeList()) {
			g.addEdge(fe, fe.getPair());
		}

		// create graph viewer
		Layout<Frat, FratEdge> layout = new CircleLayout<Frat, FratEdge>(g);
		gv = new GraphViewer(layout);
		gv.setSize(600, 600);
		gv.revalidate();
		gv.setBackground(Color.WHITE);

		// scale graph
		// final ScalingControl scaler = new CrossoverScalingControl();
		// gv.scaleToLayout(scaler);
		// scaler.scale(gv, 2.0f, gv.getCenter());

		// save graph viewer to file
		for (int i = 0; i < 15; i++) {
			gv.setTime(i);
			GuiUtils.drawComponentToFile(gv, new File("./image/" + i + ".jpg"), "jpg");
		}
	}

	/**
	 * @return reads a dynamical network of 17x17 matrix of length-15 arrays
	 *         (17x17x15) from file
	 * @throws IOException
	 */
	private FratNetwork read() throws IOException {
		// prepare reader
		BufferedReader br = new BufferedReader(new FileReader(new File("matrix")));
		FratNetwork fn = new FratNetwork();

		String line;
		int lineNo = 0;
		String[] sgmt;

		while ((line = br.readLine()) != null) {
			// split line
			sgmt = line.split(",");
			// parse line to array
			for (int j = 0; j < sgmt.length - 1; j++) {
				// i,j,week,weight
				fn.setMatrixEntry(lineNo % 17, j, lineNo / 17,
						1 / Float.parseFloat(sgmt[j + 1]));
			}
			lineNo++;
			// set no edges from one to itself
			for (int i = 0; i < 17; i++) {
				for (int week = 0; week < 15; week++) {
					fn.setMatrixEntry(i, i, week, 0);
				}
			}
		}
		fn.formNetwork();
		return fn;
	}

}

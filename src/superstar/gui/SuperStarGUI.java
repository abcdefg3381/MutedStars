package superstar.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import superstar.MainProgram;
import superstar.db.entity.Author;
import superstar.db.entity.CoAuthorship;
import superstar.db.entity.StarNetwork;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class SuperStarGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 784051455727352203L;

	public static void main(String[] args) {
		SuperStarGUI g = new SuperStarGUI();
		g.pack();
		g.setVisible(true);
	}

	private JTextField txtNode;
	private JTextField txtEdge;
	private JTextField txtPubs;
	private JTextField txtCc;

	private JTextField txtYear;
	private int yearStart, yearEnd, windowSize;
	private JSlider sliderEnd;
	private JSlider sliderStart;

	private JList list;

	private ViewerPanel panelGraph;
	private Graph<Author, CoAuthorship> g;

	public SuperStarGUI() {
		setTitle("Project SuperStar");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MainProgram.getInstance().exit();
			}
		});
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		panelGraph = new ViewerPanel();
		getContentPane().add(panelGraph, BorderLayout.CENTER);

		JPanel panelControl = new JPanel();
		getContentPane().add(panelControl, BorderLayout.SOUTH);

		JLabel lblStart = new JLabel("Start:");
		panelControl.add(lblStart);

		sliderStart = new JSlider();
		sliderStart.setSnapToTicks(true);
		sliderStart.setMinorTickSpacing(1);
		sliderStart.setPaintLabels(true);
		sliderStart.setMajorTickSpacing(20);
		sliderStart.setPaintTicks(true);
		panelControl.add(sliderStart);

		JLabel lblEnd = new JLabel("End:");
		panelControl.add(lblEnd);

		sliderEnd = new JSlider();
		sliderEnd.setSnapToTicks(true);
		sliderEnd.setMinorTickSpacing(1);
		sliderEnd.setMajorTickSpacing(20);
		sliderEnd.setPaintLabels(true);
		sliderEnd.setPaintTicks(true);
		panelControl.add(sliderEnd);

		JPanel panelSelect = new JPanel();
		getContentPane().add(panelSelect, BorderLayout.WEST);

		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					formNetwork(list.getSelectedValues());
				}
			}
		});
		panelSelect.setLayout(new BorderLayout(0, 0));

		JButton btnFormNetwork = new JButton("Form Network");
		btnFormNetwork.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formNetwork(list.getSelectedValues());
			}
		});
		panelSelect.add(btnFormNetwork, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(list);
		panelSelect.add(scrollPane);

		JPanel panelAttribute = new JPanel();
		getContentPane().add(panelAttribute, BorderLayout.EAST);
		GridBagLayout gbl_panelAttribute = new GridBagLayout();
		gbl_panelAttribute.columnWidths = new int[] { 0, 0, 0 };
		gbl_panelAttribute.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelAttribute.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panelAttribute.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		panelAttribute.setLayout(gbl_panelAttribute);

		JPanel panelE1 = new JPanel();
		GridBagConstraints gbc_panelE1 = new GridBagConstraints();
		gbc_panelE1.gridwidth = 2;
		gbc_panelE1.insets = new Insets(0, 0, 5, 0);
		gbc_panelE1.fill = GridBagConstraints.BOTH;
		gbc_panelE1.gridx = 0;
		gbc_panelE1.gridy = 0;
		panelAttribute.add(panelE1, gbc_panelE1);

		JLabel lblYear = new JLabel("Year");
		GridBagConstraints gbc_lblYear = new GridBagConstraints();
		gbc_lblYear.anchor = GridBagConstraints.EAST;
		gbc_lblYear.insets = new Insets(0, 0, 5, 5);
		gbc_lblYear.gridx = 0;
		gbc_lblYear.gridy = 1;
		panelAttribute.add(lblYear, gbc_lblYear);

		txtYear = new JTextField();
		txtYear.setText("year");
		GridBagConstraints gbc_txtYear = new GridBagConstraints();
		gbc_txtYear.insets = new Insets(0, 0, 5, 0);
		gbc_txtYear.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtYear.gridx = 1;
		gbc_txtYear.gridy = 1;
		panelAttribute.add(txtYear, gbc_txtYear);
		txtYear.setColumns(10);

		JLabel lblNode = new JLabel("Node");
		GridBagConstraints gbc_lblNode = new GridBagConstraints();
		gbc_lblNode.anchor = GridBagConstraints.EAST;
		gbc_lblNode.insets = new Insets(0, 0, 5, 5);
		gbc_lblNode.gridx = 0;
		gbc_lblNode.gridy = 2;
		panelAttribute.add(lblNode, gbc_lblNode);

		txtNode = new JTextField();
		txtNode.setText("node");
		GridBagConstraints gbc_txtNode = new GridBagConstraints();
		gbc_txtNode.insets = new Insets(0, 0, 5, 0);
		gbc_txtNode.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNode.gridx = 1;
		gbc_txtNode.gridy = 2;
		panelAttribute.add(txtNode, gbc_txtNode);
		txtNode.setColumns(10);

		JLabel lblEdge = new JLabel("Edge");
		GridBagConstraints gbc_lblEdge = new GridBagConstraints();
		gbc_lblEdge.anchor = GridBagConstraints.EAST;
		gbc_lblEdge.insets = new Insets(0, 0, 5, 5);
		gbc_lblEdge.gridx = 0;
		gbc_lblEdge.gridy = 3;
		panelAttribute.add(lblEdge, gbc_lblEdge);

		txtEdge = new JTextField();
		txtEdge.setText("edge");
		GridBagConstraints gbc_txtEdge = new GridBagConstraints();
		gbc_txtEdge.insets = new Insets(0, 0, 5, 0);
		gbc_txtEdge.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEdge.gridx = 1;
		gbc_txtEdge.gridy = 3;
		panelAttribute.add(txtEdge, gbc_txtEdge);
		txtEdge.setColumns(10);

		JLabel lblPubs = new JLabel("Pubs");
		GridBagConstraints gbc_lblPubs = new GridBagConstraints();
		gbc_lblPubs.anchor = GridBagConstraints.EAST;
		gbc_lblPubs.insets = new Insets(0, 0, 5, 5);
		gbc_lblPubs.gridx = 0;
		gbc_lblPubs.gridy = 4;
		panelAttribute.add(lblPubs, gbc_lblPubs);

		txtPubs = new JTextField();
		txtPubs.setText("pubs");
		GridBagConstraints gbc_txtPubs = new GridBagConstraints();
		gbc_txtPubs.insets = new Insets(0, 0, 5, 0);
		gbc_txtPubs.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPubs.gridx = 1;
		gbc_txtPubs.gridy = 4;
		panelAttribute.add(txtPubs, gbc_txtPubs);
		txtPubs.setColumns(10);

		JLabel lblCc = new JLabel("C.C.");
		GridBagConstraints gbc_lblCc = new GridBagConstraints();
		gbc_lblCc.anchor = GridBagConstraints.EAST;
		gbc_lblCc.insets = new Insets(0, 0, 5, 5);
		gbc_lblCc.gridx = 0;
		gbc_lblCc.gridy = 5;
		panelAttribute.add(lblCc, gbc_lblCc);

		txtCc = new JTextField();
		txtCc.setText("c.c.");
		GridBagConstraints gbc_txtCc = new GridBagConstraints();
		gbc_txtCc.insets = new Insets(0, 0, 5, 0);
		gbc_txtCc.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCc.gridx = 1;
		gbc_txtCc.gridy = 5;
		panelAttribute.add(txtCc, gbc_txtCc);
		txtCc.setColumns(10);

		JPanel panelE2 = new JPanel();
		GridBagConstraints gbc_panelE2 = new GridBagConstraints();
		gbc_panelE2.gridwidth = 2;
		gbc_panelE2.insets = new Insets(0, 0, 5, 5);
		gbc_panelE2.fill = GridBagConstraints.BOTH;
		gbc_panelE2.gridx = 0;
		gbc_panelE2.gridy = 6;
		panelAttribute.add(panelE2, gbc_panelE2);
	}

	protected void formNetwork(Object[] selectedValues) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		MainProgram.getInstance().getLogic().formRootNetwork(selectedValues);
		yearStart = MainProgram.getInstance().getLogic().getRootNetwork().getFirtYear();
		yearEnd = 2010;
		windowSize = yearEnd - yearStart;
		setSliders(yearStart, yearEnd);
		setGraph(MainProgram.getInstance().getLogic().getRootNetwork());
		setAttributes();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	protected void setAttributes() {
		txtYear.setText(yearStart + " - " + yearEnd);
		txtNode.setText(MainProgram.getInstance().getLogic().getNetwork().getNodeList().size() + "");
		txtEdge.setText(MainProgram.getInstance().getLogic().getNetwork().getEdgeList().size() + "");
		txtPubs.setText(MainProgram.getInstance().getLogic().getNetwork().getNetworkPubsCount()
				+ "");
		txtCc.setText(MainProgram.getInstance().getLogic().getNetwork().calcClustCoeff() + "");
	}

	/**
	 * Generates a graph: in this case, reads it from current network
	 * 
	 * @param yearEnd
	 * @param yearStart
	 * @param network
	 * 
	 * @param currentNetwork
	 * 
	 * @return A sample undirected graph
	 */
	private void setGraph(StarNetwork network) {
		g = new UndirectedSparseGraph<Author, CoAuthorship>();
		for (CoAuthorship coau : network.getEdgeList()) {
			g.addEdge(coau, coau.getPair(), EdgeType.UNDIRECTED);
		}
		panelGraph.setGraph(g);
		panelGraph.setTime(yearStart, yearEnd);
	}

	private void setSliders(int firtYear, int lastYear) {
		for (ChangeListener sl : sliderStart.getChangeListeners()) {
			sliderStart.removeChangeListener(sl);
		}
		sliderStart.setMinimum(firtYear);
		sliderStart.setMaximum(lastYear);
		sliderStart.setValue(firtYear);
		sliderStart.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				yearStart = ((JSlider) e.getSource()).getValue();
				sliderEnd.setValue(yearStart + windowSize);
				updateNetwork();
			}
		});
		for (ChangeListener sl : sliderEnd.getChangeListeners()) {
			sliderEnd.removeChangeListener(sl);
		}
		sliderEnd.setMinimum(firtYear);
		sliderEnd.setMaximum(lastYear);
		sliderEnd.setValue(lastYear);
		sliderEnd.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				yearEnd = ((JSlider) e.getSource()).getValue();
				if (windowSize == (yearEnd - yearStart))
					return;
				windowSize = yearEnd - yearStart;
				updateNetwork();
			}
		});
	}

	public void setStarList(List<Author> retrieveStars) {
		list.setListData(retrieveStars.toArray());
	}

	/**
	 * @param year
	 * @param slider
	 *            : 0=start, 1=end
	 */
	protected void updateNetwork() {
		MainProgram.getInstance().getLogic().setTime(yearStart, yearEnd);
		panelGraph.setTime(yearStart, yearEnd);
		setAttributes();
	}

}

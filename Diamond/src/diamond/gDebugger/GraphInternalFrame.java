package diamond.gDebugger;

import javax.swing.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

import diamond.data.TripleToken;
import diamond.retenetwork.*;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.event.*;

/**
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class GraphInternalFrame extends MyInternalFrame implements ActionListener, GraphSelectionListener {

    GraphModel model = new DefaultGraphModel();
    GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
    JGraph jgraph = new JGraph(model, view);
    JScrollPane js;
    DefaultGraphCell[] cells = new DefaultGraphCell[5];
    DefaultGraphCell[] cellVector;
    DefaultGraphCell[] edgeVector;
    int numberOfInput = 0;
    List<ReteNode> rtList;
    int[] alpha = new int[100];
    int[] beta = new int[100];
    int[] triple = new int[100];
    int[] join = new int[100];
    int nOfTriple = 0;
    int nOfAlpha = 0;
    int nOfBeta = 0;
    int nOfJoin = 0;
    int numberOfAlphaMemory = 0;
    int numberOfBetaMemory = 0;
    int numberOfMemory = 0;
    int indexOfMemory = 0;

    // Vector <DefaultGraphCell> cellVector = new Vector<DefaultGraphCell>();
    int sizeX;
    int sizeY;

    ArrayList<ArrayList<ReteNode>> inputReteList;

    public GraphInternalFrame() {
        super("SPARQL Graph ", true, false, false, true);

        new Font("Eras Demi ITC", Font.PLAIN, 12);

        sizeX = 800;
        sizeY = 800;

        jgraph.setPreferredSize(new Dimension(sizeX, sizeY));
        js = new JScrollPane(jgraph);
        js.setPreferredSize(new Dimension(850, 850));
        js.setAutoscrolls(true);
        js.setVisible(true);

        getContentPane().add(js);
        updateDraw(2);

        jgraph.addGraphSelectionListener(this);

    }

    // public void updateDraw(ReteNetwork inputNetwork){
    public void updateDraw(ArrayList<ArrayList<ReteNode>> reteList) {

        // for(ArrayList<ReteNode> i: reteList) {
        // for(ReteNode j: i) {
        // System.out.println("Node: " + j + " - " + j.getId());
        // }
        // System.out.println("<br>");
        // }

        // ReteNetwork reteNetwork = inputNetwork;
        // reteList = reteNetwork.getListOfReteNodesInNetwork();

        inputReteList = reteList;

        int countNode = 0;
        int countEdge = 0;
        int pointX;
        int pointY;
        DefaultGraphCell cell;
        int maximumXSize = 0;

        // Remove old data (drawn nodes and edges)
        jgraph.getGraphLayoutCache().remove(cells);
        if (cellVector != null)
            jgraph.getGraphLayoutCache().remove(cellVector);
        if (edgeVector != null)
            jgraph.getGraphLayoutCache().remove(edgeVector);

        int temp;
        // In order to obtain the maximum number of nodes in the level of list
        for (int i = 0; i < reteList.size(); i++)
            for (int j = 0; j < reteList.get(i).size(); j++) {
                temp = reteList.get(i).size();
                if (temp > maximumXSize)
                    maximumXSize = temp;
            }

        // Removing the duplicate nodes in the list
        boolean deleteParent = false;
        for (int i = 0; i < reteList.size() - 1; i++) {
            for (int i2 = i + 1; i2 < reteList.size(); i2++) {
                for (int j = 0; j < reteList.get(i).size(); j++) {
                    for (int k = 0; k < reteList.get(i2).size(); k++) {
                        ReteNode tmp1 = reteList.get(i).get(j);
                        ReteNode tmp2 = reteList.get(i2).get(k);

                        // DMain.mFrame.updateMsgValues(tmp1.toString()+
                        // ","+tmp2.toString());
                        if (tmp1.getId() == tmp2.getId()) {
                            // DMain.mFrame.updateMsgValues("Duplicate :"
                            // +reteList.get(i).get(j).toString());
                            deleteParent = true;
                        }
                    }
                    if (deleteParent) {
                        reteList.get(i).remove(j);
                        deleteParent = false;
                    }
                }
            }
        }
        // Annotating every node's parents to each node

        // Adjusting graph panel according to the number of nodes in Rete
        // Network
        if (maximumXSize < 5)
            sizeX = 500;
        else
            sizeX = 500 + 50 * (maximumXSize - 5);

        if (reteList.size() < 5)
            sizeY = 300;
        else
            sizeY = 400 + 40 * (reteList.size() - 5);

        jgraph.setPreferredSize(new Dimension(sizeX, sizeY));

        cellVector = new DefaultGraphCell[reteList.size() * maximumXSize];
        edgeVector = new DefaultGraphCell[reteList.size() * maximumXSize];

        for (int outIndex = 0; outIndex < reteList.size(); outIndex++) {

            pointY = sizeY / (reteList.size() + 1) * (outIndex + 1);

            for (int inIndex = 0; inIndex < reteList.get(outIndex).size(); inIndex++) {

                // for drawing a node
                pointX = sizeX / (reteList.get(outIndex).size() + 1) * (inIndex + 1);

                ReteNode currentNode = reteList.get(outIndex).get(inIndex);
                String nodeName = currentNode.toString();

                // if memory node display solution-set size
                if (nodeName.contains("Memory")) {
                    nodeName += " [" + ((Memory) currentNode).getMemory().size() + "]";
                }

                DMain.mFrame.showMessage("Node " + countNode + ": " + nodeName);

                cell = new DefaultGraphCell(new String(nodeName));

                GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(pointX, pointY, 100, 30));
                GraphConstants.setFont(cell.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));
                GraphConstants.setOpaque(cell.getAttributes(), true);
                DefaultPort port0 = new DefaultPort();
                cell.add(port0);

                if (nodeName.contains("Alpha")) {
                    if (((Memory) currentNode).getMemory().isEmpty()) {
                        GraphConstants.setBackground(cell.getAttributes(), new Color(255, 0, 0));
                    } else {
                        GraphConstants.setBackground(cell.getAttributes(), new Color(224, 255, 255));
                    }
                } else if (nodeName.contains("Beta")) {
                    if (((Memory) currentNode).getMemory().isEmpty()) {
                        GraphConstants.setBackground(cell.getAttributes(), new Color(255, 0, 0));
                    } else {
                        GraphConstants.setBackground(cell.getAttributes(), new Color(224, 238, 224));
                    }
                } else if (nodeName.contains("Root")) {

                    GraphConstants.setBackground(cell.getAttributes(), new Color(238, 207, 161));
                } else {
                    GraphConstants.setBackground(cell.getAttributes(), new Color(205, 205, 193));

                }

                cellVector[countNode] = cell;

                if (cellVector != null)
                    jgraph.getGraphLayoutCache().insert(cellVector[countNode]);

                // for drawing an edge

                if (outIndex > 0) {

                    int indexForRParent = 0;
                    int indexForLParent = 0;
                    int countIndex = 0;

                    // there is a way to get right & left parent in join node.
                    if (nodeName.contains("Join") || nodeName.contains("Union") || nodeName.contains("Intersection")) {
                        Join joinNode = (Join) currentNode;
                        ReteNode rParent = joinNode.getRightParent();
                        ReteNode lParent = joinNode.getLeftParent();

                        for (int idx = 0; idx < reteList.size(); idx++) {
                            for (int idx2 = 0; idx2 < reteList.get(idx).size(); idx2++) {
                                ReteNode expectedParent = reteList.get(idx).get(idx2);
                                int temp2 = expectedParent.getId();
                                // DMain.mFrame.updateMsgValues("expected parent: "+temp2);

                                if (temp2 == rParent.getId()) {
                                    indexForRParent = countIndex;
                                    // DMain.mFrame.updateMsgValues("Right Parent found "+temp2+" , "+indexForRParent);

                                } else if (temp2 == lParent.getId()) {
                                    indexForLParent = countIndex;
                                    // DMain.mFrame.updateMsgValues("Left Parent found"+temp2+" , "+indexForLParent);

                                } else {
                                    // DMain.mFrame.updateMsgValues("Not found... "+temp2+
                                    // " , "+countIndex);
                                }
                                countIndex++;

                            }
                        }

                        DefaultEdge edge = new DefaultEdge();
                        edge.setSource(cellVector[indexForRParent].getChildAt(0));
                        edge.setTarget(cellVector[countNode].getChildAt(0));
                        edgeVector[countEdge] = edge;

                        jgraph.getGraphLayoutCache().insert(edgeVector[countEdge++]);

                        DefaultEdge edge2 = new DefaultEdge();
                        edge2.setSource(cellVector[indexForLParent].getChildAt(0));
                        edge2.setTarget(cellVector[countNode].getChildAt(0));
                        edgeVector[countEdge] = edge2;

                        int arrow = GraphConstants.ARROW_CLASSIC;
                        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
                        GraphConstants.setEndFill(edge.getAttributes(), true);

                        @SuppressWarnings("unused")
                        int arrow2 = GraphConstants.ARROW_CLASSIC;
                        GraphConstants.setLineEnd(edge2.getAttributes(), arrow);
                        GraphConstants.setEndFill(edge2.getAttributes(), true);

                        jgraph.getGraphLayoutCache().insert(edgeVector[countEdge++]);
                    }// join node
                    else if (outIndex == 1) {
                        DefaultEdge edge3 = new DefaultEdge();
                        edge3.setSource(cellVector[0].getChildAt(0));
                        edge3.setTarget(cellVector[countNode].getChildAt(0));
                        edgeVector[countEdge] = edge3;

                        int arrow = GraphConstants.ARROW_CLASSIC;
                        GraphConstants.setLineEnd(edge3.getAttributes(), arrow);
                        GraphConstants.setEndFill(edge3.getAttributes(), true);

                        jgraph.getGraphLayoutCache().insert(edgeVector[countEdge++]);

                    } else {
                        DefaultEdge edge4 = new DefaultEdge();
                        edge4.setSource(cellVector[countNode - reteList.get(outIndex).size()].getChildAt(0));
                        edge4.setTarget(cellVector[countNode].getChildAt(0));
                        edgeVector[countEdge] = edge4;

                        int arrow = GraphConstants.ARROW_CLASSIC;
                        GraphConstants.setLineEnd(edge4.getAttributes(), arrow);
                        GraphConstants.setEndFill(edge4.getAttributes(), true);

                        jgraph.getGraphLayoutCache().insert(edgeVector[countEdge++]);

                    }
                }

                countNode++;

            }
        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void renderEvalPath(List<ReteNode> inputlist) {

        System.out.println("Called");

        Map nested = new Hashtable();
        Map attributeMap1 = new Hashtable();

        GraphConstants.setBorderColor(attributeMap1, Color.red);

        nested.put(cellVector[0], attributeMap1);
        nested.put(cellVector[1], attributeMap1);
        nested.put(cellVector[4], attributeMap1);
        nested.put(cellVector[7], attributeMap1);
        nested.put(cellVector[8], attributeMap1);
        nested.put(cellVector[9], attributeMap1);
        nested.put(cellVector[10], attributeMap1);
        jgraph.getGraphLayoutCache().edit(nested, null, null, null);
        // jgraph.getGraphLayoutCache().insert(cellVector,nested,null,null,null);

    }

    public void countNumberOfMemory() {
        for (int i = 0; i < inputReteList.size(); i++) {
            for (int j = 0; j < inputReteList.get(i).size(); j++) {
                ReteNode currentNode = inputReteList.get(i).get(j);
                if (currentNode.toString().contains("Alpha Memory"))
                    numberOfAlphaMemory++;
                else if (currentNode.toString().contains("Beta Memory"))
                    numberOfBetaMemory++;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void valueChanged(GraphSelectionEvent arg0) {
        Object lastCell;
        // DefaultTableModel model2 =
        // (DefaultTableModel)TestDiamond.dFrame.table.getModel();

        if (jgraph != null) {
            if (jgraph.getSelectionCount() == 1) {
                lastCell = jgraph.getSelectionCell();

                String cellName = lastCell.toString();
                String msg = cellName + " is selected";

                DMain.mFrame.showMessage(msg);

                countNumberOfMemory();

                for (int outIndex = 0; outIndex < inputReteList.size(); outIndex++) {
                    for (int inIndex = 0; inIndex < inputReteList.get(outIndex).size(); inIndex++) {
                        ReteNode currentNode = inputReteList.get(outIndex).get(inIndex);
                        if (cellName.contains(currentNode.toString()) && cellName.contains("AlphaMemory")) {

                            indexOfMemory = currentNode.getId();

                            DMain.dFrame[indexOfMemory].setIndexOfMemory(indexOfMemory);
                            DMain.dFrame[indexOfMemory].setMemoryNode(currentNode);
                            DMain.dFrame[indexOfMemory].setType(DMain.MEMORY);

                            DMain.dFrame[indexOfMemory].setVisible(true);
                            DMain.dFrame[indexOfMemory].setTitle(currentNode.toString());
                            DMain.dFrame[indexOfMemory].clearModel();

                            msg = "Check the status of " + currentNode.toString() + "\n";
                            DMain.mFrame.showMessage(msg);

                            Set<TripleToken> st = ((AlphaMemory) currentNode).getMemory();

                            if (st != null) {
                                Iterator it = st.iterator();

                                while (it.hasNext()) {
                                    TripleToken tt = (TripleToken) it.next();
                                    Vector temp = new Vector();
                                    temp.add(tt);
                                    DMain.dFrame[indexOfMemory].model.addRow(temp);
                                }

                                DMain.dFrame[indexOfMemory].setNumOfTriple(DMain.dFrame[indexOfMemory].model
                                        .getRowCount());

                            }

                        } else if (cellName.contains(currentNode.toString()) && cellName.contains("BetaMemory")) {

                            indexOfMemory = currentNode.getId();

                            DMain.dFrame[indexOfMemory].setIndexOfMemory(indexOfMemory);
                            DMain.dFrame[indexOfMemory].setMemoryNode(currentNode);
                            DMain.dFrame[indexOfMemory].setType(DMain.MEMORY);

                            DMain.dFrame[indexOfMemory].setVisible(true);
                            DMain.dFrame[indexOfMemory].setTitle(currentNode.toString());
                            DMain.dFrame[indexOfMemory].clearModel();

                            msg = "Check the status of " + currentNode.toString() + "\n";
                            DMain.mFrame.showMessage(msg);

                            Set<TripleToken> st = ((BetaMemory) currentNode).getMemory();

                            if (st != null) {
                                Iterator it = st.iterator();

                                while (it.hasNext()) {
                                    TripleToken tt = (TripleToken) it.next();
                                    Vector temp = new Vector();
                                    temp.add(tt);
                                    DMain.dFrame[indexOfMemory].model.addRow(temp);
                                }

                                DMain.dFrame[indexOfMemory].setNumOfTriple(DMain.dFrame[indexOfMemory].model
                                        .getRowCount());
                            }

                        } // if Beta Memory
                    } // inner loop
                } // outer loop

            } else
                lastCell = null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateMemory(int indexOfMemory, ReteNode selectedNode) {
        ReteNode currentNode = selectedNode;

        DMain.dFrame[indexOfMemory].setVisible(true);
        DMain.dFrame[indexOfMemory].setTitle(currentNode.toString());
        DMain.dFrame[indexOfMemory].clearModel();

        Set<TripleToken> st = ((Memory) currentNode).getMemory();

        if (st != null) {
            Iterator it = st.iterator();

            while (it.hasNext()) {
                TripleToken tt = (TripleToken) it.next();
                Vector temp = new Vector();
                temp.add(tt);
                DMain.dFrame[indexOfMemory].model.addRow(temp);
            }

            DMain.dFrame[indexOfMemory].setNumOfTriple(DMain.dFrame[indexOfMemory].model.getRowCount());

        }
    }

    // old version drawing method
    // for example drawing
    public void updateDraw(int y) {

        // Initialize DataInternalFrame's table model
        for (int i = 0; i < 100; i++) {
            // DMain.dFrame[i].clearModel();
            // DMain.dFrame[i].setVisible(false);
        }
        numberOfInput = y;

        // TestDiamond.addDataWindow(numberOfInput);

        // // should find a way to expand a graphic panel
        if (y > 5) {
            jgraph.setSize(sizeX + 120 * (y - 4), sizeY);
            js.setSize(sizeX + 120 * (y - 4), sizeY);

        }

        int k = 0;

        jgraph.getGraphLayoutCache().remove(cells);
        if (cellVector != null)
            jgraph.getGraphLayoutCache().remove(cellVector);
        if (edgeVector != null)
            jgraph.getGraphLayoutCache().remove(edgeVector);

        cellVector = new DefaultGraphCell[y * 4];
        edgeVector = new DefaultGraphCell[y * 5];

        DefaultGraphCell rootCell;
        rootCell = new DefaultGraphCell(new String("Root Node"));
        GraphConstants.setBounds(rootCell.getAttributes(), new Rectangle2D.Double(sizeX / 2 - 50, sizeY * 1 / 10, 100,
                30));
        // GraphConstants.setGradientColor(rootCell.getAttributes(),Color.BLUE);
        GraphConstants.setBackground(rootCell.getAttributes(), new Color(224, 255, 255));
        GraphConstants.setFont(rootCell.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));
        GraphConstants.setOpaque(rootCell.getAttributes(), true);
        DefaultPort port0 = new DefaultPort();
        rootCell.add(port0);

        cellVector[0] = rootCell;

        if (cellVector != null)
            jgraph.getGraphLayoutCache().insert(cellVector[0]);

        for (int i = 1; i < y + 1; i++) {
            DefaultGraphCell tempCell;
            tempCell = new DefaultGraphCell(new String("Triple Test Node" + i));
            GraphConstants.setBounds(tempCell.getAttributes(), new Rectangle2D.Double(sizeX / (y + 1) * i - 50,
                    sizeY * 2 / 10, 100, 30));
            // GraphConstants.setGradientColor(tempCell.getAttributes(),Color.BLUE);
            GraphConstants.setBackground(tempCell.getAttributes(), new Color(255, 226, 196));
            GraphConstants.setFont(tempCell.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));

            // GraphConstants.setLineColor(tempCell.getAttributes(),
            // Color.blue);
            GraphConstants.setOpaque(tempCell.getAttributes(), true);
            DefaultPort tempPort = new DefaultPort();
            tempCell.add(tempPort);

            cellVector[i] = tempCell;

            if (cellVector != null)
                jgraph.getGraphLayoutCache().insert(cellVector[i]);

            // for edge from root node to triple pattern check node
            DefaultEdge edge = new DefaultEdge();
            edge.setSource(cellVector[0].getChildAt(0));
            edge.setTarget(cellVector[i].getChildAt(0));
            edgeVector[k] = edge;

            int arrow = GraphConstants.ARROW_CLASSIC;
            GraphConstants.setLineEnd(edge.getAttributes(), arrow);
            GraphConstants.setEndFill(edge.getAttributes(), true);

            jgraph.getGraphLayoutCache().insert(edgeVector[k]);
            k++;

        }

        // alpha memory
        int idx = 0;
        for (int i = y + 1; i < 2 * y + 1; i++) {
            idx++;
            DefaultGraphCell tempCell;
            tempCell = new DefaultGraphCell(new String("Alpha Memory" + (i - y)));
            GraphConstants.setBounds(tempCell.getAttributes(), new Rectangle2D.Double(sizeX / (y + 1) * idx - 50,
                    sizeY * 3 / 10, 100, 30));
            // GraphConstants.setGradientColor(tempCell.getAttributes(),Color.BLUE);
            GraphConstants.setBackground(tempCell.getAttributes(), new Color(224, 238, 224));
            GraphConstants.setFont(tempCell.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));

            // GraphConstants.setLineColor(tempCell.getAttributes(),
            // Color.blue);
            GraphConstants.setOpaque(tempCell.getAttributes(), true);
            DefaultPort tempPort = new DefaultPort();
            tempCell.add(tempPort);

            cellVector[i] = tempCell;

            if (cellVector != null)
                jgraph.getGraphLayoutCache().insert(cellVector[i]);

            // edge from triple pattern to alpha memory
            DefaultEdge edge = new DefaultEdge();
            edge.setSource(cellVector[i - y].getChildAt(0));
            edge.setTarget(cellVector[i].getChildAt(0));
            edgeVector[k] = edge;

            int arrow = GraphConstants.ARROW_CLASSIC;
            GraphConstants.setLineEnd(edge.getAttributes(), arrow);
            GraphConstants.setEndFill(edge.getAttributes(), true);

            jgraph.getGraphLayoutCache().insert(edgeVector[k]);
            k++;

        }

        if (y > 1) {

            // Join Node and Beta Node
            int idx2 = 0;
            int idx3 = 0;
            int numOfBeta = 1;
            int numOfJoin = 1;

            for (int i = 2 * y + 1; i < 4 * y - 1;) {
                idx2++;
                idx3++;

                DefaultGraphCell tempCell;
                tempCell = new DefaultGraphCell(new String("Join Node" + numOfJoin));
                GraphConstants.setBounds(tempCell.getAttributes(), new Rectangle2D.Double((sizeX / (2 * (y + 1)))
                        * (idx2 + 2) - 50, sizeY * (3 + idx3) / 10, 100, 30));
                // GraphConstants.setGradientColor(tempCell.getAttributes(),Color.BLUE);
                GraphConstants.setBackground(tempCell.getAttributes(), new Color(238, 207, 161));
                GraphConstants.setFont(tempCell.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));

                // GraphConstants.setLineColor(tempCell.getAttributes(),
                // Color.blue);
                GraphConstants.setOpaque(tempCell.getAttributes(), true);
                DefaultPort tempPort = new DefaultPort();
                tempCell.add(tempPort);

                cellVector[i] = tempCell;

                if (cellVector != null)
                    jgraph.getGraphLayoutCache().insert(cellVector[i]);

                DefaultGraphCell tempCell2;
                tempCell2 = new DefaultGraphCell(new String("Beta Memory" + numOfBeta));
                GraphConstants.setBounds(tempCell2.getAttributes(), new Rectangle2D.Double((sizeX / (2 * (y + 1)))
                        * (idx2 + 2) - 50, sizeY * (4 + idx3) / 10, 100, 30));
                // GraphConstants.setGradientColor(tempCell.getAttributes(),Color.BLUE);
                GraphConstants.setBackground(tempCell2.getAttributes(), new Color(205, 205, 193));
                GraphConstants.setFont(tempCell2.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));

                // GraphConstants.setLineColor(tempCell.getAttributes(),
                // Color.blue);
                GraphConstants.setOpaque(tempCell2.getAttributes(), true);
                DefaultPort tempPort2 = new DefaultPort();
                tempCell2.add(tempPort2);

                cellVector[i + 1] = tempCell2;

                if (cellVector != null)
                    jgraph.getGraphLayoutCache().insert(cellVector[i + 1]);

                DefaultEdge edge = new DefaultEdge();
                edge.setSource(cellVector[i].getChildAt(0));
                edge.setTarget(cellVector[i + 1].getChildAt(0));
                edgeVector[k] = edge;

                int arrow = GraphConstants.ARROW_CLASSIC;
                GraphConstants.setLineEnd(edge.getAttributes(), arrow);
                GraphConstants.setEndFill(edge.getAttributes(), true);

                jgraph.getGraphLayoutCache().insert(edgeVector[k]);
                k++;

                i = i + 2;
                idx3++;
                numOfBeta++;
                numOfJoin++;

            }

            // Result Node
            DefaultGraphCell tempCell3;
            tempCell3 = new DefaultGraphCell(new String("Result Node"));
            GraphConstants.setBounds(tempCell3.getAttributes(), new Rectangle2D.Double((sizeX / (2 * (y + 1)))
                    * (idx2 + 2) - 50, sizeY * (4 + idx3) / 10, 100, 30));
            // GraphConstants.setGradientColor(tempCell.getAttributes(),Color.BLUE);
            GraphConstants.setBackground(tempCell3.getAttributes(), new Color(224, 255, 255));
            GraphConstants.setFont(tempCell3.getAttributes(), new Font("Eras Demi ITC", Font.PLAIN, 10));

            // GraphConstants.setLineColor(tempCell.getAttributes(),
            // Color.blue);
            GraphConstants.setOpaque(tempCell3.getAttributes(), true);
            DefaultPort tempPort3 = new DefaultPort();
            tempCell3.add(tempPort3);

            cellVector[4 * y - 1] = tempCell3;

            if (cellVector != null)
                jgraph.getGraphLayoutCache().insert(cellVector[4 * y - 1]);

            // edge from alpha memory to join node (first one)

            DefaultEdge edge = new DefaultEdge();
            edge.setSource(cellVector[y + 1].getChildAt(0));
            edge.setTarget(cellVector[2 * y + 1].getChildAt(0));
            edgeVector[k] = edge;

            int arrow = GraphConstants.ARROW_CLASSIC;
            GraphConstants.setLineEnd(edge.getAttributes(), arrow);
            GraphConstants.setEndFill(edge.getAttributes(), true);

            jgraph.getGraphLayoutCache().insert(edgeVector[k]);
            k++;

            // edge from alpha memory to join node

            int idx4 = 0;
            for (int i = y + 2; i <= 2 * y; i++) {

                DefaultEdge edge2 = new DefaultEdge();
                edge2.setSource(cellVector[i].getChildAt(0));
                edge2.setTarget(cellVector[2 * y + 1 + idx4].getChildAt(0));
                edgeVector[k] = edge2;

                @SuppressWarnings("unused")
                int arrow2 = GraphConstants.ARROW_CLASSIC;
                GraphConstants.setLineEnd(edge2.getAttributes(), arrow);
                GraphConstants.setEndFill(edge2.getAttributes(), true);

                jgraph.getGraphLayoutCache().insert(edgeVector[k]);

                k++;
                idx4 = idx4 + 2;

            }

            // edge from beta memory to join node
            for (int j = 2 * y + 2; j < 4 * y - 2; j = j + 2) {
                DefaultEdge edge3 = new DefaultEdge();
                edge3.setSource(cellVector[j].getChildAt(0));
                edge3.setTarget(cellVector[j + 1].getChildAt(0));
                edgeVector[k] = edge3;

                @SuppressWarnings("unused")
                int arrow3 = GraphConstants.ARROW_CLASSIC;
                GraphConstants.setLineEnd(edge3.getAttributes(), arrow);
                GraphConstants.setEndFill(edge3.getAttributes(), true);

                jgraph.getGraphLayoutCache().insert(edgeVector[k]);
                k++;

            }

            DefaultEdge edge3 = new DefaultEdge();
            edge3.setSource(cellVector[4 * y - 2].getChildAt(0));
            edge3.setTarget(cellVector[4 * y - 1].getChildAt(0));
            edgeVector[k] = edge3;

            @SuppressWarnings("unused")
            int arrow3 = GraphConstants.ARROW_CLASSIC;
            GraphConstants.setLineEnd(edge3.getAttributes(), arrow);
            GraphConstants.setEndFill(edge3.getAttributes(), true);

            jgraph.getGraphLayoutCache().insert(edgeVector[k]);

        }// if y>2
    }
}
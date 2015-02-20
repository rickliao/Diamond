package diamond.gDebugger;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import diamond.data.*;
import diamond.retenetwork.*;

/**
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class DataInternalFrame extends MyInternalFrame implements ListSelectionListener, ActionListener {

    JLabel label;
    JPanel bPanel;
    JButton start;
    JTable table;
    JPanel dPanel;
    int numOfTriple;
    TripleToken selectedToken;

    JScrollPane tablePane;
    DefaultTableModel model;
    ListSelectionModel listSelectionModel;

    String colName[] = { "contents" };

    ReteNetwork reteNetwork;
    ReteNode memoryNode;
    int indexOfMemory;
    int type = 1;

    int sizeX;
    int sizeY;

    public DataInternalFrame() {
        super("Memory Status", true, false, false, true);
        label = new JLabel("# of Triples in memory :" + numOfTriple);
        start = new JButton("refresh");

        model = new DefaultTableModel(colName, 0);
        table = new JTable(model);
        table.setGridColor(Color.black);
        table.setAutoResizeMode(WIDTH);
        tablePane = new JScrollPane(table);
        tablePane.setAutoscrolls(true);

        start.addActionListener(this);
        bPanel = new JPanel();
        bPanel.add(start);

        dPanel = new JPanel();
        dPanel.setLayout(new BorderLayout());
        dPanel.add(label, BorderLayout.NORTH);
        dPanel.add(tablePane, BorderLayout.CENTER);
        dPanel.add(bPanel, BorderLayout.SOUTH);

        listSelectionModel = table.getSelectionModel();
        // listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(this);
        table.setSelectionModel(listSelectionModel);

        getContentPane().add(dPanel);
    }

    public void setMemoryNode(ReteNode inputNode) {
        this.memoryNode = inputNode;
    }

    public void setIndexOfMemory(int indexOfMemory) {
        this.indexOfMemory = indexOfMemory;
    }

    public void setReteNetwork(ReteNetwork network) {
        this.reteNetwork = network;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNumOfTriple(int input) {
        this.numOfTriple = input;
        label.setText("# of Triples in memory :" + numOfTriple);
        label.revalidate();
    }

    public void setXY(int x, int y) {
        this.sizeX = x;
        this.sizeY = y;
    }

    public void clearModel() {
        model.setRowCount(0);
        // TestDiamond.mFrame.updateMsgValues("Row "+model.getRowCount());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

        int index = lsm.getAnchorSelectionIndex();
        Object output = model.getValueAt(index, 0);
        DMain.mFrame.showMessage(output.toString());

        selectedToken = (TripleToken) output;

    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            if (type == DMain.MEMORY) {
                DMain.gFrame.updateMemory(indexOfMemory, memoryNode);

            } else if (type == DMain.SOLUTION) {

                System.out.println("Solutin Frame");
                this.setVisible(true);
                this.clearModel();

                String result = reteNetwork.getSolutionSet().toString();
                @SuppressWarnings("rawtypes")
                Vector temp = new Vector();
                temp.add(result);
                this.model.addRow(temp);
            }
        }
    }
}
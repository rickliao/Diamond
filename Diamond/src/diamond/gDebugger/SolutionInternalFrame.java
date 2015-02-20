package diamond.gDebugger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import diamond.data.*;
import diamond.retenetwork.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class SolutionInternalFrame extends MyInternalFrame implements ActionListener {

    JLabel titleLabel;
    JTextArea solutionTextArea;
    JScrollPane solutionScrollPane;
    JButton updateButton;
    JButton exportButton;
    ReteNetwork reteNetwork;
    SolutionSet solnSet;

    public SolutionInternalFrame() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel bPanel = new JPanel(); // for buttons

        titleLabel = new JLabel("Solution Frame");
        solutionTextArea = new JTextArea();
        solutionTextArea.setEditable(false);
        solutionScrollPane = new JScrollPane(solutionTextArea);
        updateButton = new JButton("Update Solution");
        updateButton.addActionListener(this);
        exportButton = new JButton("Export Solution");
        exportButton.addActionListener(this);

        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder title;
        title = BorderFactory.createTitledBorder(blackline, "Solution set");
        title.setTitleJustification(TitledBorder.CENTER);
        solutionScrollPane.setBorder(title);

        bPanel.add(updateButton, BorderLayout.WEST);
        bPanel.add(exportButton, BorderLayout.EAST);

        panel.add(titleLabel, BorderLayout.PAGE_START);
        panel.add(solutionScrollPane, BorderLayout.CENTER);
        panel.add(bPanel, BorderLayout.PAGE_END);

        getContentPane().add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton) {
            String result = reteNetwork.getSolutionSet().toString();
            solutionTextArea.setText(result);
            solutionTextArea.setCaretPosition(solutionTextArea.getDocument().getLength());
        } else if (e.getSource() == exportButton) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Select a file");
            fileChooser.setApproveButtonText("Save");

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(solutionTextArea.getText());
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setReteNetwork(ReteNetwork network) {
        reteNetwork = network;
    }

    public void setSolutionSet(SolutionSet solnSet) {
        this.solnSet = solnSet;
    }
}

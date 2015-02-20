package diamond.gDebugger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import diamond.retenetwork.ReteNode;

/**
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class TripleTokenInternalFrame extends MyInternalFrame implements ActionListener {

    JTextArea inputArea;
    JTextArea resultArea;
    JScrollPane sp;
    JScrollPane sp2;
    JButton button;
    JPanel topPanel;
    JPanel panel;

    public TripleTokenInternalFrame() {
        super("RDF Triple Evaluation", true, false, false, true);

        inputArea = new JTextArea(3, 30);
        sp = new JScrollPane(inputArea);
        sp.setAutoscrolls(true);

        resultArea = new JTextArea(5, 35);
        sp2 = new JScrollPane(resultArea);
        sp2.setAutoscrolls(true);

        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder title;
        title = BorderFactory.createTitledBorder(blackline, "Triple Token input");
        title.setTitleJustification(TitledBorder.CENTER);
        sp.setBorder(title);

        BorderFactory.createLineBorder(Color.black);
        TitledBorder title2;
        title2 = BorderFactory.createTitledBorder(blackline, "Result");
        title.setTitleJustification(TitledBorder.CENTER);
        sp2.setBorder(title2);

        button = new JButton("Execute");
        button.addActionListener(this);

        topPanel = new JPanel();
        panel = new JPanel();
        // panel.setLayout(new BorderLayout());
        topPanel.add(sp);
        topPanel.add(button);
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(sp2, BorderLayout.SOUTH);

        getContentPane().add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            inputArea.getText();
            // temporary code
            List<ReteNode> rtList = DMain.gFrame.rtList;
            DMain.gFrame.renderEvalPath(rtList);
            resultArea.setText("root -> TriplePattern1 -> AlphaMemory1 -> JoinNode -> BetaMemory1");
        }

    }
}

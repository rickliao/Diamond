package diamond.gDebugger;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

import diamond.retenetwork.ReteNetwork;
import diamond.retenetwork.ReteNode;

/**
 * Debugger Main Class
 * 
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class DMain extends JFrame implements ActionListener {

    static JDesktopPane desktopPane = new JDesktopPane();
    static MessageInternalFrame mFrame = new MessageInternalFrame();
    static GraphInternalFrame gFrame = new GraphInternalFrame();
    static DataInternalFrame[] dFrame = new DataInternalFrame[100];
    static InputInternalFrame iFrame = new InputInternalFrame();
    static SolutionInternalFrame solutionFrame = new SolutionInternalFrame();
    // static TRepositoryInternalFrame qFrame = new TRepositoryInternalFrame();
    JPanel graphPanel;
    JPanel dataPanel;
    JPanel statusPanel;
    static int sizeX;
    static int sizeY;

    static final int MEMORY = 0;
    static final int SOLUTION = 1;

    public DMain() {
        super("Diamond SPARQL Debugger");
        this.setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        sizeX = screenSize.width;
        sizeY = screenSize.height - 50;

        initGUI();

        this.getContentPane().add(DMain.desktopPane, java.awt.BorderLayout.CENTER);
        DMain.iFrame.setLocation(sizeX / 5 * 3 - 1, 0);
        DMain.iFrame.setSize(sizeX / 5 * 2 - 1, sizeY / 5 * 3);
        DMain.desktopPane.add(DMain.iFrame);
        DMain.iFrame.setVisible(true);

        DMain.gFrame.setLocation(0, 0);
        DMain.gFrame.setSize(sizeX / 5 * 3 - 1, sizeY / 5 * 3);
        DMain.desktopPane.add(DMain.gFrame);
        DMain.gFrame.setVisible(true);

        DMain.solutionFrame.setLocation(sizeX / 5 * 3 - 1, sizeY / 5 * 3);
        DMain.solutionFrame.setSize(sizeX / 5 * 2 - 1, sizeY / 5 * 2);
        DMain.desktopPane.add(DMain.solutionFrame);
        DMain.solutionFrame.setVisible(true);
        DMain.solutionFrame.setTitle("Solution");

        for (int i = 0; i < 100; i++) {
            DMain.dFrame[i] = new DataInternalFrame();
            DMain.dFrame[i].setLocation(sizeX / 5 * 3 - 1 - i * 5, sizeY / 5 * 2 - i * 5);
            DMain.dFrame[i].setSize(sizeX / 5 * 2 - 1, sizeY / 5 * 1);
            DMain.desktopPane.add(DMain.dFrame[i]);
            DMain.dFrame[i].setXY(sizeX / 5 * 2 - 1, sizeY / 5 * 1);
            DMain.dFrame[i].setVisible(false);
        }

        DMain.mFrame.setLocation(0, sizeY / 5 * 3);
        DMain.mFrame.setSize(sizeX / 5 * 3 - 1, sizeY / 5 * 2 - 30);
        DMain.desktopPane.add(DMain.mFrame);
        DMain.mFrame.setVisible(true);

        // DMain.qFrame.setLocation(sizeX/5*3-1,sizeY/5*2);
        // DMain.qFrame.setSize(500,250);
        // DMain.desktopPane.add(DMain.qFrame);
        // DMain.qFrame.setVisible(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(DMain.sizeX, DMain.sizeY);
        this.setLocation(0, 0);
        this.setVisible(true);
        DMain.desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

    }
    
    // Test ******************
    public static void dMain(ReteNetwork reteNetwork) {
        ArrayList<ArrayList<ReteNode>> listOfReteNetwork;
        new DMain();
        DMain.solutionFrame.setReteNetwork(reteNetwork);
        listOfReteNetwork = reteNetwork.getListOfReteNodesInNetwork();
        DMain.gFrame.updateDraw(listOfReteNetwork);
    }

    private void initGUI() {
        // int inset = 50;
        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // screenSize.width = (int)(screenSize.width *0.7);
        // screenSize.height = (int)(screenSize.height*0.8);
        // setBounds(inset, inset, (screenSize.width) - inset*2,
        // (screenSize.height)- inset*2);
        DMain.desktopPane.setBackground(new Color(221, 221, 221));
        DMain.desktopPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

        // this.getContentPane().setLayout(new BorderLayout());
    }

    // For the ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        // to do
    }

    // Main Method
    public static void main(String args[]) {
        try {
            new DMain();

        } catch (Exception e) {
            String msg = e.getMessage();
            DMain.mFrame.showMessage(msg);
            System.out.println(msg);
        }
    }

}

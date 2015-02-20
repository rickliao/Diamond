package diamond.gDebugger;

import javax.swing.JInternalFrame;

/**
 * Parent Class for internal Frame
 * 
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class MyInternalFrame extends JInternalFrame {

    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;

    public MyInternalFrame() {
        super("Document #" + (++openFrameCount), true, // resizable
                true, // closable
                true, // maximizable
                true);// iconifiable

        // ...Create the GUI and put it in the window...

        // ...Then set the window size or call pack...
        // setSize(300,300);

        // Set the window's location.
        // setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }

    public MyInternalFrame(String title, boolean res, boolean clo, boolean max, boolean ico) {
        super(title, res, clo, max, ico);
    }
}
package diamond.gDebugger;

import javax.swing.*;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * This internal frame shows message 
 * corresponding to events during the program execution.
 * 
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class MessageInternalFrame extends MyInternalFrame {

    String initMsg = "Program is initialized...\n";
    JTextArea msgTextArea = new JTextArea();
    JScrollPane msgTextPane = new JScrollPane(msgTextArea);

    public MessageInternalFrame() {
        super("Message Status", true, false, false, true);

        Font aFont = new Font("Eras Demi ITC", Font.PLAIN, 12);

        msgTextArea.setBounds(0, 0, ImageObserver.WIDTH, ImageObserver.HEIGHT);
        msgTextArea.setBackground(new Color(153, 204, 255));
        msgTextArea.setFont(aFont);
        msgTextArea.setEditable(false);
        msgTextArea.append(initMsg);

        getContentPane().add(msgTextPane);

    }

    // update its textarea with an input string message
    public void showMessage(String msg) {
        msgTextArea.append(msg + "\n");

        msgTextArea.setCaretPosition(msgTextArea.getDocument().getLength());
    }

}
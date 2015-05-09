package diamond.gDebugger;

import javax.swing.*;
import javax.swing.border.*;

import org.openrdf.rio.RDFFormat;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;

import diamond.processors.*;
import diamond.managers.*;
import diamond.retenetwork.*;
import diamond.data.*;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;
import diamond.retenetwork.ReteNetwork;

/**
 * @author Hyun Joon Jung
 */
@SuppressWarnings("serial")
public class InputInternalFrame extends MyInternalFrame implements ActionListener {

    boolean debug = true;
    JPanel tabPanel1;
    JPanel tabPanel2;
    JPanel inputPanel;
    JLabel inputLabel;
    JTextField inputText;
    JTextArea textArea;
    JScrollPane scrollPane;
    JButton openButton;
    JButton runButton;
    JFileChooser fileChooser1; // query file chooser for normal
    JButton dataFileButton;
    JLabel dataFileLabel;
    JButton queryFileButton;
    JLabel queryFileLabel;
    JButton testButton;
    JButton clearMemButton;
    JFileChooser fileChooser2; // query file chooser for test
    JFileChooser fileChooser3; // data file chooser for test
    String newline = "\n";
    ArrayList<ArrayList<ReteNode>> listOfReteNetwork;
    ReteNetwork reteNetwork;

    JLabel dataFileName;
    JLabel queryFileName;

    JRadioButton rButton1;
    JRadioButton rButton2;
    JRadioButton rButton3;

    JTextArea textArea2;
    JScrollPane scrollPane2;

    JTabbedPane tabbedPane = new JTabbedPane();

    QueryProcessor queryProcessor = null;
    LinkedDataManager linkedDataManager = null;

    int sizeX;
    int sizeY;

    final String FS = File.separator;

    File dataFile;
    File queryFile;

    RDFFormat rdfFormat;
    NativeStoreStorageManager storageManager;

    String defaultDir;

    public InputInternalFrame() {

        super("Input Frame", true, false, false, true);

        tabPanel1 = new JPanel();
        inputPanel = new JPanel();
        tabPanel1.setLayout(new BorderLayout());

        tabPanel2 = new JPanel();

        inputLabel = new JLabel("Input Query");
        inputText = new JTextField(20);
        fileChooser1 = new JFileChooser();
        openButton = new JButton("Open");
        openButton.addActionListener(this);
        runButton = new JButton("Run");
        runButton.addActionListener(this);
        inputPanel.add(inputLabel);
        inputPanel.add(inputText);
        inputPanel.add(openButton);
        inputPanel.add(runButton);

        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        // taPane.setPreferredSize(new Dimension(500,200));
        scrollPane.setAutoscrolls(true);

        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder title;
        title = BorderFactory.createTitledBorder(blackline, "Input SPARQL");
        title.setTitleJustification(TitledBorder.CENTER);
        scrollPane.setBorder(title);

        tabPanel1.add(inputPanel, BorderLayout.PAGE_START);
        tabPanel1.add(scrollPane, BorderLayout.CENTER);
        // this.getContentPane().add(inputPanel,BorderLayout.PAGE_START);

        // sizeX = TestDiamond.sizeX/5*2;
        // sizeY = TestDiamond.sizeY/5*1;
        // setSize(sizeX,sizeY);
        // setLocation(TestDiamond.sizeX/5*3,0);

        tabbedPane.add(tabPanel1, "Normal Execution");

        dataFileButton = new JButton("Data File");
        dataFileButton.addActionListener(this);
        fileChooser2 = new JFileChooser();
        dataFileLabel = new JLabel("Data File");
        queryFileButton = new JButton("Query File");
        queryFileButton.addActionListener(this);
        fileChooser3 = new JFileChooser();
        queryFileLabel = new JLabel("Query File");
        testButton = new JButton("Test Button");
        testButton.addActionListener(this);
        clearMemButton = new JButton("Clear Memory");
        clearMemButton.addActionListener(this);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 4));
        // tabPanel2.setLayout(new GridLayout(3,0));
        tabPanel2.setLayout(new BorderLayout());

        topPanel.add(queryFileLabel);
        topPanel.add(queryFileButton);
        topPanel.add(dataFileLabel);
        topPanel.add(dataFileButton);

        defaultDir = "";
        fileChooser1.setCurrentDirectory(new File(defaultDir));
        fileChooser1.setDialogTitle("Select a file");
        fileChooser2.setCurrentDirectory(new File(defaultDir));
        fileChooser2.setDialogTitle("Select a file");
        fileChooser3.setCurrentDirectory(new File(defaultDir));
        fileChooser3.setDialogTitle("Select a file");

        // rButton1 = new JRadioButton("NTriple");
        // rButton1.setActionCommand("NTriple");
        // rButton1.setSelected(true);
        // rButton2 = new JRadioButton("N3");
        // rButton2.setActionCommand("N3");
        // rButton3 = new JRadioButton("N/A");

        // ButtonGroup bg = new ButtonGroup();
        // bg.add(rButton1);
        // bg.add(rButton2);
        // bg.add(rButton3);

        // rButton1.addActionListener(this);
        // rButton2.addActionListener(this);
        // rButton3.addActionListener(this);

        // JPanel radioPanel = new JPanel(new GridLayout(1, 0));
        // topPanel.add(new JLabel("RDF Type"));
        // topPanel.add(rButton1);
        // topPanel.add(rButton2);
        // topPanel.add(rButton3);

        JPanel labelPanel = new JPanel(new GridLayout(2, 1));

        textArea2 = new JTextArea();
        scrollPane2 = new JScrollPane(textArea2);
        // taPane.setPreferredSize(new Dimension(500,200));
        scrollPane2.setAutoscrolls(true);

        dataFileName = new JLabel("Data File :");
        queryFileName = new JLabel("Query File :");

        labelPanel.add(dataFileName);
        labelPanel.add(queryFileName);
        labelPanel.add(testButton);
        labelPanel.add(clearMemButton);

        BorderFactory.createLineBorder(Color.black);
        title = BorderFactory.createTitledBorder(blackline, "Input SPARQL QUERY");
        title.setTitleJustification(TitledBorder.CENTER);
        scrollPane2.setBorder(title);

        tabPanel2.add(topPanel, BorderLayout.PAGE_START);
        tabPanel2.add(scrollPane2, BorderLayout.CENTER);
        tabPanel2.add(labelPanel, BorderLayout.PAGE_END);

        tabbedPane.add(tabPanel2, "Test Code Execution");

        getContentPane().add(tabbedPane);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String msg;

        // case for open button
        if (e.getSource() == openButton) {
            // Open fileDialog
            fileChooser1.setCurrentDirectory(new File(defaultDir));
            int returnVal = fileChooser1.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File qfile = fileChooser1.getSelectedFile();
                defaultDir = qfile.getParent();

                msg = "Opening: " + qfile.getName() + "." + newline;
                DMain.mFrame.showMessage(msg);

                try {

                    queryProcessor = new FileQueryProcessor(qfile, debug);

                    @SuppressWarnings("resource")
                    BufferedReader input = new BufferedReader(new FileReader(qfile));
                    String line = null; // not declared within while loop
                    StringBuilder contents = new StringBuilder();

                    while ((line = input.readLine()) != null) {
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }

                    textArea.setText(contents.toString());
                    System.out.println(contents.toString());

                    queryProcessor.process();// execute sparql parsing

                    if (debug) {
                        // System.err.println(queryProcessor.getSparqlParser().getURLs());
                    }

                } catch (Exception ee) {
                    ee.printStackTrace();
                }

                msg = "Query processing...";
                DMain.mFrame.showMessage(msg);

                try {
                    linkedDataManager = new LinkedDataManager(queryProcessor);

                } catch (Exception ex) {
                    DMain.mFrame.showMessage(ex.toString());
                }

                reteNetwork = queryProcessor.getSparqlParser().getReteNetwork();
                DMain.solutionFrame.setReteNetwork(reteNetwork);
                listOfReteNetwork = reteNetwork.getListOfReteNodesInNetwork();

                DMain.gFrame.updateDraw(listOfReteNetwork);

                Set<TripleToken> results = null;
                QueryThread qc = new QueryThread(results);
                qc.start();
            }
            // Handle save button action.
        } else if (e.getSource() == runButton) {

            msg = "Run button is clicked" + newline;
            DMain.mFrame.showMessage(msg);

            String querySource = textArea.getText();
            System.out.println("Here:" + querySource);

            queryProcessor = new StringQueryProcessor(querySource, debug);

            msg = "Query processing...";
            DMain.mFrame.showMessage(msg);

            try {
                queryProcessor.process();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // SolutionSet solutionSet = new
            // SolutionSet(queryProcessor.getSparqlParser().returnSelectList());
            // DMain.solutionFrame.setSolutionSet(solutionSet);

            try {
                linkedDataManager = new LinkedDataManager(queryProcessor);
            } catch (Exception ex) {
                DMain.mFrame.showMessage(ex.toString());
            }

            reteNetwork = queryProcessor.getSparqlParser().getReteNetwork();
            DMain.solutionFrame.setReteNetwork(reteNetwork);
            listOfReteNetwork = reteNetwork.getListOfReteNodesInNetwork();

            DMain.gFrame.updateDraw(listOfReteNetwork);

            Set<TripleToken> results = null;
            QueryThread qc = new QueryThread(results);
            qc.start();

        } else if (e.getSource() == queryFileButton) {
            fileChooser2.setCurrentDirectory(new File(defaultDir));
            int returnVal = fileChooser2.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                queryFile = fileChooser2.getSelectedFile();
                defaultDir = queryFile.getParent();

                msg = "Opening: " + queryFile.getName() + " as a query file." + newline;
                DMain.mFrame.showMessage(msg);
                queryFileName.setText("Query File: " + queryFile.getName());
                reteNetwork = null;
            }
        } else if (e.getSource() == dataFileButton) {
            fileChooser3.setCurrentDirectory(new File(defaultDir));
            int returnVal = fileChooser3.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dataFile = fileChooser3.getSelectedFile();
                defaultDir = dataFile.getParent();

                msg = "Opening: " + dataFile.getName() + " as a data file." + newline;
                DMain.mFrame.showMessage(msg);
                dataFileName.setText("Data File: " + dataFile.getName());

            }
        } else if (e.getSource() == rButton1) {
            rdfFormat = RDFFormat.NTRIPLES;
        } else if (e.getSource() == rButton2) {
            rdfFormat = RDFFormat.N3;

        } else if (e.getSource() == testButton) {

            QueryProcessor queryProcessor;

            try {

                if (reteNetwork == null) {
                    BufferedReader input = new BufferedReader(new FileReader(queryFile));
                    String line = null; // not declared within while loop
                    StringBuilder contents = new StringBuilder();

                    while ((line = input.readLine()) != null) {
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                    input.close();

                    textArea2.setText(contents.toString());
                    queryProcessor = new FileQueryProcessor(queryFile, true);

                    // create rete network from query
                    queryProcessor.process();

                    storageManager = new NativeStoreStorageManager(queryProcessor, dataFile);
                    DMain.mFrame.showMessage("Storage Manager is initialized...");

                    // storageManager.getTripleTokens(new TriplePattern(new
                    // Element(null,null,null),new Element(null,null,null),new
                    // Element(null,null,null)));
                } else if (textArea2.getText().length() != 0) {
                    String querySource = textArea2.getText();
                    queryProcessor = new StringQueryProcessor(querySource, true);

                    queryProcessor.process();
                    storageManager = new NativeStoreStorageManager(queryProcessor, dataFile);
                    DMain.mFrame.showMessage("Storage Manager is initialized...");

                } else {
                    storageManager.setFile(dataFile);
                }
                // execute query

                storageManager.executeQuery(false);

                // extract rete network
                this.reteNetwork = storageManager.getReteNetwork();

                // get list of rete nodes
                listOfReteNetwork = this.reteNetwork.getListOfReteNodesInNetwork();

                // update solution frame
                DMain.solutionFrame.setSolutionSet(this.reteNetwork.getSolutionSet());
                DMain.solutionFrame.setReteNetwork(this.reteNetwork);

                // System.out.println(listOfReteNetwork.size());
                // System.out.println(listOfReteNetwork.get(1).size());
                DMain.gFrame.updateDraw(listOfReteNetwork);

            } catch (Exception ee) {
                ee.toString();
            }

        } else if (e.getSource() == clearMemButton) {
            reteNetwork.getRoot().clearMemories();

            // update solution frame
            DMain.solutionFrame.setSolutionSet(this.reteNetwork.getSolutionSet());
            DMain.solutionFrame.setReteNetwork(this.reteNetwork);

            DMain.gFrame.updateDraw(listOfReteNetwork);
        }
    }

    // Worker Thread for the Query execution of Linked Data
    class QueryThread extends Thread {

        Set<TripleToken> results;

        public QueryThread(Set<TripleToken> input) {
            results = input;
        }

        @Override
        public void run() {
            try {
                linkedDataManager.executeQueryOnWebOfLinkedData(null, null, false, false);
            } catch (Exception doE) {
                String msg = doE.getMessage();
                System.out.println("~~~~" + doE);
                DMain.mFrame.showMessage(msg);
            }
        }

        public Set<TripleToken> getResults() {
            return results;
        }
    }

}

package diamond.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import diamond.data.Binding;
import diamond.data.TokenQueue;
import diamond.data.TripleToken;
import diamond.gDebugger.DMain;
import diamond.managers.SpinRuleManager;
import diamond.parser.ParseException;
import diamond.processors.FileQueryProcessor;
import diamond.retenetwork.ReteNetwork;
import diamond.spin.ConflictSet;
import diamond.spin.ConflictSetUnit;
import diamond.spin.WorkingMemory;

/**
 * Main program for SpinDiamond.
 * 
 * @author Slavcho Slavchev
 */
public class Spin {

    public static void main(String[] args) {
        String spinFile = null;
        String dataFile = null;
        boolean debugMode = false;

        String manual = "usage: SpinDiamond.jar [parameters]\n";
        manual += "     -spinfile   <file>      Execute a SPIN Rules File on local data.\n";
        manual += "     -datafile   <file>      Input RDF Data File to execute query on.\n";
        manual += "     -debug                  Enter debug mode.\n";
        manual += "     -tutorial               Quick tutorial.\n";

        // Iterate through the arguments
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-spinfile")) {
                // look at next argument for file name
                if (i + 1 < args.length) {
                    spinFile = args[i + 1];
                }
            } else if (args[i].equals("-datafile")) {
                // look at next argument for file name
                if (i + 1 < args.length) {
                    dataFile = args[i + 1];
                }
            } else if (args[i].equals("-debug")) {
                debugMode = true;
            } else if(args[i].equals("-tutorial")) {
                try {
                    Spin x = new Spin();
                    x.tutorial();
                } catch(Exception e) {
                    System.out.println("Unable to display Tutorial!");
                }
                System.exit(0);
            }
        }

        if (valid(spinFile) && valid(dataFile)) {
            try {
                exec(spinFile, dataFile, debugMode);
            } catch (Exception e) {
                System.err.println("Exception occured!");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println(manual);
            String error = "Error: You must specify both spinfile and datafile!";
            System.err.println(error);
            System.exit(1);
        }
    }

    public static void exec(String spinFile, String dataFile, boolean debugMode) throws Exception {
        File rules = new File(spinFile);
        File data = new File(dataFile);

        // Compile and Parse Rules
        FileQueryProcessor queryProcessor = new FileQueryProcessor(rules, true);
        try {
            queryProcessor.processSpinRule();
        } catch (ParseException e) {
            System.err.println("Invalid SPIN Rule!");
            System.err.println(e);
            System.exit(1);
        }

        SpinRuleManager manager = new SpinRuleManager(queryProcessor, data);
        TokenQueue tokenQueue = manager.getTokenQueue();
        ConflictSet conflictSet = manager.getConflictSet();

        WorkingMemory wm = new WorkingMemory();
        for (TripleToken token : tokenQueue) {
            wm.accept(token);
        }
        
        if(!debugMode) {
            while(tokenQueue.size() > 0) {
                manager.processNextTripleToken();
            }
            manager.flushBetaMemoryDataToConflicSet();
        }

        // Start Processing the Rules and Data
        Scanner in = new Scanner(System.in); // user input
        System.out.println("Type 'help' for a list with all commands.");
        while (true) {
            System.out.print("\n> ");
            String command = in.next();

            if (command.equals("help")) {
                System.out.println(userGuide());
            } else if (command.equals("exit")) {
                in.close();
                System.exit(0);
            }

            else if (debugMode && command.equals("tq-size")) {
                int size = tokenQueue.size();
                System.out.println("TokenQueue size: " + size);
            } else if (debugMode && command.equals("tq-print")) {
                for (TripleToken token : tokenQueue) {
                    String meta = token.getTag() + ", " + token.getTTTimestamp() + ", ";
                    for (Binding binding : token.getBindings()) {
                        System.out.println("(" + meta + "[" + binding.getRdfTriple() + "])");
                    }
                }
            }

            else if (command.equals("cs-size")) {
                int size = manager.getConflictSet().size();
                System.out.println("ConflictSet size: " + size);
            } else if (command.equals("cs-print")) {
                for (ConflictSetUnit csu : manager.getConflictSet()) {
                    System.out.println();
                    System.out.println(csu);
                }
            }

            else if (debugMode && command.equals("nt-print")) {
                if (!tokenQueue.isEmpty()) {
                    TripleToken token = tokenQueue.peek();
                    String meta = token.getTag() + ", " + token.getTTTimestamp() + ", ";
                    for (Binding binding : token.getBindings()) {
                        System.out.println("(" + meta + "[" + binding.getRdfTriple() + "])");
                    }
                } else {
                    System.out.println("The TokenQueue is empty!");
                }
            } else if (debugMode && command.equals("nt-process")) {
                if (!tokenQueue.isEmpty()) {
                    manager.processNextTripleToken();
                    manager.flushBetaMemoryDataToConflicSet();
                } else {
                    System.out.println("The TokenQueue is empty!");
                }
            } else if (debugMode && command.equals("nt-quick-process")) {
                int steps = in.nextInt();

                for (int i = 0; i < steps; ++i) {
                    if (!tokenQueue.isEmpty()) {
                        manager.processNextTripleToken();
                        manager.flushBetaMemoryDataToConflicSet();
                    } else {
                        System.out.println("The TokenQueue is empty!");
                        break;
                    }
                }
            } else if(debugMode && command.equals("last-mem")) {
            	manager.printLastReteMemory();	
            }

            else if (command.equals("nr-print")) {
                if (!conflictSet.isEmpty()) {
                    ConflictSetUnit csu = manager.getConflictSet().peek();
                    System.out.println(csu);
                } else {
                    System.out.println("\nThe ConflictSet is empty!");
                }
            } else if (debugMode && command.equals("nr-fire")) {
                List<TripleToken> consequentData = null;
                if (!conflictSet.isEmpty()) {
                    ConflictSetUnit csunit = manager.getNextConflictSetUnit();
                    System.out.println("\nFire " + csunit.getRuleToFire() + " for: " + csunit.getInstantiation());
                    consequentData = csunit.getRuleToFire().evalRule(csunit.getInstantiation());
                    
                    if(consequentData.size() == 0) {
                        System.out.println("\nNo new Tokens:");
                    } else {
                        System.out.println("\nNew Tokens:");
                    } for (TripleToken newTT : consequentData) {
                        System.out.println(newTT);
                        tokenQueue.add(newTT);
                        wm.accept(newTT);
                    }
                } else {
                    System.out.println("\nThe ConflictSet is empty!");
                }
            } else if (debugMode && command.equals("nr-quick-fire")) {
                int steps = in.nextInt();

                List<TripleToken> consequentData = null;
                for (int i = 0; i < steps; ++i) {
                    if (!conflictSet.isEmpty()) {
                        ConflictSetUnit csunit = manager.getNextConflictSetUnit();
                        System.out.println("\nFire " + csunit.getRuleToFire() + " for: " + csunit.getInstantiation());
                        consequentData = csunit.getRuleToFire().evalRule(csunit.getInstantiation());
                        
                        if(consequentData.size() == 0) {
                            System.out.println("\nNo new Tokens:");
                        } else {
                            System.out.println("\nNew Tokens:");
                        } for (TripleToken newTT : consequentData) {
                            System.out.println(newTT);
                            tokenQueue.add(newTT);
                            wm.accept(newTT);
                        }
                    } else {
                        System.out.println("The ConflictSet is empty!");
                        break;
                    }
                }
            }

            else if (command.equals("run")) {
                int steps = in.nextInt(), step = 0;

                while (step < steps) {
                    // Fire next rule
                    List<TripleToken> consequentData = null;
                    if (!conflictSet.isEmpty()) {
                        ConflictSetUnit csunit = manager.getNextConflictSetUnit();
                        System.out.println("\nFire " + csunit.getRuleToFire() + " for: " + csunit.getInstantiation());
                        consequentData = csunit.getRuleToFire().evalRule(csunit.getInstantiation());

                        if(consequentData.size() == 0) {
                            System.out.println("\nNo new Tokens:");
                        } else {
                            System.out.println("\nNew Tokens:");
                        } for (TripleToken newTT : consequentData) {
                            System.out.println(newTT);
                            tokenQueue.add(newTT);
                            wm.accept(newTT);
                        }
                    }
                    
                    // Propagate new triples
                    while(tokenQueue.size() > 0) {
                        manager.processNextTripleToken();
                    }
                    conflictSet.clear();
                    manager.flushBetaMemoryDataToConflicSet();
                    ++step;
                }
            } else if (command.equals("result-set")) {
                System.out.println("ResultSet:\n" + wm);
            }
            
            else if (command.equals("export-result")) {
            	String file = in.next();
            	PrintWriter writer = new PrintWriter(file, "UTF-8");
            	writer.println(wm);
            	writer.close();
            }
            
            else if (command.equals("debugger")) {
                int networkId = in.nextInt();
                List<ReteNetwork> networks = manager.getSpinReteNetworks();
                int size = networks.size();
                if(networkId >= size) {
                    System.out.println("Invalid SpinRule! Max SpinRule Id: " + (size-1));
                } else {
                    DMain.dMain(manager.getSpinReteNetworks().get(networkId));
                }         
            } else {
                System.out.println("Illegal command! Type 'help' for guide.");
            }
        }
    }

    private static boolean valid(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return false; // return failure
        } else {
            // test to see that we can create a file with file name
            File file = new File(fileName);
            if (file.exists() == false) {
                System.err.println("The file name \"" + fileName + "\" is not found.");
                System.exit(1);
            }
            return true;
        }
    }

    private static String userGuide() {
        StringBuilder guide = new StringBuilder("Commands:\n");
        guide.append("    help : User guide.\n");
        guide.append("    exit : Terminate the program.\n");
        guide.append("    tq-size : (debugMode) Get the number of tokens in the TokenQueue.\n");
        guide.append("    tq-print : (debugMode) Print all tokens in the TokenQueue.\n");
        guide.append("    cs-size : Get the number of elements in the ConflictSet.\n");
        guide.append("    cs-print : Print all elements in the ConflictSet.\n");
        guide.append("    nt-print : (debugMode) Peek the next token in the TokenQueue.\n");
        guide.append("    nt-process : (debugMode) Propagate the next token through the Network.\n");
        guide.append("    nt-quick-process <num_tokens> : (debugMode) Propagate k tokens at once.\n");
        guide.append("    last-mem : (debugMode) Print the last memory of the ReteNetwork.\n");
        guide.append("    nr-print : Peek the next rule-element in the ConflictSet.\n");
        guide.append("    nr-fire : (debugMode) Fire the next rule, obtaining result.\n");
        guide.append("    nr-quick-fire <num_rules> : (debugMode) Fire k rules at once.\n");
        guide.append("    run <num_steps> : Execute all to completion. Specify max number of steps.\n");
        guide.append("    result-set : Get the current ResultSet.\n");
        guide.append("    export-result <file> : Write the ResultSet to a file.\n");
        guide.append("    debugger <rete_id> : Start the Diamond Debugger GUI for the k rule.\n");
        return guide.toString();
    }
    
    private void tutorial() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("tutorial.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String next = null;
        while((next = br.readLine()) != null) {
            System.out.println(next);
        } br.close();
    }
}

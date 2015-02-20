package diamond.tests;

import java.io.File;
import java.util.List;

import org.openrdf.rio.RDFFormat;

import diamond.data.Timer;
import diamond.data.TokenQueue;
import diamond.data.TripleToken;
import diamond.managers.SpinRuleManager;
import diamond.processors.FileQueryProcessor;
import diamond.spin.ConflictSet;
import diamond.spin.ConflictSetUnit;
import diamond.spin.WorkingMemory;

/**
 * SpinDiamond Test Suite.
 * @author Slavcho Slavchev
 */
public class SpinTestSuite {

    public static void main(String[] args) {
        SpinTestSuite tm = new SpinTestSuite();
        tm.executeTestSuite(false);
    }

    /**
     * Execute a test of a particular query on a data set.
     */
    private void test(String queryFile, String dataFile, RDFFormat rdfFormat) throws Exception {
        FileQueryProcessor queryProcessor = new FileQueryProcessor(new File(queryFile), true);
        queryProcessor.processSpinRule();
        SpinRuleManager manager = new SpinRuleManager(queryProcessor, new File(dataFile));
        TokenQueue tokenQueue = manager.getTokenQueue();
        ConflictSet conflictSet = manager.getConflictSet();
        
        WorkingMemory wm = new WorkingMemory();
        for (TripleToken token : tokenQueue) {
            wm.accept(token);
        }

        while (tokenQueue.size() > 0) {
            manager.processNextTripleToken();
            manager.flushBetaMemoryDataToConflicSet();
            
            List<TripleToken> consequentData = null;
            while (!conflictSet.isEmpty()) {
                ConflictSetUnit csunit = manager.getNextConflictSetUnit();
                consequentData = csunit.getRuleToFire().evalRule(csunit.getInstantiation());

                for (TripleToken newTT : consequentData) {
                    wm.accept(newTT);
                    tokenQueue.add(newTT);
                }
            }
        }

        System.out.println(wm.toString());

        /* Validate the Result
        if (reteNetwork.getSolutionSet().equals(expectedSolutionSet) == false) {
            System.err.println(queryFile + " Failed\n");
            System.err.println(reteNetwork.getSolutionSet());
            System.err.println("expected solution set ...");
            System.err.println(expectedSolutionSet);
        } else {
            System.out.println(queryFile + " Success!");
        }*/
    }
    
    private void executeTestSuite(boolean b) {
        final String FS = File.separator;
        Timer timer = new Timer();
        timer.start();
    
        // ********TEST-1.1********
        try {
            test("test" + FS + "SpinRules" + FS + "query" + FS + "rules_1.1",
                    "test" + FS + "SpinRules" + FS + "data" + FS + "dataset_1.nt", RDFFormat.NTRIPLES);
            System.out.println();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        // ********TEST-1.2********
        try {
            test("test" + FS + "SpinRules" + FS + "query" + FS + "rules_1.2",
                    "test" + FS + "SpinRules" + FS + "data" + FS + "dataset_1.nt", RDFFormat.NTRIPLES);
            System.out.println();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        timer.stop();
    }
}

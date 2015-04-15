package diamond.managers;

import java.io.File;

import diamond.data.TokenQueue;
import diamond.data.TripleToken;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;
import diamond.retenetwork.ReteNetwork;
import diamond.spin.ConflictSet;
import diamond.spin.ConflictSetUnit;
import java.util.List;
import java.util.Set;

/**
 * @author Slavcho Slavchev
 */
public class SpinRuleManager {

    private final List<ReteNetwork> spinReteNetworks;
    private final TokenQueue tokenQueue;
    private final ConflictSet conflictSet;

    public SpinRuleManager(QueryProcessor queryProcessor, File dataFile) throws Exception {
        this.spinReteNetworks = initializeSpinReteNetworks(queryProcessor);
        this.tokenQueue = new TokenQueue();
        this.tokenQueue.enqueue(dataFile);
        this.conflictSet = new ConflictSet();

        if (spinReteNetworks == null) {
            throw new IllegalArgumentException("SpinReteNetworks is null.");
        }
    }

    /**
     * Initialize the Spin ReteNetworks for the parsed Spin Rules
     */
    private List<ReteNetwork> initializeSpinReteNetworks(QueryProcessor queryProcessor) throws Exception {
        if (queryProcessor != null) {
            // Initialize the spinReteNetworks for the given Spin rules
            List<ReteNetwork> spinReteNetworks = queryProcessor.getSparqlParser().getSpinReteNetworks();
            for (ReteNetwork spinRN : spinReteNetworks) {
                spinRN.createNetwork();
            }
            return spinReteNetworks;
        } else {
            throw new IllegalArgumentException("Query processor is null.");
        }
    }

    /**
     * @return false if all TripleTokens in the tokenQueue have been processed
     */
    public boolean processNextTripleToken() throws Exception {
        TripleToken nextToken = tokenQueue.dequeue();
        if (nextToken == null) {
            return false; // no more tokens
        } else
            for (ReteNetwork spinRN : spinReteNetworks) {
                spinRN.insertTokenIntoNetwork(nextToken);
            }
        return true;
    }

    /**
     * Moves antecedent TripleTokens from BetaMemory to the ConflicSet
     */
    public void flushBetaMemoryDataToConflicSet() {
        for (ReteNetwork spinRN : spinReteNetworks) {
            Set<TripleToken> antecedents = spinRN.getLastMemory().getMemory();
            for (TripleToken tt : antecedents) {
                ConflictSetUnit csu = new ConflictSetUnit(spinRN.getSpinRule(), tt);
                conflictSet.add(csu);
            }
            //antecedents.clear();
        }
    }

    /**
     * Get next ConflictSetUnit based on ConflictResolution strategy
     * 
     * @return null if the ConflictSet is empty
     */
    public ConflictSetUnit getNextConflictSetUnit() {
        return conflictSet.poll();
    }

    /**
     * Getter methods
     */
    public List<ReteNetwork> getSpinReteNetworks() {
        return this.spinReteNetworks;
    }

    public TokenQueue getTokenQueue() {
        return this.tokenQueue;
    }

    public ConflictSet getConflictSet() {
        return this.conflictSet;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("$SpinRuleManager RULES DATA");
            System.exit(0);
        }

        File rules = new File(args[0]);
        File data = new File(args[1]);

        // Compile and Parse Rules
        FileQueryProcessor queryProcessor = new FileQueryProcessor(rules, true);
        queryProcessor.processSpinRule();

        SpinRuleManager manager = new SpinRuleManager(queryProcessor, data);
        TokenQueue tokenQueue = manager.getTokenQueue();
        ConflictSet conflictSet = manager.getConflictSet();

        while (tokenQueue.size() > 0) {
            manager.processNextTripleToken();
            manager.flushBetaMemoryDataToConflicSet();

            List<TripleToken> consequentData = null;
            while (!conflictSet.isEmpty()) {
                ConflictSetUnit csunit = manager.getNextConflictSetUnit();
                System.out.println("\nFire rule for: " + csunit.getInstantiation());
                consequentData = csunit.getRuleToFire().evalRule(csunit.getInstantiation());

                for (TripleToken newTT : consequentData) {
                    System.out.println(newTT);
                    // tokenQueue.add(newTT);
                }
            }
        }

        System.out.println("\nCheckpoint");
    }

	public void printLastReteMemory() {
		for (ReteNetwork spinRN : spinReteNetworks) {
            System.out.println("\n" + spinRN.getLastMemory().getMemory());
        }
	}
}

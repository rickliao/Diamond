package diamond.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.rio.RDFFormat;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.SPO;
import diamond.data.Solution;
import diamond.data.SolutionSet;
import diamond.data.Timer;
import diamond.data.TokenQueue;
import diamond.data.TripleToken;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;
import diamond.retenetwork.ReteNetwork;

/**
 * <code>SparqlTestSuite</code> will be responsible for managing the execution
 * of the Diamond test suite.
 * 
 * @author Rodolfo Kaplan Depena
 */
@SuppressWarnings("unused")
public class SparqlTestSuite {

    private static int success = 0;
    
    public static void main(String[] args) throws Exception {
        SparqlTestSuite tm = new SparqlTestSuite();
        tm.executeTestSuite(false);
    }

    /**
     * Execute a test of a particular query on a data set.
     */
    private void test(String queryFile, String dataFile, RDFFormat rdfFormat, SolutionSet expectedSolutionSet)
            throws Exception {
        // initialize query processor to compile query test file known as test.rq
        QueryProcessor queryProcessor = new FileQueryProcessor(new File(queryFile), true);
        queryProcessor.process();

        // create a storage manager and process data file known as model.nt
        TokenQueue tokenQueue = new TokenQueue();
        tokenQueue.enqueue(new File(dataFile));

        // Create the ReteNetwork
        ReteNetwork reteNetwork = queryProcessor.getSparqlParser().getReteNetwork();
        reteNetwork.createNetwork();

        // Shuffle tokens and insert them in the ReteNetwork
        Collections.shuffle(tokenQueue);
        for (TripleToken tripleToken : tokenQueue) {
            //System.out.println(tripleToken);
            reteNetwork.insertTokenIntoNetwork(tripleToken);
        }

        // Validate the Result
        if (reteNetwork.getSolutionSet().equals(expectedSolutionSet) == false) {
            System.err.println(queryFile + " Failed\n");
            System.err.println(reteNetwork.getSolutionSet());
            System.err.println("expected solution set ...");
            System.err.println(expectedSolutionSet);
        } else {
            System.out.println(queryFile + " Success!");
            ++success;
        }
        System.out.println("Total success: " + success);
    }

    /**
     * Execute test suite
     * 
     * @throws Exception
     */
    public void executeTestSuite(boolean hasTimer) throws Exception {
        final String FS = File.separator;
        Timer timer = new Timer();
        timer.start();

        // ********TEST-0-01********
        List<String> selectListForTest001 = new ArrayList<String>();
        selectListForTest001.add("?x");
        selectListForTest001.add("?y");
        selectListForTest001.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-0-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model0.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest001(selectListForTest001));
        selectListForTest001 = null;// clean-up

        // ********TEST-0-02********
        List<String> selectListForTest002 = new ArrayList<String>();
        selectListForTest002.add("?y");
        selectListForTest002.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-0-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model0.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest002(selectListForTest002));
        selectListForTest002 = null;// clean-up

        // ********TEST-0-03********
        List<String> selectListForTest003 = new ArrayList<String>();
        selectListForTest003.add("?select");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-0-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model0.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest003(selectListForTest003));
        selectListForTest003 = null;// clean-up

        // ********TEST-0-04********
        List<String> selectListForTest004 = new ArrayList<String>();
        selectListForTest004.add("?x");
        selectListForTest004.add("?y");
        selectListForTest004.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-0-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model0.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest004(selectListForTest004));
        selectListForTest004 = null;// clean-up

        // ********TEST-1-01********
        List<String> selectListForTest101 = new ArrayList<String>();
        selectListForTest101.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest101(selectListForTest101));
        selectListForTest101 = null;// clean-up

        // ********TEST-1-02********
        List<String> selectListForTest102 = new ArrayList<String>();
        selectListForTest102.add("?y");
        selectListForTest102.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest102(selectListForTest102));
        selectListForTest102 = null;// clean-up

        // ********TEST-1-03********
        List<String> selectListForTest103 = new ArrayList<String>();
        selectListForTest103.add("?x");
        selectListForTest103.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest103(selectListForTest103));
        selectListForTest103 = null;// clean-up

        // ********TEST-1-04********
        List<String> selectListForTest104 = new ArrayList<String>();
        selectListForTest104.add("?x");
        selectListForTest104.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest104(selectListForTest104));
        selectListForTest104 = null;// clean-up

        // ********TEST-1-05********
        List<String> selectListForTest105 = new ArrayList<String>();
        selectListForTest105.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-05.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest105(selectListForTest105));
        selectListForTest105 = null;// clean-up

        // ********TEST-1-06********
        List<String> selectListForTest106 = new ArrayList<String>();
        selectListForTest106.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-06.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest106(selectListForTest106));
        selectListForTest106 = null;// clean-up

        // ********TEST-1-07********
        List<String> selectListForTest107 = new ArrayList<String>();
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-07.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest107(selectListForTest107));
        selectListForTest107 = null;// clean-up

        // ********TEST-1-08********
        List<String> selectListForTest108 = new ArrayList<String>();
        selectListForTest108.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-08.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest108(selectListForTest108));
        selectListForTest108 = null;// clean-up

        //********TEST-1-09******** ---- ILLEGAL, should give parse exception,
        try {
            test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-09.rq", "test" + FS + "Sparql" + FS + "data" + FS
                    + "model1.nt", RDFFormat.NTRIPLES, null);
        } catch(Exception e) {
            System.out.println("test" + FS + "Sparql" + FS + "query" + FS + "test-1-09.rq" + " Success!");
        }

        // ********TEST-1-10********
        List<String> selectListForTest110 = new ArrayList<String>();
        selectListForTest110.add("?x");
        selectListForTest110.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-1-10.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest110(selectListForTest110));
        selectListForTest110 = null;// clean-up

        // ********TEST-2-01********
        List<String> selectListForTest201 = new ArrayList<String>();
        selectListForTest201.add("?x");
        selectListForTest201.add("?y");
        selectListForTest201.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest201(selectListForTest201));
        selectListForTest201 = null;// clean-up

        // ********TEST-2-02********
        List<String> selectListForTest202 = new ArrayList<String>();
        selectListForTest202.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest202(selectListForTest202));
        selectListForTest202 = null;// clean-up

        // ********TEST-2-03********
        List<String> selectListForTest203 = new ArrayList<String>();
        selectListForTest203.add("?x");
        selectListForTest203.add("?y");
        selectListForTest203.add("?z");
        selectListForTest203.add("?a");
        selectListForTest203.add("?b");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest203(selectListForTest203));
        selectListForTest203 = null;// clean-up

        // ********TEST-2-04********
        List<String> selectListForTest204 = new ArrayList<String>();
        selectListForTest204.add("?x");
        selectListForTest204.add("?y");
        selectListForTest204.add("?z");
        selectListForTest204.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest204(selectListForTest204));
        selectListForTest204 = null;// clean-up

        // ********TEST-2-05********
        List<String> selectListForTest205 = new ArrayList<String>();
        selectListForTest205.add("?x");
        selectListForTest205.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-05.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest205(selectListForTest205));
        selectListForTest205 = null;// clean-up

        // ********TEST-2-06********
        List<String> selectListForTest206 = new ArrayList<String>();
        selectListForTest206.add("?x");
        selectListForTest206.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-06.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest206(selectListForTest206));
        selectListForTest206 = null;// clean-up

        // ********TEST-2-07********
        List<String> selectListForTest207 = new ArrayList<String>();
        selectListForTest207.add("?x");
        selectListForTest207.add("?y");
        selectListForTest207.add("?z1");
        selectListForTest207.add("?z2");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-07.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest207(selectListForTest207));
        selectListForTest207 = null;// clean-up

        // ********TEST-2-08********
        List<String> selectListForTest208 = new ArrayList<String>();
        selectListForTest208.add("?x");
        selectListForTest208.add("?y");
        selectListForTest208.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-08.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest208(selectListForTest208));
        selectListForTest208 = null;// clean-up

        // ********TEST-2-09********
        List<String> selectListForTest209 = new ArrayList<String>();
        selectListForTest209.add("?x");
        selectListForTest209.add("?y");
        selectListForTest209.add("?z");
        selectListForTest209.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-09.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest209(selectListForTest209));
        selectListForTest209 = null;// clean-up

        // ********TEST-2-10********
        List<String> selectListForTest210 = new ArrayList<String>();
        selectListForTest210.add("?x");
        selectListForTest210.add("?y");
        selectListForTest210.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-2-10.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest210(selectListForTest210));
        selectListForTest210 = null;// clean-up

        // ********TEST-3-01********
        List<String> selectListForTest301 = new ArrayList<String>();
        selectListForTest301.add("?x");
        selectListForTest301.add("?y");
        selectListForTest301.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest301(selectListForTest301));
        selectListForTest301 = null;// clean-up

        // ********TEST-3-02********
        List<String> selectListForTest302 = new ArrayList<String>();
        selectListForTest302.add("?x");
        selectListForTest302.add("?y");
        selectListForTest302.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest302(selectListForTest302));
        selectListForTest302 = null;// clean-up

        // ********TEST-3-03********
        List<String> selectListForTest303 = new ArrayList<String>();
        selectListForTest303.add("?x");
        selectListForTest303.add("?y");
        selectListForTest303.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest303(selectListForTest303));
        selectListForTest303 = null;// clean-up

        // ********TEST-3-04********
        List<String> selectListForTest304 = new ArrayList<String>();
        selectListForTest304.add("?x");
        selectListForTest304.add("?y");
        selectListForTest304.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest304(selectListForTest304));
        selectListForTest304 = null;// clean-up

        // ********TEST-3-05********
        List<String> selectListForTest305 = new ArrayList<String>();
        selectListForTest305.add("?x");
        selectListForTest305.add("?y");
        selectListForTest305.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-05.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest305(selectListForTest305));
        selectListForTest305 = null;// clean-up

        // ********TEST-3-06********
        List<String> selectListForTest306 = new ArrayList<String>();
        selectListForTest306.add("?x");
        selectListForTest306.add("?y");
        selectListForTest306.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-06.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest306(selectListForTest306));
        selectListForTest306 = null;// clean-up

        // ********TEST-3-07********
        List<String> selectListForTest307 = new ArrayList<String>();
        selectListForTest307.add("?b");
        selectListForTest307.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-3-07.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model5.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest307(selectListForTest307));
        selectListForTest307 = null;// clean-up

        // ********TEST-4-01********
        List<String> selectListForTest401 = new ArrayList<String>();
        selectListForTest401.add("?a");
        selectListForTest401.add("?b");
        selectListForTest401.add("?z");
        selectListForTest401.add("?c");
        selectListForTest401.add("?d");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model2.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest401(selectListForTest401));
        selectListForTest401 = null;// clean-up

        // ********TEST-4-02********
        List<String> selectListForTest402 = new ArrayList<String>();
        selectListForTest402.add("?a");
        selectListForTest402.add("?z");
        selectListForTest402.add("?d");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model2.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest402(selectListForTest402));
        selectListForTest402 = null;// clean-up

        // ********TEST-4-03********
        List<String> selectListForTest403 = new ArrayList<String>();
        selectListForTest403.add("?a");
        selectListForTest403.add("?b");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model2.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest403(selectListForTest403));
        selectListForTest403 = null;// clean-up

        // ********TEST-4-04********
        List<String> selectListForTest404 = new ArrayList<String>();
        selectListForTest404.add("?a");
        selectListForTest404.add("?b");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model2.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest404(selectListForTest404));
        selectListForTest404 = null;// clean-up

        // ********TEST-4-05********
        List<String> selectListForTest405 = new ArrayList<String>();
        selectListForTest405.add("?y");
        selectListForTest405.add("?a");
        selectListForTest405.add("?b");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-05.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model3.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest405(selectListForTest405));
        selectListForTest405 = null;// clean-up

        // ********TEST-4-06******** -- Will not test because contains blank
        // nodes, which are no longer supported by Diamond.

        // ********TEST-4-07********
        List<String> selectListForTest407 = new ArrayList<String>();
        selectListForTest407.add("?x");
        selectListForTest407.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-4-07.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model3.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest407(selectListForTest407));
        selectListForTest407 = null;// clean-up

        // ********TEST-5-01********
        List<String> selectListForTest501 = new ArrayList<String>();
        selectListForTest501.add("?x");
        selectListForTest501.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-5-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest501(selectListForTest501));
        selectListForTest501 = null;// clean-up

        // ********TEST-5-02********
        List<String> selectListForTest502 = new ArrayList<String>();
        selectListForTest502.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-5-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest502(selectListForTest502));
        selectListForTest502 = null;// clean-up

        // ********TEST-5-03********
        List<String> selectListForTest503 = new ArrayList<String>();
        selectListForTest503.add("?z");
        selectListForTest503.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-5-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest503(selectListForTest503));
        selectListForTest503 = null;// clean-up

        // ********TEST-5-04********
        List<String> selectListForTest504 = new ArrayList<String>();
        selectListForTest504.add("?x");
        selectListForTest504.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-5-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest504(selectListForTest504));
        selectListForTest504 = null;// clean-up

        // ********TEST-6-01********
        List<String> selectListForTest601 = new ArrayList<String>();
        selectListForTest601.add("?x");
        selectListForTest601.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-6-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest601(selectListForTest601));
        selectListForTest601 = null;// clean-up

        // ********TEST-6-02********
        List<String> selectListForTest602 = new ArrayList<String>();
        selectListForTest602.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-6-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest602(selectListForTest602));
        selectListForTest602 = null;// clean-up

        // ********TEST-6-03********
        List<String> selectListForTest603 = new ArrayList<String>();
        selectListForTest603.add("?z");
        selectListForTest603.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-6-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest603(selectListForTest603));
        selectListForTest603 = null;// clean-up

        // ********TEST-6-04********
        List<String> selectListForTest604 = new ArrayList<String>();
        selectListForTest604.add("?x");
        selectListForTest604.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-6-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model4.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest604(selectListForTest604));
        selectListForTest604 = null;// clean-up

        // ********TEST-7-01********
        List<String> selectListForTest701 = new ArrayList<String>();
        selectListForTest701.add("?x");
        selectListForTest701.add("?y");
        selectListForTest701.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-7-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model6.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest701(selectListForTest701));
        selectListForTest701 = null;// clean-up

        // ********TEST-7-02********
        List<String> selectListForTest702 = new ArrayList<String>();
        selectListForTest702.add("?y");
        selectListForTest702.add("?a");
        selectListForTest702.add("?b");
        selectListForTest702.add("?x");
        selectListForTest702.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-7-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model6.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest702(selectListForTest702));
        selectListForTest702 = null;// clean-up

        // ********TEST-7-03********
        List<String> selectListForTest703 = new ArrayList<String>();
        selectListForTest703.add("?x");
        selectListForTest703.add("?y");
        selectListForTest703.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-7-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model7.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest703(selectListForTest703));
        selectListForTest703 = null;// clean-up

        // ********TEST-7-04********
        List<String> selectListForTest704 = new ArrayList<String>();
        selectListForTest704.add("?x");
        selectListForTest704.add("?y");
        selectListForTest704.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-7-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model7.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest704(selectListForTest704));
        selectListForTest704 = null;// clean-up

        // ********TEST-9-01********
        List<String> selectListForTest901 = new ArrayList<String>();
        selectListForTest901.add("?x");
        selectListForTest901.add("?y");
        selectListForTest901.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-9-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model1.nt", RDFFormat.NTRIPLES, expectedSolutionSetForTest901(selectListForTest901));
        selectListForTest901 = null;// clean-up
        
        // ********TEST-B-01********
        List<String> selectListForTestB01 = new ArrayList<String>();
        selectListForTestB01.add("?x");
        selectListForTestB01.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model8.n3", RDFFormat.N3, expectedSolutionSetForTestB01(selectListForTestB01));
        selectListForTestB01 = null;// clean-up
        
        // ********TEST-B-02********
        List<String> selectListForTestB02 = new ArrayList<String>();
        selectListForTestB02.add("?x");
        selectListForTestB02.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model8.n3", RDFFormat.N3, expectedSolutionSetForTestB02(selectListForTestB02));
        selectListForTestB02 = null;// clean-up

        // ********TEST-B-03********
        List<String> selectListForTestB03 = new ArrayList<String>();
        selectListForTestB03.add("?x");
        selectListForTestB03.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model8.n3", RDFFormat.N3, expectedSolutionSetForTestB03(selectListForTestB03));
        selectListForTestB03 = null;// clean-up

        // ********TEST-B-04********
        List<String> selectListForTestB04 = new ArrayList<String>();
        selectListForTestB04.add("?x");
        selectListForTestB04.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-04.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model8.n3", RDFFormat.N3, expectedSolutionSetForTestB04(selectListForTestB04));
        selectListForTestB04 = null;// clean-up

        // ********TEST-B-05********
        List<String> selectListForTestB05 = new ArrayList<String>();
        selectListForTestB05.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-05.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model8.n3", RDFFormat.N3, expectedSolutionSetForTestB05(selectListForTestB05));
        selectListForTestB05 = null;// clean-up

        // ********TEST-B-08********
        List<String> selectListForTestB08 = new ArrayList<String>();
        selectListForTestB08.add("?x");
        selectListForTestB08.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-08.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB08(selectListForTestB08));
        selectListForTestB08 = null;// clean-up

        // ********TEST-B-09********
        List<String> selectListForTestB09 = new ArrayList<String>();
        selectListForTestB09.add("?x");
        selectListForTestB09.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-09.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB09(selectListForTestB09));
        selectListForTestB09 = null;// clean-up

        // ********TEST-B-10********
        List<String> selectListForTestB10 = new ArrayList<String>();
        selectListForTestB10.add("?x");
        selectListForTestB10.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-10.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB10(selectListForTestB10));
        selectListForTestB10 = null;// clean-up
    
        // ********TEST-B-11********
        List<String> selectListForTestB11 = new ArrayList<String>();
        selectListForTestB11.add("?x");
        selectListForTestB11.add("?y");
        selectListForTestB11.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-11.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB11(selectListForTestB11));
        selectListForTestB11 = null;// clean-up 

        // ********TEST-B-12********
        List<String> selectListForTestB12 = new ArrayList<String>();
        selectListForTestB12.add("?x");
        selectListForTestB12.add("?y");
        selectListForTestB12.add("?z");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-12.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB12(selectListForTestB12));
        selectListForTestB12 = null;// clean-up

        // ********TEST-B-13********
        List<String> selectListForTestB13 = new ArrayList<String>();
        selectListForTestB13.add("?x");
        selectListForTestB13.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-13.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB13(selectListForTestB13));
        selectListForTestB13 = null;// clean-up
    
        // ********TEST-B-15********
        List<String> selectListForTestB15 = new ArrayList<String>();
        selectListForTestB15.add("?x");
        selectListForTestB15.add("?y");
        selectListForTestB15.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-15.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB15(selectListForTestB15));
        selectListForTestB15 = null;// clean-up
        
        // ********TEST-B-17********
        List<String> selectListForTestB17 = new ArrayList<String>();
        selectListForTestB17.add("?x");
        selectListForTestB17.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-17.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB17(selectListForTestB17));
        selectListForTestB17 = null;// clean-up

        // ********TEST-B-18********
        List<String> selectListForTestB18 = new ArrayList<String>();
        selectListForTestB18.add("?x");
        selectListForTestB18.add("?y");
        selectListForTestB18.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-18.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB18(selectListForTestB18));
        selectListForTestB18 = null;// clean-up

        // ********TEST-B-19********
        List<String> selectListForTestB19 = new ArrayList<String>();
        selectListForTestB19.add("?x");
        selectListForTestB19.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-19.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB19(selectListForTestB19));
        selectListForTestB19 = null;// clean-up
        
        // ********TEST-B-20********
        List<String> selectListForTestB20 = new ArrayList<String>();
        selectListForTestB20.add("?x");
        selectListForTestB20.add("?y");
        selectListForTestB20.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "test-B-20.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "model9.n3", RDFFormat.N3, expectedSolutionSetForTestB20(selectListForTestB20));
        selectListForTestB20 = null;// clean-up

        // ********DAWG-tp-01********
        List<String> selectListForTestDawgTP01 = new ArrayList<String>();
        selectListForTestDawgTP01.add("?p");
        selectListForTestDawgTP01.add("?q");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-tp-01.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "dawg-tp-01.n3", RDFFormat.N3, expectedSolutionSetForTestDawgTp01(selectListForTestDawgTP01));
        selectListForTestDawgTP01 = null;// clean-up

        // ********DAWG-tp-02********
        List<String> selectListForTestDawgTp02 = new ArrayList<String>();
        selectListForTestDawgTp02.add("?x");
        selectListForTestDawgTp02.add("?q");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-tp-02.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "dawg-tp-02.n3", RDFFormat.N3, expectedSolutionSetForTestDawgTp02(selectListForTestDawgTp02));
        selectListForTestDawgTp02 = null;// clean-up

        // ********DAWG-tp-03********
        List<String> selectListForTestDawgTp03 = new ArrayList<String>();
        selectListForTestDawgTp03.add("?a");
        selectListForTestDawgTp03.add("?b");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-tp-03.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "dawg-tp-03.n3", RDFFormat.N3, expectedSolutionSetForTestDawgTp03(selectListForTestDawgTp03));
        selectListForTestDawgTp03 = null;// clean-up

        // ********DAWG-tp-04******** -- Will not test on data containing blank nodes
        // List<String> selectListForTestDawgTp04 = new ArrayList<String>();
        // selectListForTestDawgTp04.add("?name");
        // test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-tp-04.rq",
        // "test" + FS + "Sparql" + FS + "data" + FS + "dawg-tp-04.n3",
        // RDFFormat.N3, expectedSolutionSetForTestDawgTp04(selectListForTestDawgTp04));
        // selectListForTestDawgTp04 = null;//clean-up

        // ********EX2-1a********
        List<String> selectListForEx21a = new ArrayList<String>();
        selectListForEx21a.add("?title");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex2-1a.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex2-1a.n3", RDFFormat.N3, expectedSolutionSetForEx21a(selectListForEx21a));
        selectListForEx21a = null;// clean-up

        // ********EX2-2a********
        List<String> selectListForEx22a = new ArrayList<String>();
        selectListForEx22a.add("?x");
        selectListForEx22a.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex2-2a.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex2-2a.n3", RDFFormat.N3, expectedSolutionSetForEx22a(selectListForEx22a));
        selectListForEx22a = null;// clean-up

        // ********EX2-3a********
        List<String> selectListForEx23a = new ArrayList<String>();
        selectListForEx23a.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex2-3a.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex2-3a.n3", RDFFormat.N3, expectedSolutionSetForEx23a(selectListForEx23a));
        selectListForEx23a = null;// clean-up

        // ********EX2-4a********
        List<String> selectListForEx24a = new ArrayList<String>();
        selectListForEx24a.add("?name");
        selectListForEx24a.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex2-4a.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex2-4a.n3", RDFFormat.N3, expectedSolutionSetForEx24a(selectListForEx24a));
        selectListForEx24a = null;// clean-up

        // ********Q-OPT-1********
        List<String> selectListForQOpt1 = new ArrayList<String>();
        selectListForQOpt1.add("?name");
        selectListForQOpt1.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-opt-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "q-opt-1.n3", RDFFormat.N3, expectedSolutionSetForQOpt1(selectListForQOpt1));
        selectListForQOpt1 = null;// clean-up

        // ********Q-OPT-2********
        List<String> selectListForQOpt2 = new ArrayList<String>();
        selectListForQOpt2.add("?mbox");
        selectListForQOpt2.add("?name");
        selectListForQOpt2.add("?nick");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-opt-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "q-opt-2.n3", RDFFormat.N3, expectedSolutionSetForQOpt2(selectListForQOpt2));
        selectListForQOpt2 = null;// clean-up

        // ********Dawg-Query-001******** -- Will not test on data containing
        // blank nodes
        List<String> selectListForDawgQuery001 = new ArrayList<String>();
        selectListForDawgQuery001.add("?name");
        selectListForDawgQuery001.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-query-001.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "dawg-query-001.n3", RDFFormat.N3, expectedSolutionSetForDawgQuery001(selectListForDawgQuery001));
        selectListForDawgQuery001 = null;// clean-up

        // ********Dawg-Query-002********
        List<String> selectListForDawgQuery002 = new ArrayList<String>();
        selectListForDawgQuery002.add("?name");
        selectListForDawgQuery002.add("?name2");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-query-002.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "dawg-query-002.n3", RDFFormat.N3, expectedSolutionSetForDawgQuery002(selectListForDawgQuery002));
        selectListForDawgQuery002 = null;// clean-up

        // ********Dawg-Query-003********
        List<String> selectListForDawgQuery003 = new ArrayList<String>();
        selectListForDawgQuery003.add("?name");
        selectListForDawgQuery003.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-query-003.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "dawg-query-003.n3", RDFFormat.N3, expectedSolutionSetForDawgQuery003(selectListForDawgQuery003));
        selectListForDawgQuery003 = null;// clean-up

        // ********Dawg-Query-004********
        List<String> selectListForDawgQuery004 = new ArrayList<String>();
        selectListForDawgQuery004.add("?name");
        test("test" + FS + "Sparql" + FS + "query" + FS + "dawg-query-004.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "dawg-query-004.n3", RDFFormat.N3, expectedSolutionSetForDawgQuery004(selectListForDawgQuery004));
        selectListForDawgQuery004 = null;// clean-up

        // ********Q-SELECT-1********
        List<String> selectListForQSelect1 = new ArrayList<String>();
        selectListForQSelect1.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-select-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "q-select-1.n3", RDFFormat.N3, expectedSolutionSetForQSelect1(selectListForQSelect1));
        selectListForQSelect1 = null;// clean-up

        // ********Q-SELECT-2********
        List<String> selectListForQSelect2 = new ArrayList<String>();
        selectListForQSelect2.add("?x");
        selectListForQSelect2.add("?y");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-select-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "q-select-2.n3", RDFFormat.N3, expectedSolutionSetForQSelect2(selectListForQSelect2));
        selectListForQSelect2 = null;// clean-up

        // ********Q-SELECT-3********
        List<String> selectListForQSelect3 = new ArrayList<String>();
        selectListForQSelect3.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-select-3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "q-select-3.n3", RDFFormat.N3, expectedSolutionSetForQSelect3(selectListForQSelect3));
        selectListForQSelect3 = null;// clean-up
        
        // ********CustomUnion1********
        List<String> selectListForCustomUnion1 = new ArrayList<String>();
        selectListForCustomUnion1.add("?title");
        test("test" + FS + "Sparql" + FS + "query" + FS + "customUnion1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "customUnion1.n3", RDFFormat.N3, expectedSolutionSetForCustomUnion1(selectListForCustomUnion1));
        selectListForCustomUnion1 = null;// clean-up
        
        // ********CustomUnion2********
        List<String> selectListForCustomUnion2 = new ArrayList<String>();
        selectListForCustomUnion2.add("?name");
        selectListForCustomUnion2.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "customUnion2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "customUnion2.n3", RDFFormat.N3, expectedSolutionSetForCustomUnion2(selectListForCustomUnion2));
        selectListForCustomUnion2 = null;// clean-up */

        // -- Will not test because we do not support ORDER BY or ASC statements
        // -- And do not support data containing blank nodes
        // ********QuerySort6********

        // -- Will not test because we do not support ORDER BY or DESC
        // statements
        // -- And do not support data containing blank nodes
        // ********QuerySort5********

        // -- Will not test because we do not support ORDER BY or ASC statements
        // -- And do not support data containing blank nodes
        // ********QuerySort4********

        // -- Will not test because we do not support ORDER BY or ASC statements
        // -- And do not support data containing blank nodes
        // ********QuerySort3********

        // -- Will not test because we do not support ORDER BY or DESC
        // statements
        // -- And do not support data containing blank nodes
        // ********QuerySort2********

        // -- Will not test because we do not support ORDER BY statements
        // -- And do not support data containing blank nodes
        // ********QuerySort1********

        // -- Will not test because we do not support ORDER BY and LIMIT
        // statements
        // -- And do not support data containing blank nodes
        // ********LimitOff1********

        // -- Will not test because we do not support ORDER BY and ASC and
        // OFFSET statements
        // -- And do not support data containing blank nodes
        // ********LimitOff2********

        // -- Will not test because we do not support ORDER BY and ASC and
        // OFFSET and LIMIT statements
        // -- And do not support data containing blank nodes
        // ********LimitOff3********
        
        // ********Bound1********
        List<String> selectListForBound1 = new ArrayList<String>();
        selectListForBound1.add("?a");
        selectListForBound1.add("?c");
        test("test" + FS + "Sparql" + FS + "query" + FS + "bound1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data.n3", RDFFormat.N3, expectedSolutionSetForBound1(selectListForBound1));
        selectListForBound1 = null;// clean-up
        
        // ********EX3********
        List<String> selectListForEx3 = new ArrayList<String>();
        selectListForEx3.add("?title");
        selectListForEx3.add("?price");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex3.n3", RDFFormat.N3, expectedSolutionSetForEx3(selectListForEx3));
        selectListForEx3 = null;// clean-up
        
        // ********ex11.2.3.1_0.rq********
        List<String> selectListForEx112310 = new ArrayList<String>();
        selectListForEx112310.add("?name1");
        selectListForEx112310.add("?name2");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.1_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.1_0.n3", RDFFormat.N3, expectedSolutionSetForEx112310(selectListForEx112310));
        selectListForEx112310 = null;// clean-up

        // ********ex11.2.3.2_1.rq********
        List<String> selectListForEx112321 = new ArrayList<String>();
        selectListForEx112321.add("?name");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.2_1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.2_0.n3", RDFFormat.N3, expectedSolutionSetForEx112321(selectListForEx112321));
        selectListForEx112321 = null;// clean-up

        // ********ex11.2.3.3_0.rq********
        List<String> selectListForEx112330 = new ArrayList<String>();
        selectListForEx112330.add("?name");
        selectListForEx112330.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.3_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.3_0.n3", RDFFormat.N3, expectedSolutionSetForEx112330(selectListForEx112330));
        selectListForEx112330 = null;// clean-up

        // ********ex11.2.3.4_0.rq******** -- Will not support blank nodes and
        // therefore will not support isBlank

        // ********ex11.2.3.5_0.rq******** -- Will not test on data containing
        // blank nodes
        List<String> selectListForEx112350 = new ArrayList<String>();
        selectListForEx112350.add("?name");
        selectListForEx112350.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.5_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.5_0.n3", RDFFormat.N3, expectedSolutionSetForEx112350(selectListForEx112350));
        selectListForEx112350 = null;// clean-up

        // ********ex11.2.3.7_0.rq******** -- Will not test on data containing
        // blank nodes
        List<String> selectListForEx112370 = new ArrayList<String>();
        selectListForEx112370.add("?name");
        selectListForEx112370.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.7_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.7_0.n3", RDFFormat.N3, expectedSolutionSetForEx112370(selectListForEx112370));
        selectListForEx112370 = null;// clean-up
        
        // ********expr-1.rq********
        List<String> selectListForExpr1 = new ArrayList<String>();
        selectListForExpr1.add("?title");
        selectListForExpr1.add("?price");
        test("test" + FS + "Sparql" + FS + "query" + FS + "expr-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-f1.n3", RDFFormat.N3, expectedSolutionSetForExpr1(selectListForExpr1));
        selectListForExpr1 = null;// clean-up
        
        // ********expr-2.rq********
        List<String> selectListForExpr2 = new ArrayList<String>();
        selectListForExpr2.add("?title");
        selectListForExpr2.add("?price");
        test("test" + FS + "Sparql" + FS + "query" + FS + "expr-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-f1.n3", RDFFormat.N3, expectedSolutionSetForExpr2(selectListForExpr2));
        selectListForExpr2 = null;// clean-up

        // ********expr-3.rq********
        List<String> selectListForExpr3 = new ArrayList<String>();
        selectListForExpr3.add("?title");
        selectListForExpr3.add("?price");
        test("test" + FS + "Sparql" + FS + "query" + FS + "expr-3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-f1.n3", RDFFormat.N3, expectedSolutionSetForExpr3(selectListForExpr3));
        selectListForExpr3 = null;// clean-up

        // ******query-bev-1.rq******
        List<String> selectListForQueryBev1 = new ArrayList<String>();
        selectListForQueryBev1.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool1.n3", RDFFormat.N3, expectedSolutionSetForQueryBev1(selectListForQueryBev1));
        selectListForQueryBev1 = null;// clean-up
 
        // ******query-bev-2.rq******
        List<String> selectListForQueryBev2 = new ArrayList<String>();
        selectListForQueryBev2.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool1.n3", RDFFormat.N3, expectedSolutionSetForQueryBev2(selectListForQueryBev2));
        selectListForQueryBev2 = null;// clean-up

        // ******query-bev-3.rq******
        List<String> selectListForQueryBev3 = new ArrayList<String>();
        selectListForQueryBev3.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool1.n3", RDFFormat.N3, expectedSolutionSetForQueryBev3(selectListForQueryBev3));
        selectListForQueryBev3 = null;// clean-up

        // ******query-bev-4.rq******
        List<String> selectListForQueryBev4 = new ArrayList<String>();
        selectListForQueryBev4.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-4.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool1.n3", RDFFormat.N3, expectedSolutionSetForQueryBev4(selectListForQueryBev4));
        selectListForQueryBev4 = null;// clean-up

        // ******query-bev-5.rq******
        List<String> selectListForQueryBev5 = new ArrayList<String>();
        selectListForQueryBev5.add("?a");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-5.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool2.n3", RDFFormat.N3, expectedSolutionSetForQueryBev5(selectListForQueryBev5));
        selectListForQueryBev5 = null;// clean-up

        // ******query-bev-6.rq******
        List<String> selectListForQueryBev6 = new ArrayList<String>();
        selectListForQueryBev6.add("?a");
        selectListForQueryBev6.add("?w");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-bev-6.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-bool2.n3", RDFFormat.N3, expectedSolutionSetForQueryBev6(selectListForQueryBev6));
        selectListForQueryBev6 = null;// clean-up
        
        // ******q-str-1.rq******
        List<String> selectListForQStr1 = new ArrayList<String>();
        selectListForQStr1.add("?x");
        selectListForQStr1.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-str-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1.n3", RDFFormat.N3, expectedSolutionSetForQStr1(selectListForQStr1));
        selectListForQStr1 = null;// clean-up

        // ******q-str-2.rq******
        List<String> selectListForQStr2 = new ArrayList<String>();
        selectListForQStr2.add("?x");
        selectListForQStr2.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-str-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1.n3", RDFFormat.N3, expectedSolutionSetForQStr2(selectListForQStr2));
        selectListForQStr2 = null;// clean-up

        // ******q-str-3.rq******
        List<String> selectListForQStr3 = new ArrayList<String>();
        selectListForQStr3.add("?x");
        selectListForQStr3.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-str-3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1.n3", RDFFormat.N3, expectedSolutionSetForQStr3(selectListForQStr3));
        selectListForQStr3 = null;// clean-up

        // ******q-str-4.rq******
        List<String> selectListForQStr4 = new ArrayList<String>();
        selectListForQStr4.add("?x");
        selectListForQStr4.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-str-4.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1.n3", RDFFormat.N3, expectedSolutionSetForQStr4(selectListForQStr4));
        selectListForQStr4 = null;// clean-up

        // ******regex-query-001.rq******
        List<String> selectListForRegexQuery001 = new ArrayList<String>();
        selectListForRegexQuery001.add("?val");
        test("test" + FS + "Sparql" + FS + "query" + FS + "regex-query-001.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "regex-data-01.n3", RDFFormat.N3,
                expectedSolutionSetForRegexQuery001(selectListForRegexQuery001));
        selectListForRegexQuery001 = null;// clean-up

        // ******regex-query-002.rq******
        List<String> selectListForRegexQuery002 = new ArrayList<String>();
        selectListForRegexQuery002.add("?val");
        test("test" + FS + "Sparql" + FS + "query" + FS + "regex-query-002.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "regex-data-01.n3", RDFFormat.N3,
                expectedSolutionSetForRegexQuery002(selectListForRegexQuery002));
        selectListForRegexQuery002 = null;// clean-up

        // ******regex-query-003.rq******
        List<String> selectListForRegexQuery003 = new ArrayList<String>();
        selectListForRegexQuery003.add("?val");
        test("test" + FS + "Sparql" + FS + "query" + FS + "regex-query-003.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "regex-data-01.n3", RDFFormat.N3,
                expectedSolutionSetForRegexQuery003(selectListForRegexQuery003));
        selectListForRegexQuery003 = null;// clean-up

        // ******ex11.2.3.6_0.rq******
        List<String> selectListForEx112360 = new ArrayList<String>();
        selectListForEx112360.add("?name");
        selectListForEx112360.add("?mbox");
        test("test" + FS + "Sparql" + FS + "query" + FS + "ex11.2.3.6_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "ex11.2.3.6_0.n3", RDFFormat.N3, expectedSolutionSetForEx112360(selectListForEx112360));
        selectListForEx112360 = null;// clean-up

        // ******q-datatype-1.rq******
        List<String> selectListForQDataType1 = new ArrayList<String>();
        selectListForQDataType1.add("?x");
        selectListForQDataType1.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-datatype-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1_changed.n3", RDFFormat.N3, expectedSolutionSetForQDataType1(selectListForQDataType1));
        selectListForQDataType1 = null;// clean-up
        
        //******ex11_0.rq****** //date time comparisons not supported
        // List<String> selectListForEx110 = new ArrayList<String>();
        // selectListForEx110.add("?annot"); test("test" + FS + "Sparql" + FS +
        // "query" + FS + "ex11_0.rq", "test" + FS + "Sparql" + FS + "data" + FS
        // + "ex11_0.n3", RDFFormat.N3, expectedSolutionSetForEx110(selectListForEx110));
        // selectListForEx110 = null;//clean-up

        // ******ex11_1.rq****** //date time comparisons not supported

        // ******ex11.2.3.1_1.rq****** //date time comparisons not supported

        // ******ex11.2.3.2_0.rq****** //date time comparisons not supported

        // ******q-uri-1.rq******
        List<String> selectListForQUri1 = new ArrayList<String>();
        selectListForQUri1.add("?x");
        selectListForQUri1.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-uri-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-fil1.n3", RDFFormat.N3, expectedSolutionSetForQUri1(selectListForQUri1));
        selectListForQUri1 = null;// clean-up

        // ******q-langMatches-1.rq******
        List<String> selectListForQLangMatches1 = new ArrayList<String>();
        selectListForQLangMatches1.add("?p");
        selectListForQLangMatches1.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-langMatches-1.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-langMatches.n3", RDFFormat.N3, expectedSolutionSetForQLangMatches1(selectListForQLangMatches1));
        selectListForQLangMatches1 = null;// clean-up

        // ******q-langMatches-2.rq******
        List<String> selectListForQLangMatches2 = new ArrayList<String>();
        selectListForQLangMatches2.add("?p");
        selectListForQLangMatches2.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-langMatches-2.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-langMatches.n3", RDFFormat.N3, expectedSolutionSetForQLangMatches2(selectListForQLangMatches2));
        selectListForQLangMatches2 = null;// clean-up

        // ******q-langMatches-3.rq******
        List<String> selectListForQLangMatches3 = new ArrayList<String>();
        selectListForQLangMatches3.add("?p");
        selectListForQLangMatches3.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-langMatches-3.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-langMatches.n3", RDFFormat.N3, expectedSolutionSetForQLangMatches3(selectListForQLangMatches3));
        selectListForQLangMatches3 = null;// clean-up

        // ******q-langMatches-4.rq******
        List<String> selectListForQLangMatches4 = new ArrayList<String>();
        selectListForQLangMatches4.add("?p");
        selectListForQLangMatches4.add("?v");
        test("test" + FS + "Sparql" + FS + "query" + FS + "q-langMatches-4.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-langMatches.n3", RDFFormat.N3, expectedSolutionSetForQLangMatches4(selectListForQLangMatches4));
        selectListForQLangMatches4 = null;// clean-up

        // ******query-eq-1.rq******
        List<String> selectListForQueryEq1 = new ArrayList<String>();
        selectListForQueryEq1.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-1.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEq1(selectListForQueryEq1));
        selectListForQueryEq1 = null;// clean-up

        // ******query-eq-2.rq******
        List<String> selectListForQueryEq2 = new ArrayList<String>();
        selectListForQueryEq2.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-2.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEq2(selectListForQueryEq2));
        selectListForQueryEq2 = null;// clean-up

        // ******query-eq-3.rq******
        List<String> selectListForQueryEq3 = new ArrayList<String>();
        selectListForQueryEq3.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-3.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEq3(selectListForQueryEq3));
        selectListForQueryEq3 = null;// clean-up

        // ******query-eq-4.rq******
        List<String> selectListForQueryEq4 = new ArrayList<String>();
        selectListForQueryEq4.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-4.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEq4(selectListForQueryEq4));
        selectListForQueryEq4 = null;// clean-up
        
        // ******query-eq-5.rq******
        List<String> selectListForQueryEq5 = new ArrayList<String>();
        selectListForQueryEq5.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-5.rq", "test" + FS + "Sparql" + FS + "data" + FS
                + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEq5(selectListForQueryEq5));
        selectListForQueryEq5 = null;// clean-up

        // ******query-eq-graph-1.rq******
        List<String> selectListForQueryEqGraph1 = new ArrayList<String>();
        selectListForQueryEqGraph1.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-graph-1.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEqGraph1(selectListForQueryEqGraph1));
        selectListForQueryEqGraph1 = null;// clean-up

        // ******query-eq-graph-2.rq******
        List<String> selectListForQueryEqGraph2 = new ArrayList<String>();
        selectListForQueryEqGraph2.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-graph-2.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEqGraph2(selectListForQueryEqGraph2));
        selectListForQueryEqGraph2 = null;// clean-up

        // ******query-eq-graph-3.rq******
        List<String> selectListForQueryEqGraph3 = new ArrayList<String>();
        selectListForQueryEqGraph3.add("?x");
        test("test" + FS + "Sparql" + FS + "query" + FS + "query-eq-graph-3.rq", "test" + FS + "Sparql" + FS + "data"
                + FS + "data-eq.n3", RDFFormat.N3, expectedSolutionSetForQueryEqGraph3(selectListForQueryEqGraph3));
        selectListForQueryEqGraph3 = null;// clean-up

        timer.stop();

        if (hasTimer && timer.getStatus() == false) {
            System.out.println(timer.toString());
        }
    }

    private SolutionSet expectedSolutionSetForTest704(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/a");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"r-1-2\"");

        // add elements to solutions
        soln1.addElement("?x", res3);
        soln1.addElement("?y", res1);
        soln1.addElement("?a", res1);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res1);
        soln2.addElement("?a", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest307(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/j1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://never/bag");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"11\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"12\"");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"21\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"22\"");

        // add elements to solutions
        soln1.addElement("?b", res1);
        soln1.addElement("?y", res3);

        soln2.addElement("?b", res1);
        soln2.addElement("?y", res4);

        soln3.addElement("?b", res2);
        soln3.addElement("?y", res5);

        soln4.addElement("?b", res2);
        soln4.addElement("?y", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEqGraph3(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xp2");

        // add elements to solutions
        soln1.addElement("?x", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEqGraph2(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd3");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln2.addElement("?x", res2);
        soln3.addElement("?x", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryEqGraph1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi3");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln2.addElement("?x", res2);
        soln3.addElement("?x", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryEq5(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xu");

        // add elements to solutions
        soln1.addElement("?x", res1);

        // add solutions solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEq4(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res7 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xp1");

        // add elements to solutions
        soln1.addElement("?x", res7);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEq3(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res7 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xp2");

        // add elements to solutions
        soln1.addElement("?x", res7);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEq2(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);
        Solution soln6 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd1");
        Element res5 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd2");
        Element res6 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd3");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln2.addElement("?x", res2);
        soln3.addElement("?x", res3);
        soln4.addElement("?x", res4);
        soln5.addElement("?x", res5);
        soln6.addElement("?x", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);
        solnSet.add(soln6);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryEq1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);
        Solution soln6 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd1");
        Element res5 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd2");
        Element res6 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd3");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln2.addElement("?x", res2);
        soln3.addElement("?x", res3);
        soln4.addElement("?x", res4);
        soln5.addElement("?x", res5);
        soln6.addElement("?x", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);
        solnSet.add(soln6);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQLangMatches4(List<String> selectList) {
        // Initialize Solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p2");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/abc");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p1");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"");

        // add elements to solutions
        soln1.addElement("?p", res1);
        soln1.addElement("?v", res2);
        soln2.addElement("?p", res3);
        soln2.addElement("?v", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQLangMatches3(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p4");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"@en-gb");

        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p3");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"@en");

        Element res5 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p5");
        Element res6 = new Element(SPO.OBJECT, DataType.URL, "\"abc\"@fr");

        // add elements to solutions
        soln1.addElement("?v", res2);
        soln1.addElement("?p", res1);

        soln2.addElement("?v", res4);
        soln2.addElement("?p", res3);

        soln3.addElement("?v", res6);
        soln3.addElement("?p", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQLangMatches2(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p4");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"@en-gb");

        Element res3 = new Element(SPO.PREDICATE, DataType.LITERAL, "http://example.org/#p3");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"@en");

        // add elements to solutions
        soln1.addElement("?v", res2);
        soln1.addElement("?p", res1);

        soln2.addElement("?v", res4);
        soln2.addElement("?p", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQLangMatches1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/#p4");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abc\"@en-gb");

        // add elements to solutions
        soln1.addElement("?v", res2);
        soln1.addElement("?p", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQUri1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#z");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#xu");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#a");
        Element res4 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#xb");

        // add elements to solutions
        soln1.addElement("?v", res1);
        soln1.addElement("?x", res2);
        soln2.addElement("?v", res3);
        soln2.addElement("?x", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    /*
     * private SolutionSet expectedSolutionSetForEx110(List<String> selectList)
     * { //Initialize solutions Solution soln1 = new Solution(selectList);
     * 
     * //elements Element res1 = new Element(SPO.SUBJECT, DataType.URL,
     * "http://www.example.org/a");
     * 
     * //add elements to solutions soln1.addElement("?annot", res1);
     * 
     * //add solutions to solution set SolutionSet solnSet = new
     * SolutionSet(selectList); solnSet.add(soln1);
     * 
     * return solnSet; }
     */

    private SolutionSet expectedSolutionSetForQDataType1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#xd3");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#xd1");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/things#xd2");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"^^<http://www.w3.org/2001/XMLSchema#double>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1.0\"^^<http://www.w3.org/2001/XMLSchema#double>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1.0\"^^<http://www.w3.org/2001/XMLSchema#double>");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?v", res4);

        soln2.addElement("?x", res2);
        soln2.addElement("?v", res5);

        soln3.addElement("?x", res3);
        soln3.addElement("?v", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx112370(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Roberto\"@es");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@work.example");

        // add elements
        soln1.addElement("?mbox", res2);
        soln1.addElement("?name", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx112350(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bob\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"bob@work.example\"");

        // add elements to solutions
        soln1.addElement("?mbox", res2);
        soln1.addElement("?name", res1);

        // add solution to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForEx112330(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@work.example");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln1.addElement("?mbox", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx112321(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");

        // add elements to solution
        soln1.addElement("?name", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx112310(List<String> selectList) {
        // Initialize Solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Ms A.\"");

        soln1.addElement("?name1", res1);
        soln1.addElement("?name2", res2);

        soln2.addElement("?name1", res2);
        soln2.addElement("?name2", res1);

        // solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForDawgQuery004(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@home");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:eve@example.net");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@work");
        Element res8 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@work");
        Element res9 = new Element(SPO.OBJECT, DataType.URL, "mailto:fred@edu");

        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Eve\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bob\"");

        // add elements to solutions
        soln1.addElement("?name", res7);
        soln2.addElement("?name", res5);
        soln3.addElement("?name", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForDawgQuery003(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@home");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:eve@example.net");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@work");
        Element res8 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@work");
        Element res9 = new Element(SPO.OBJECT, DataType.URL, "mailto:fred@edu");

        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Eve\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bob\"");

        soln1.addElement("?name", res7);
        soln1.addElement("?mbox", res8);

        soln2.addElement("?name", res5);
        soln2.addElement("?mbox", res3);

        soln3.addElement("?name", res6);
        soln3.addElement("?mbox", new Element(null, null, ""));

        soln4.addElement("?name", new Element(null, null, ""));
        soln4.addElement("?mbox", res9);

        soln5.addElement("?name", res7);
        soln5.addElement("?mbox", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForDawgQuery002(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Eve\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bob\"");

        // add solutions to element
        soln1.addElement("?name", res6);
        soln1.addElement("?name2", new Element(null, null, ""));
        soln2.addElement("?name", res5);
        soln2.addElement("?name2", res7);
        soln3.addElement("?name", res7);
        soln3.addElement("?name2", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForDawgQuery001(List<String> selectList) {
        // initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@home");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:eve@example.net");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@work");
        Element res8 = new Element(SPO.OBJECT, DataType.URL, "mailto:bob@work");

        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Eve\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bob\"");

        // add elements to solutions
        soln1.addElement("?name", res6);
        soln1.addElement("?mbox", new Element(null, null, ""));

        soln2.addElement("?name", res5);
        soln2.addElement("?mbox", res3);

        soln3.addElement("?name", res7);
        soln3.addElement("?mbox", res1);

        soln4.addElement("?name", res7);
        soln4.addElement("?mbox", res8);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQOpt1(List<String> selectList) {
        // initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:bert@example.net");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:eve@example.net");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@example.net");

        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bert\"");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");

        // add elements to solutions
        soln1.addElement("?name", res4);
        soln1.addElement("?mbox", res1);
        soln2.addElement("?name", res5);
        soln2.addElement("?mbox", res3);
        soln3.addElement("?name", new Element(SPO.OBJECT, DataType.LITERAL, ""));
        soln3.addElement("?mbox", res2);

        // initialize Solution Set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQOpt2(List<String> selectList) {
        // initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:bert@example.net");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:eve@example.net");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "mailto:alice@example.net");

        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Bert\"");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");

        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"WhoMe?\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"DuckSoup\"");

        // add elements to solutions
        soln1.addElement("?name", res4);
        soln1.addElement("?mbox", res1);
        soln1.addElement("?nick", new Element(SPO.OBJECT, DataType.LITERAL, ""));

        soln2.addElement("?name", res5);
        soln2.addElement("?mbox", res3);
        soln2.addElement("?nick", res6);

        soln3.addElement("?name", new Element(SPO.OBJECT, DataType.LITERAL, ""));
        soln3.addElement("?mbox", res2);
        soln3.addElement("?nick", res7);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForCustomUnion1(List<String> selectList) {
        // initialize solution set
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);
        Solution soln6 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"SPARQL Query Language Tutorial\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"SPARQL\"");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"SPARQL Protocol Tutorial\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"SPARQL (updated)\"");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Test\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Test (updated)\"");

        // elements
        soln1.addElement("?title", res1);
        soln2.addElement("?title", res2);
        soln3.addElement("?title", res3);
        soln4.addElement("?title", res4);
        soln5.addElement("?title", res5);
        soln6.addElement("?title", res6);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);
        solnSet.add(soln6);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForCustomUnion2(List<String> selectList) {
        // initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "mailto:alice@work.example");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "mailto:bert@work.example");

        soln1.addElement("?name", res1);
        soln1.addElement("?mbox", res2);
        soln2.addElement("?name", new Element(SPO.OBJECT, DataType.LITERAL, ""));
        soln2.addElement("?mbox", res3);

        // solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForTest703(List<String> selectList) {
        // initialize solution set
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/a");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"r-1-2\"");

        // add elements to solution
        soln1.addElement("?x", res3);
        soln1.addElement("?y", res1);
        soln1.addElement("?a", res1);
        soln2.addElement("?x", res1);
        soln2.addElement("?y", res1);
        soln2.addElement("?a", res1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForTest404(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    private SolutionSet expectedSolutionSetForTest407(List<String> selectList) {
        // Initialize Solution Set
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/a");
        Element res6 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/b");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-a-1\"");
        Element res9 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-a-2\"");

        // add elements to solution
        soln1.addElement("?x", res4);
        soln1.addElement("?y", res4);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest405(List<String> selectList) {
        // Initialize Solution Set
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");
        Element res5 = new Element(SPO.OBJECT, DataType.URL, "\"v-a-1\"");
        Element res9 = new Element(SPO.OBJECT, DataType.URL, "\"v-a-2\"");

        // add elements to solution
        soln1.addElement("?y", res2);
        soln1.addElement("?a", res1);
        soln1.addElement("?b", res5);

        soln2.addElement("?y", res2);
        soln2.addElement("?a", res3);
        soln2.addElement("?b", res9);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx112360(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Alice\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "mailto:alice@work.example");

        // add elements to solution
        soln1.addElement("?mbox", res2);
        soln1.addElement("?name", res1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    /**
     * Return expected result set for model0 data.
     * 
     * @param selectList
     * @return expected result set for model0
     */
    private SolutionSet expectedSolutionSetForTest001(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element subj = new Element(SPO.SUBJECT, DataType.URL, "urn:/*not_a_comment*/");
        Element pred = new Element(SPO.PREDICATE, DataType.URL, "http://localhost/p1");
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"RDQL//RDQL\"");

        // add elements to solution
        soln.addElement("?x", subj);
        soln.addElement("?y", pred);
        soln.addElement("?z", obj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for test002.
     * 
     * @param selectList
     * @return expected result set for test002
     */
    private SolutionSet expectedSolutionSetForTest002(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element pred = new Element(SPO.PREDICATE, DataType.URL, "http://localhost/p1");
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"RDQL//RDQL\"");

        // add elements to solution
        soln.addElement("?y", pred);
        soln.addElement("?z", obj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest106(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        // Element pred0 = new Element(SPO.PREDICATE, DataType.URL,
        // "http://rdf.hp.com/p-1");
        Element pred1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");

        // add elements to solution
        soln.addElement("?y", pred1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest107(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest108(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

       //solutionSet.add(solution);

        return solutionSet;
    }

    /*
     * private SolutionSet expectedSolutionSetForTest109(List<String>
     * selectList) { //Initialize solution Solution soln = new
     * Solution(selectList);
     * 
     * //elements //Element pred0 = new Element(SPO.PREDICATE, DataType.URL,
     * "http://rdf.hp.com/p-1"); Element pred1 = new Element(SPO.PREDICATE,
     * DataType.URL, "http://rdf.hp.com/p-2");
     * 
     * //add elements to solution soln.addElement("?y", pred1);
     * 
     * //initialize solution set SolutionSet solnSet = new
     * SolutionSet(selectList); solnSet.add(soln);
     * 
     * return solnSet; }
     */

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest110(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element subj = new Element(null, null, "");
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");

        // add elements to solution
        soln.addElement("?v", obj);
        soln.addElement("?x", subj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest201(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element s2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element p1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element s1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element p2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");

        Element o21 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element o12 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element o11 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element o22 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements to solution 1
        soln1.addElement("?x", s2);
        soln1.addElement("?y", p1);
        soln1.addElement("?z", o21);

        // add elements to solution 2
        soln2.addElement("?x", s1);
        soln2.addElement("?y", p2);
        soln2.addElement("?z", o12);

        // add elements to solution 3
        soln3.addElement("?x", s1);
        soln3.addElement("?y", p1);
        soln3.addElement("?z", o11);

        // add elements to solution 4
        soln4.addElement("?x", s2);
        soln4.addElement("?y", p2);
        soln4.addElement("?z", o22);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln1);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest202(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element o21 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element o12 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element o11 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element o22 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        soln1.addElement("?a", o21);// add elements to solution 1
        soln2.addElement("?a", o12);// add elements to solution 2
        soln3.addElement("?a", o11);// add elements to solution 3
        soln4.addElement("?a", o22);// add elements to solution 4

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest203(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);
        Solution soln6 = new Solution(selectList);
        Solution soln7 = new Solution(selectList);
        Solution soln8 = new Solution(selectList);

        // elements
        Element s1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element s2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element p1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element p2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element o11 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element o12 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element o21 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element o22 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements to solution 1
        soln1.addElement("?x", s2);
        soln1.addElement("?y", p1);
        soln1.addElement("?z", o21);
        soln1.addElement("?a", p1);
        soln1.addElement("?b", o21);

        // add elements to solution 2
        soln2.addElement("?x", s2);
        soln2.addElement("?y", p1);
        soln2.addElement("?z", o21);
        soln2.addElement("?a", p2);
        soln2.addElement("?b", o22);

        // add elements to solution 3
        soln3.addElement("?x", s1);
        soln3.addElement("?y", p2);
        soln3.addElement("?z", o12);
        soln3.addElement("?a", p1);
        soln3.addElement("?b", o11);

        // add elements to solution 4
        soln4.addElement("?x", s1);
        soln4.addElement("?y", p2);
        soln4.addElement("?z", o12);
        soln4.addElement("?a", p2);
        soln4.addElement("?b", o12);

        // add elements to solution 5
        soln5.addElement("?x", s1);
        soln5.addElement("?y", p1);
        soln5.addElement("?z", o11);
        soln5.addElement("?a", p1);
        soln5.addElement("?b", o11);

        // add elements to solution 6
        soln6.addElement("?x", s1);
        soln6.addElement("?y", p1);
        soln6.addElement("?z", o11);
        soln6.addElement("?a", p2);
        soln6.addElement("?b", o12);

        // add elements to solution 7
        soln7.addElement("?x", s2);
        soln7.addElement("?y", p2);
        soln7.addElement("?z", o22);
        soln7.addElement("?a", p1);
        soln7.addElement("?b", o21);

        // add elements to solution 8
        soln8.addElement("?x", s2);
        soln8.addElement("?y", p2);
        soln8.addElement("?z", o22);
        soln8.addElement("?a", p2);
        soln8.addElement("?b", o22);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);
        solnSet.add(soln6);
        solnSet.add(soln7);
        solnSet.add(soln8);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest204(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements to solution 1
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res7);
        soln1.addElement("?a", res3);

        // add elements to solution 2
        soln2.addElement("?x", res1);
        soln2.addElement("?y", res4);
        soln2.addElement("?z", res6);
        soln2.addElement("?a", res4);

        // add elements to solution 3
        soln3.addElement("?x", res1);
        soln3.addElement("?y", res3);
        soln3.addElement("?z", res5);
        soln3.addElement("?a", res3);

        // add elements to solution 4
        soln4.addElement("?x", res2);
        soln4.addElement("?y", res4);
        soln4.addElement("?z", res8);
        soln4.addElement("?a", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest205(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest206(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    private SolutionSet expectedSolutionSetForTest207(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);
        soln1.addElement("?z1", res7);
        soln1.addElement("?z2", res7);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res4);
        soln2.addElement("?z1", res6);
        soln2.addElement("?z2", res6);

        soln3.addElement("?x", res1);
        soln3.addElement("?y", res3);
        soln3.addElement("?z1", res5);
        soln3.addElement("?z2", res5);

        soln4.addElement("?x", res2);
        soln4.addElement("?y", res4);
        soln4.addElement("?z1", res8);
        soln4.addElement("?z2", res8);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest208(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest209(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res7);
        soln1.addElement("?a", res7);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res3);
        soln2.addElement("?z", res5);
        soln2.addElement("?a", res5);

        soln3.addElement("?x", res1);
        soln3.addElement("?y", res4);
        soln3.addElement("?z", res6);
        soln3.addElement("?a", res6);

        soln4.addElement("?x", res2);
        soln4.addElement("?y", res4);
        soln4.addElement("?z", res8);
        soln4.addElement("?a", res8);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest210(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest301(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res4);
        soln1.addElement("?z", res6);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res3);
        soln2.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest302(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res7);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res3);
        soln2.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest303(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res4);
        soln1.addElement("?z", res8);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest304(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest305(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-2\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?z", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest306(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    private SolutionSet expectedSolutionSetForTest401(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);
        Solution soln6 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/s1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/s2");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Value\"");
        Element res9 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res10 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res11 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-1");
        Element res12 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res13 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-1-2");
        Element res14 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-3");
        Element res15 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3a");
        Element res16 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3b");

        // add elements to solution
        soln1.addElement("?a", res1);
        soln1.addElement("?b", res2);
        soln1.addElement("?z", res3);
        soln1.addElement("?c", res4);
        soln1.addElement("?d", res5);

        soln2.addElement("?a", res9);
        soln2.addElement("?b", res2);
        soln2.addElement("?z", res9);
        soln2.addElement("?c", res2);
        soln2.addElement("?d", res9);

        soln3.addElement("?a", res10);
        soln3.addElement("?b", res11);
        soln3.addElement("?z", res12);
        soln3.addElement("?c", res13);
        soln3.addElement("?d", res10);

        soln4.addElement("?a", res12);
        soln4.addElement("?b", res13);
        soln4.addElement("?z", res10);
        soln4.addElement("?c", res11);
        soln4.addElement("?d", res12);

        soln5.addElement("?a", res12);
        soln5.addElement("?b", res13);
        soln5.addElement("?z", res10);
        soln5.addElement("?c", res14);
        soln5.addElement("?d", res15);

        soln6.addElement("?a", res12);
        soln6.addElement("?b", res13);
        soln6.addElement("?z", res10);
        soln6.addElement("?c", res14);
        soln6.addElement("?d", res16);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);
        solnSet.add(soln6);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest402(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/s1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/s2");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Value\"");
        Element res9 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res10 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res11 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-1");
        Element res12 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res13 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-1-2");
        Element res14 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-3");
        Element res15 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3a");
        Element res16 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3b");

        // add elements to solution
        soln1.addElement("?a", res1);
        soln1.addElement("?z", res3);
        soln1.addElement("?d", res5);

        soln2.addElement("?a", res9);
        soln2.addElement("?z", res9);
        soln2.addElement("?d", res9);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest403(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/s1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/s2");
        Element res4 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Value\"");
        Element res9 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res10 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element res11 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-1");
        Element res12 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res13 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-1-2");
        Element res14 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-r-2-3");
        Element res15 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3a");
        Element res16 = new Element(SPO.OBJECT, DataType.URL, "http://rdf.hp.com/r-3b");

        // add elements to solution
        soln1.addElement("?a", res1);
        soln1.addElement("?b", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest501(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"R==P\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?z", res2);

        soln2.addElement("?x", res1);
        soln2.addElement("?z", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForTest502(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"R==P\"");

        // add elements
        soln1.addElement("?x", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest503(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri-r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/uri-p");

        // add elements
        soln1.addElement("?z", res1);
        soln1.addElement("?x", res1);

        soln2.addElement("?z", res2);
        soln2.addElement("?x", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest504(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?z", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest601(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"R==P\"");

        // add elements
        soln1.addElement("?x", res1);
        soln1.addElement("?z", res2);

        soln2.addElement("?x", res1);
        soln2.addElement("?z", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest602(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"R==P\"");

        // add elements
        soln1.addElement("?x", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest603(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri-r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/uri-p");

        // add elements
        soln1.addElement("?z", res1);
        soln1.addElement("?x", res1);

        soln2.addElement("?z", res2);
        soln2.addElement("?x", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest604(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/uri1");

        // add elements
        soln1.addElement("?z", res1);
        soln1.addElement("?x", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest701(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/r2");

        // add elements
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res1);
        soln1.addElement("?a", res2);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res2);
        soln2.addElement("?a", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest702(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/r2");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"r-2-1\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"r-1-2\"");

        // add elements
        soln1.addElement("?y", res2);
        soln1.addElement("?a", res1);
        soln1.addElement("?b", res3);
        soln1.addElement("?x", res1);
        soln1.addElement("?z", res4);

        soln2.addElement("?y", res1);
        soln2.addElement("?a", res2);
        soln2.addElement("?b", res4);
        soln2.addElement("?x", res2);
        soln2.addElement("?z", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTest901(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res2);
        soln1.addElement("?z", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB01(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB02(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB03(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    private SolutionSet expectedSolutionSetForTestB04(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB05(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://www.w3.org/2001/XMLSchema#string>");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");

        // add elements to solutions
        soln1.addElement("?z", res1);
        soln2.addElement("?z", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB08(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");

        // add elements to solutions
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB09(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solutions
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB10(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");

        // add elements to solutions
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB11(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@en");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?z", res1);
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        soln2.addElement("?z", res8);
        soln2.addElement("?x", res2);
        soln2.addElement("?y", res7);

        soln3.addElement("?z", res4);
        soln3.addElement("?x", res2);
        soln3.addElement("?y", res9);

        soln4.addElement("?z", res6);
        soln4.addElement("?x", res2);
        soln4.addElement("?y", res3);

        soln5.addElement("?z", res5);
        soln5.addElement("?x", res2);
        soln5.addElement("?y", res10);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB12(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@en");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p2");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?z", res1);
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solution to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB13(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@en");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p4");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB15(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"5\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p4");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?v", res1);
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB17(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"5\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p7");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?x", res2);
        soln1.addElement("?y", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB18(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"true\"^^<http://www.w3.org/2001/XMLSchema#boolean>");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p6");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?v", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?x", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB19(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"true\"^^<http://www.w3.org/2001/XMLSchema#boolean>");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p5");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?y", res3);
        soln1.addElement("?x", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestB20(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"5.7\"^^<http://www.w3.org/2001/XMLSchema#double>");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r");
        Element res3 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p5");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"value\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"^^<http://rdf.hp.com/ns#someType>");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"@zz");
        Element res7 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p3");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"value\"");
        Element res9 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p8");
        Element res10 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p1");

        // add elements to solution
        soln1.addElement("?v", res1);
        soln1.addElement("?y", res3);
        soln1.addElement("?x", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestDawgTp01(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/data/p");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/data/v1");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/data/v2");

        // add elements to solution
        soln1.addElement("?p", res1);
        soln1.addElement("?q", res2);
        soln2.addElement("?p", res1);
        soln2.addElement("?q", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForTestDawgTp02(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/data/x");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/data/v1");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/data/v2");

        // add elements to solution
        soln1.addElement("?x", res1);
        soln1.addElement("?q", res2);
        soln2.addElement("?x", res1);
        soln2.addElement("?q", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForTestDawgTp03(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/data/x");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://example.org/data/y");

        // add elements to solution
        soln1.addElement("?b", res1);
        soln1.addElement("?a", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForTestDawgTp04(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "Eve");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "Alice");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "Bob");

        // add elements to solution
        soln1.addElement("?name", res1);
        soln2.addElement("?name", res2);
        soln3.addElement("?name", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQSelect1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"");

        // add elements to solution
        soln1.addElement("?x", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQSelect2(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"");

        // add elements to solution
        soln1.addElement("?x", res5);
        soln1.addElement("?y", new Element(null, null, ""));

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQSelect3(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"");

        // add elements to solution
        soln1.addElement("?x", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForEx21a(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"SPARQL Tutorial\"");

        // add elements to solution
        soln1.addElement("?title", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForEx22a(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
        Element res2 = new Element(SPO.PREDICATE, DataType.URL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

        // add elements to solution
        soln1.addElement("?v", res1);
        soln1.addElement("?x", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForEx23a(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:jlow@example.com");

        // add elements
        soln1.addElement("?mbox", res1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForEx24a(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.URL, "mailto:peter@example.org");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "mailto:jlow@example.com");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Peter Goodguy\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Johnny Lee Outlaw\"");

        // add elements
        soln1.addElement("?mbox", res1);
        soln1.addElement("?name", res3);
        soln2.addElement("?mbox", res2);
        soln2.addElement("?name", res4);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForBound1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#a2");
        Element res2 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/ns#c2");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://example.org/ns#f");

        // add elements to solution
        soln1.addElement("?a", res1);
        soln1.addElement("?c", res2);
        soln2.addElement("?a", res2);
        soln2.addElement("?c", res3);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForEx3(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"23\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"The Semantic Web\"");

        // add elements to solution
        soln1.addElement("?price", res1);
        soln1.addElement("?title", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForExpr1(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 3\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 2\"");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 1\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"10\"^^<http://www.w3.org/2001/XMLSchema#integer>");

        // add elements to solutions
        soln1.addElement("?title", res1);
        soln1.addElement("?price", new Element(null, null, ""));
        soln2.addElement("?title", res2);
        soln2.addElement("?price", new Element(null, null, ""));
        soln3.addElement("?title", res3);
        soln3.addElement("?price", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForExpr2(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 1\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"10\"^^<http://www.w3.org/2001/XMLSchema#integer>");

        // add elements to solutions
        soln1.addElement("?title", res3);
        soln1.addElement("?price", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForExpr3(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 1\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"10\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"TITLE 3\"");

        // add elements to solutions
        soln1.addElement("?title", res3);
        soln1.addElement("?price", res4);
        soln2.addElement("?title", res5);
        soln2.addElement("?price", new Element(null, null, ""));

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryBev1(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x4");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln2.addElement("?a", res2);
        soln3.addElement("?a", res3);
        soln4.addElement("?a", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryBev2(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#y1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#y2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#y3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#y4");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln2.addElement("?a", res2);
        soln3.addElement("?a", res3);
        soln4.addElement("?a", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryBev3(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x4");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln2.addElement("?a", res2);
        soln3.addElement("?a", res3);
        soln4.addElement("?a", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryBev4(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x2");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x3");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x4");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln2.addElement("?a", res2);
        soln3.addElement("?a", res3);
        soln4.addElement("?a", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQueryBev5(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x3");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln2.addElement("?a", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;

    }

    private SolutionSet expectedSolutionSetForQueryBev6(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/ns#x2");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL,
                "\"false\"^^<http://www.w3.org/2001/XMLSchema#boolean>");

        // add elements to solutions
        soln1.addElement("?a", res1);
        soln1.addElement("?w", res2);

        // add solutions to solutio nset
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQStr1(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xp2");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xd3");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi2");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi1");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"^^<http://www.w3.org/2001/XMLSchema#double>");
        Element res7 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res8 = new Element(SPO.OBJECT, DataType.LITERAL, "\"1\"");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?v", res5);

        soln2.addElement("?x", res2);
        soln2.addElement("?v", res6);

        soln3.addElement("?x", res3);
        soln3.addElement("?v", res7);

        soln4.addElement("?x", res4);
        soln4.addElement("?v", res8);

        // add solutions to solutio nset
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);

        return solnSet;
    }

    public SolutionSet expectedSolutionSetForQStr2(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xi3");
        Element res5 = new Element(SPO.SUBJECT, DataType.URL, "\"01\"^^<http://www.w3.org/2001/XMLSchema#integer>");

        // add elements to solutions
        soln1.addElement("?x", res3);
        soln1.addElement("?v", res5);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    public SolutionSet expectedSolutionSetForQStr3(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xp1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.org/things#xt1");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"zzz\"");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"zzz\"^^<http://example.org/things#myType>");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?v", res3);

        soln2.addElement("?x", res2);
        soln2.addElement("?v", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    public SolutionSet expectedSolutionSetForQStr4(List<String> selectList) {
        // initialize solution set
        SolutionSet solutionSet = new SolutionSet(selectList);

        // initialize solution to return
        Solution solution = new Solution(selectList);

        // if solution contains null value, replace with empty element
        for (String selectVar : solution.getSolution().keySet()) {
            if (solution.getSolution().get(selectVar) == null) {
                Element emptyElem = new Element(null, null, "");
                solution.addElement(selectVar, emptyElem);
            }
        }

        //solutionSet.add(solution);

        return solutionSet;
    }

    private SolutionSet expectedSolutionSetForRegexQuery001(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"ABCdefGHIjkl\"");

        // add elements to solutions
        soln1.addElement("?val", res1);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForRegexQuery002(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"abcDEFghiJKL\"");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"ABCdefGHIjkl\"");

        // add elements to solutions
        soln1.addElement("?val", res1);
        soln2.addElement("?val", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForRegexQuery003(List<String> selectList) {
        // Initialize solution
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"http://example.com/literal\"");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://example.com/uri");

        // add elements to solutions
        soln1.addElement("?val", res1);
        soln2.addElement("?val", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest105(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");

        // add elements to solution
        soln.addElement("?z", obj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest104(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element subj = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element pred = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");

        // add elements to solution
        soln.addElement("?x", subj);
        soln.addElement("?y", pred);

        // Initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest103(List<String> selectList) {
        // Initialize solution
        Solution soln0 = new Solution(selectList);
        Solution soln1 = new Solution(selectList);

        // elements
        Element subj0 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element obj0 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");
        Element subj1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element obj1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-2-1\"");

        // add elements to solution
        soln0.addElement("?x", subj0);
        soln0.addElement("?z", obj0);
        soln1.addElement("?x", subj1);
        soln1.addElement("?z", obj1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln0);
        solnSet.add(soln1);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest102(List<String> selectList) {
        // Initialize solution
        Solution soln0 = new Solution(selectList);
        Solution soln1 = new Solution(selectList);

        // elements
        Element pred0 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-1");
        Element pred1 = new Element(SPO.PREDICATE, DataType.URL, "http://rdf.hp.com/p-2");
        Element obj1 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-2\"");
        Element obj0 = new Element(SPO.OBJECT, DataType.LITERAL, "\"v-1-1\"");

        // add elements to solution
        soln0.addElement("?y", pred0);
        soln0.addElement("?z", obj0);
        soln1.addElement("?y", pred1);
        soln1.addElement("?z", obj1);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln0);

        return solnSet;
    }

    /**
     * Return expected result set for model1
     * 
     * @param selectList
     * @return expected result set for model1
     */
    private SolutionSet expectedSolutionSetForTest101(List<String> selectList) {
        // Initialize solution
        Solution soln0 = new Solution(selectList);
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);

        // elements
        Element subj0 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element subj1 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-1");
        Element subj2 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");
        Element subj3 = new Element(SPO.SUBJECT, DataType.URL, "http://rdf.hp.com/r-2");

        // add elements to solution
        soln0.addElement("?x", subj0);
        soln1.addElement("?x", subj1);
        soln2.addElement("?x", subj2);
        soln3.addElement("?x", subj3);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln0);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);

        return solnSet;
    }

    /**
     * Return expected result set for model0 data.
     * 
     * @param selectList
     * @return expected result set for model0
     */
    private SolutionSet expectedSolutionSetForTest004(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element subj = new Element(SPO.SUBJECT, DataType.URL, "urn:/*not_a_comment*/");
        Element pred = new Element(SPO.PREDICATE, DataType.URL, "http://localhost/p1");
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"RDQL//RDQL\"");

        // add elements to solution
        soln.addElement("?x", subj);
        soln.addElement("?y", pred);
        soln.addElement("?z", obj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }

    /**
     * Return expected result set for test002.
     * 
     * @param selectList
     * @return expected result set for test002
     */
    private SolutionSet expectedSolutionSetForTest003(List<String> selectList) {
        // Initialize solution
        Solution soln = new Solution(selectList);

        // elements
        Element obj = new Element(SPO.OBJECT, DataType.LITERAL, "\"RDQL//RDQL\"");

        // add elements to solution
        soln.addElement("?select", obj);

        // initialize solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln);

        return solnSet;
    }
}
/*
 * Copyright (c) 2010, Rodolfo Kaplan Depena All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions and use of source code, binary forms, and documentation
 * are for personal and educational use only. 2. Redistributions and use of
 * source code, binary forms, and documentation must not be used for monetary
 * gain and/or for business purposes (PROFIT AND NON-PROFIT) of any sort without
 * the express written permission of Rodolfo Kaplan Depena. 3. Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 4. Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution. 5. All advertising materials mentioning features or
 * use of this software must display the following acknowledgment: This product
 * includes software developed by Rodolfo Kaplan Depena. Any use of this
 * software for monetary gain (or business purposes) of any sort without the
 * express written consent of Rodolfo Kaplan Depena is not allowed. 6. Neither
 * the name of the Rodolfo Kaplan Depena nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY RODOLFO KAPLAN DEPENA (AND CONTRIBUTORS) ''AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL RODOLFO KAPLAN DEPENA (AND
 * CONTRIBUTORS) BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
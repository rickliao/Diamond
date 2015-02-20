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

public class SparqlTests2012 {

    public static void main(String[] args) throws Exception {
        SparqlTests2012 tm = new SparqlTests2012();
        tm.executeTestSuite();
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
        }
    }
    
    private void executeTestSuite() throws Exception {
        final String FS = File.separator;
        final String DIR = "test" + FS + "Sparql-Tests2012" + FS;
        Timer timer = new Timer();
        timer.start();
        
        // ********TEST-1-01********
        List<String> selectListForQuery101 = new ArrayList<String>();
        selectListForQuery101.add("?x");
        test(DIR + "test-1.rq", DIR + "data-1.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery101(selectListForQuery101));
        selectListForQuery101 = null; // clean-up

        // ********TEST-2-01********
        List<String> selectListForQuery201 = new ArrayList<String>();
        selectListForQuery201.add("?x");
        selectListForQuery201.add("?y");
        selectListForQuery201.add("?z");
        test(DIR + "test-2.rq", DIR + "data-2.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery201(selectListForQuery201));
        selectListForQuery201 = null; // clean-up

        // ********TEST-3-01********
        List<String> selectListForQuery301 = new ArrayList<String>();
        selectListForQuery301.add("?x");
        selectListForQuery301.add("?name");
        selectListForQuery301.add("?z");
        test(DIR + "test-3.rq", DIR + "data-3.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery301(selectListForQuery301));
        selectListForQuery301 = null; // clean-up

        // ********TEST-4-01********
        List<String> selectListForQuery401 = new ArrayList<String>();
        selectListForQuery401.add("?name");
        test(DIR + "test-4.rq", DIR + "data-4.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery401(selectListForQuery401));
        selectListForQuery401 = null; // clean-up

        // ********TEST-5-01********
        List<String> selectListForQuery501 = new ArrayList<String>();
        selectListForQuery501.add("?author");
        test(DIR + "test-5.rq", DIR + "data-5.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery501(selectListForQuery501));
        selectListForQuery501 = null; // clean-up

        // ********TEST-6-01********
        List<String> selectListForQuery601 = new ArrayList<String>();
        selectListForQuery601.add("?author");
        test(DIR + "test-6.rq", DIR + "data-6.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery601(selectListForQuery601));
        selectListForQuery601 = null; // clean-up

        // ********TEST-7-01********
        List<String> selectListForQuery701 = new ArrayList<String>();
        selectListForQuery701.add("?name");
        selectListForQuery701.add("?age");
        selectListForQuery701.add("?name2");
        selectListForQuery701.add("?age2");
        test(DIR + "test-7.rq", DIR + "data-7.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery701(selectListForQuery701));
        selectListForQuery701 = null; // clean-up

        // ********TEST-8-01********
        List<String> selectListForQuery801 = new ArrayList<String>();
        selectListForQuery801.add("?name");
        test(DIR + "test-8.rq", DIR + "data-8.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery801(selectListForQuery801));
        selectListForQuery801 = null; // clean-up

        // ********TEST-9-01********
        List<String> selectListForQuery901 = new ArrayList<String>();
        selectListForQuery901.add("?name");
        test(DIR + "test-9.rq", DIR + "data-9.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery901(selectListForQuery901));
        selectListForQuery901 = null; // clean-up

        // ********TEST-10-01********
        List<String> selectListForQuery1001 = new ArrayList<String>();
        selectListForQuery1001.add("?name");
        selectListForQuery1001.add("?profession");
        selectListForQuery1001.add("?order");
        test(DIR + "test-10.rq", DIR + "data-10.nt", RDFFormat.NTRIPLES,
                expectedSolutionSetForQuery1001(selectListForQuery1001));
        selectListForQuery1001 = null; // clean-up

        timer.stop();
        if (!timer.getStatus()) {
            System.out.println(timer.toString());
        }
    }
    
    private SolutionSet expectedSolutionSetForQuery101(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln2.addElement("?x", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery201(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);
        Solution soln5 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Bob");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Dennis");
        Element res5 = new Element(SPO.PREDICATE, DataType.URL, "http://www.example.org/knows");
        Element res6 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res7 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/Bob");
        Element res8 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/Charlotte");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?y", res5);
        soln1.addElement("?z", res7);

        soln2.addElement("?x", res1);
        soln2.addElement("?y", res5);
        soln2.addElement("?z", res8);

        soln3.addElement("?x", res2);
        soln3.addElement("?y", res5);
        soln3.addElement("?z", res6);

        soln4.addElement("?x", res3);
        soln4.addElement("?y", res5);
        soln4.addElement("?z", res7);

        soln5.addElement("?x", res4);
        soln5.addElement("?y", res5);
        soln5.addElement("?z", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        solnSet.add(soln5);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery301(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/r1");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/r2");
        Element res3 = new Element(SPO.OBJECT, DataType.LITERAL, "\"21\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"12\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res5 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res6 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/Bob");

        // add elements to solutions
        soln1.addElement("?x", res1);
        soln1.addElement("?name", res5);
        soln1.addElement("?z", res3);

        soln2.addElement("?x", res2);
        soln2.addElement("?name", res6);
        soln2.addElement("?z", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery401(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln2.addElement("?name", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery501(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");

        // add elements to solutions
        soln1.addElement("?author", res1);
        soln2.addElement("?author", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery601(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Bob");

        // add elements to solutions
        soln1.addElement("?author", res1);
        soln2.addElement("?author", res2);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery701(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"12\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");
        Element res4 = new Element(SPO.OBJECT, DataType.LITERAL, "\"21\"^^<http://www.w3.org/2001/XMLSchema#integer>");
        Element res5 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Eddard");
        Element res6 = new Element(SPO.OBJECT, DataType.LITERAL, "\"22\"^^<http://www.w3.org/2001/XMLSchema#integer>");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln1.addElement("?age", res2);
        soln1.addElement("?name2", res3);
        soln1.addElement("?age2", res4);

        soln2.addElement("?name", res5);
        soln2.addElement("?age", res6);
        soln2.addElement("?name2", new Element(null, null, ""));
        soln2.addElement("?age2", new Element(null, null, ""));

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);

        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery801(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Bob");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Charlotte");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Dennis");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Eddard");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln2.addElement("?name", res2);
        soln3.addElement("?name", res3);
        soln4.addElement("?name", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery901(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);
        Solution soln3 = new Solution(selectList);
        Solution soln4 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Bob");
        Element res3 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Dennis");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Fiona");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln2.addElement("?name", res2);
        soln3.addElement("?name", res3);
        soln4.addElement("?name", res4);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        solnSet.add(soln3);
        solnSet.add(soln4);
        return solnSet;
    }

    private SolutionSet expectedSolutionSetForQuery1001(List<String> selectList) {
        // Initialize solutions
        Solution soln1 = new Solution(selectList);
        Solution soln2 = new Solution(selectList);

        // elements
        Element res1 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Alice");
        Element res2 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Engineering\"");
        Element res3 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/o1");
        Element res4 = new Element(SPO.SUBJECT, DataType.URL, "http://www.example.org/Dennis");
        Element res5 = new Element(SPO.OBJECT, DataType.LITERAL, "\"Developer\"");
        Element res6 = new Element(SPO.OBJECT, DataType.URL, "http://www.example.org/o4");

        // add elements to solutions
        soln1.addElement("?name", res1);
        soln1.addElement("?profession", res2);
        soln1.addElement("?order", res3);
        soln2.addElement("?name", res4);
        soln2.addElement("?profession", res5);
        soln2.addElement("?order", res6);

        // add solutions to solution set
        SolutionSet solnSet = new SolutionSet(selectList);
        solnSet.add(soln1);
        solnSet.add(soln2);
        return solnSet;
    }
}

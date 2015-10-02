package diamond.managers.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.RDFTriple;
import diamond.data.SPO;
import diamond.managers.LinkedDataCache;
import diamond.managers.LinkedDataManager;
import diamond.managers.LinkedDataManagerProv;
import diamond.managers.QueryStats;
import diamond.managers.Query;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;

public class LinkedDataManagerTest {

	String testSol1;
	
	@Before
	public void setUp() throws Exception {
		//File file = new File("testSol.txt");
		File file = new File("src/diamond/managers/test/testSol");
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");

	    try {
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        testSol1 = fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}

	@Test
	public void testLinkedDataManager() {
		//fail("Not yet implemented");
	}

	@Test
	public void testExecuteQueryOnWebOfLinkedData() throws Exception {
        /*File query = new File("test.rq");
        File cacheFile = new File("cache.n3");
        QueryProcessor queryProcessor = new FileQueryProcessor(query, false);
        queryProcessor.process();
        LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
        Integer steps = new Integer(2);
        boolean timer = true;
        boolean verbose = true;
        LinkedDataCache cache = new LinkedDataCache(cacheFile);
        QueryStats r1 = linkedDataManager.executeQueryOnWebOfLinkedData(cache, steps, timer, verbose);
        assertEquals(263, r1.getSolutionSet().size());
        assertEquals(114, r1.getDereferencedURLs());
        assertEquals(15217, r1.getNumTriples());*/
	}
	
	@Test
	public void testGetBlankNodes() throws Exception {
		RDFTriple triple1 = new RDFTriple();
		triple1.setSubject(formElement(SPO.SUBJECT, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/32"));
		triple1.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple1.setObject(formElement(SPO.PREDICATE, "_:node1a0dkond4x1"));
		RDFTriple triple2 = new RDFTriple();
		triple2.setSubject(formElement(SPO.SUBJECT, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/445"));
		triple2.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple2.setObject(formElement(SPO.PREDICATE, "http://test.com"));
		
		List<RDFTriple> list = Arrays.asList(triple1, triple2);
		
		File query = new File("test.rq");
        QueryProcessor queryProcessor = new FileQueryProcessor(query, false);
        queryProcessor.process();
		LinkedDataManagerProv linkedDataManagerProv = new LinkedDataManagerProv(queryProcessor, "select ?x ?y where {<http://dbpedia.org/resource/Austin,_Texas> <http://dbpedia.org/ontology/isPartOf> ?x. ?x <http://www.w3.org/2002/07/owl#sameAs> ?y . }");
		List<List<RDFTriple>> res = linkedDataManagerProv.separateNodes(list);
		List<RDFTriple> notBlank = res.get(0);
		List<RDFTriple> blank = res.get(1);
		assertTrue(notBlank.size() == 1);
		assertTrue(blank.size() == 1);
		assertEquals(notBlank.get(0).getObject().toString(), "http://test.com");
		assertEquals(blank.get(0).getObject().toString(), "_:node1a0dkond4x1");
	}
	
	@Test
	public void testCalculateBlankDifferences() throws Exception {
		RDFTriple triple1 = new RDFTriple();
		triple1.setSubject(formElement(SPO.SUBJECT, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/32"));
		triple1.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple1.setObject(formElement(SPO.PREDICATE, "_:node1a0dkond4x1"));
		RDFTriple triple2 = new RDFTriple();
		triple2.setSubject(formElement(SPO.SUBJECT, "_:node1a0dkond4x1"));
		triple2.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple2.setObject(formElement(SPO.PREDICATE, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/123"));
		RDFTriple triple3 = new RDFTriple();
		triple3.setSubject(formElement(SPO.SUBJECT, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/32"));
		triple3.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple3.setObject(formElement(SPO.PREDICATE, "_:node1a0dkond4x2"));
		RDFTriple triple4 = new RDFTriple();
		triple4.setSubject(formElement(SPO.SUBJECT, "_:node1a0dkond4x3"));
		triple4.setPredicate(formElement(SPO.PREDICATE, "http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList"));
		triple4.setObject(formElement(SPO.PREDICATE, "http://data.semanticweb.org/conference/iswc/2008/paper/poster_demo/85"));
		
		List<RDFTriple> cached = Arrays.asList(triple1, triple2);
		List<RDFTriple> extracted = Arrays.asList(triple3, triple4);
		
		File query = new File("test.rq");
        QueryProcessor queryProcessor = new FileQueryProcessor(query, false);
        queryProcessor.process();
		LinkedDataManagerProv linkedDataManagerProv = new LinkedDataManagerProv(queryProcessor, "select ?x ?y where {<http://dbpedia.org/resource/Austin,_Texas> <http://dbpedia.org/ontology/isPartOf> ?x. ?x <http://www.w3.org/2002/07/owl#sameAs> ?y . }");
		
		List<List<RDFTriple>> res = linkedDataManagerProv.calculateBlankDifference(cached, extracted);
		List<RDFTriple> minus = res.get(0);
		List<RDFTriple> plus = res.get(1);
		assertTrue(minus.size() == 1);
		assertTrue(plus.size() == 1);	
		assertEquals(minus, Arrays.asList(triple2));
		assertEquals(plus, Arrays.asList(triple4));
	}
	
	/**
     * Return an element that is formed from data (which is a String).
     */
    private static Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
	
	private String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
	    String line = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
		try {
		    while((line = reader.readLine()) != null ) {
		        stringBuilder.append( line );
		        stringBuilder.append( ls );
		    }
		} finally {
			reader.close();
		}

	    return stringBuilder.toString();
	}

}

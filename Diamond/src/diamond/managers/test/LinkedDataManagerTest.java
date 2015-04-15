package diamond.managers.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import diamond.managers.LinkedDataCache;
import diamond.managers.LinkedDataManager;
import diamond.managers.QueryStats;
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
		fail("Not yet implemented");
	}

	@Test
	public void testExecuteQueryOnWebOfLinkedData() throws Exception {
        File query = new File("test.rq");
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
        assertEquals(15217, r1.getNumTriples());
	}

}

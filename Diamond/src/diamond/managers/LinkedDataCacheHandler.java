package diamond.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.openrdf.model.impl.BNodeImpl;

import diamond.data.SolutionSet;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;

public class LinkedDataCacheHandler extends AbstractHandler{
	
	private LinkedDataCache cache = null;
	private LinkedDataCacheProv cacheProv = null;
	private File cacheFile = null;
	private RdfGenerator gen = null;

	public LinkedDataCacheHandler(File myCacheFile) {
		try {
			cacheFile = myCacheFile;
			//cache = new LinkedDataCache(cacheFile);
			cacheProv = new LinkedDataCacheProv(cacheFile);
			gen = new RdfGenerator();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if(request.getQueryString().contains("get")) {
			response.setContentType("application/rdf; charset=utf-8");
			
		} else {
			//Parse the request URL
			String[] options = request.getQueryString().split("&");
			int steps = 0;
			boolean timer = false;
			boolean verbose = false;
			QueryProcessor queryProcessor = null;
			String query = null;
			for(String op:options) {
				String[] varValue = op.split("=");
				String variable = varValue[0];
				String value = varValue[1];
				switch(variable) {
					case "query": 
						File qFile = new File(value);
						queryProcessor = new FileQueryProcessor(qFile, false);
						query = readFile(qFile.toString());
						break;
					case "steps":
						steps = Integer.parseInt(value);
						break;
					case "timer":
						timer = Boolean.parseBoolean(value);
						break;
					case "verbose":
						verbose = Boolean.parseBoolean(value);
						break;	
				}
			}
			
			//Process the query
			QueryStats sol = null;
			SolutionSet optimisticRuns = null;
			try {
				queryProcessor.process();
				//LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
				//sol = linkedDataManager.executeQueryOnWebOfLinkedData(cache, steps, timer, verbose);
				LinkedDataManagerProv linkedDataManager = new LinkedDataManagerProv(queryProcessor, query);
				//prepare the rdf files for optimistic execution
				gen.generateNext();
				//execute
				sol = linkedDataManager.executeQueryOnWebOfLinkedData(cacheProv, steps, timer, verbose);
				optimisticRuns = linkedDataManager.runOptimisticExecution(linkedDataManager.getRedereferenceURIs(), cacheProv, timer, verbose);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Set response for client
			response.setContentType("application/java-serialized-object; charset=utf-8");
	        PrintWriter pw = response.getWriter();
	        pw.print(sol.getSolutionSet().toString());
	        if(optimisticRuns != null) {
	        	pw.print(optimisticRuns.toString());
	        }
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
		}
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

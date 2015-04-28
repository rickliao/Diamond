package diamond.managers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;

public class LinkedDataCacheHandler extends AbstractHandler{
	
	private LinkedDataCache cache = null;
	private File cacheFile = null;

	public LinkedDataCacheHandler(File myCacheFile) {
		try {
			cacheFile = myCacheFile;
			cache = new LinkedDataCache(cacheFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//Parse the request URL
		String[] options = request.getQueryString().split("&");
		int steps = 0;
		boolean timer = false;
		boolean verbose = false;
		QueryProcessor queryProcessor = null;
		for(String op:options) {
			String[] varValue = op.split("=");
			String variable = varValue[0];
			String value = varValue[1];
			switch(variable) {
				case "query": 
					File qFile = new File(value);
					queryProcessor = new FileQueryProcessor(qFile, false);
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
		try {
			queryProcessor.process();
			LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
			sol = linkedDataManager.executeQueryOnWebOfLinkedData(cache, steps, timer, verbose);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Set response for client
		response.setContentType("application/java-serialized-object; charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(sol.getSolutionSet().toString());
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}

}

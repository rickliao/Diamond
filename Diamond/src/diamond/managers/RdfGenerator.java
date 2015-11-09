package diamond.managers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RdfGenerator {
	
	private BufferedReader br = null;
	
    public RdfGenerator() {
    	try {
    		br = new BufferedReader(new FileReader("src/diamond/streams/AarhusWeatherData0.stream"));
    	    br.readLine();
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public boolean generateNext() {
    	String line;
    	boolean hasNext = false;
    	try {
			if((line = br.readLine()) != null) {
				hasNext = true;
				String[] data = line.split(",");
				
				String[] files = new String[]{
						"rdf/SampleEventService_Property-7e96ab11-820a-42b9-aab2-b6c4ebf88de2.xml",
						"rdf/SampleEventService_Property-66462839-6a65-4d5a-acc6-ffb627d8dc14.xml",
						"rdf/SampleEventService_Property-5cb2e2d7-8773-4e66-9690-6cfb3591c17b.xml"
				};
				
				// loop through the files need to be updated
				for(int i = 0; i < files.length; i++) {
					// read data
					ArrayList<String> input = new ArrayList<String>();
					BufferedReader in = new BufferedReader(new FileReader(files[i]));
		    	    String inLine;
					while((inLine = in.readLine()) != null) {
						input.add(inLine);
					}
					in.close();
					
					// write data
					PrintWriter writer = new PrintWriter(files[i], "UTF-8");
					for(int j = 0; j < input.size(); j++) {
						if(input.get(j).contains("hasValue")) {
							writer.println("        <sao:hasValue>"+data[i]+"</sao:hasValue>");
						} else {
							writer.println(input.get(j));
						}
					}
					writer.println();
					writer.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return hasNext;
    }
}

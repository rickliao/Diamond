package diamond.managers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
				PrintWriter writer = new PrintWriter("rdf/the-file-name.txt", "UTF-8");
				BufferedReader in = new BufferedReader(new FileReader("rdf/SampleEventService_Property-66462839-6a65-4d5a-acc6-ffb627d8dc14.xml"));
	    	    String inLine;
				while((inLine = in.readLine()) != null) {
					if(inLine.contains("hasValue")) {
						writer.println("        <sao:hasValue>"+data[0]+"</sao:hasValue>");
					} else {
						writer.println(inLine);
					}
				}
				in.close();
				writer.println();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return hasNext;
    }
}

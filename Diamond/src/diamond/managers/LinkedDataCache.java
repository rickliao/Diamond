package diamond.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemStatement;
import org.openrdf.sail.memory.model.MemURI;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.RDFTriple;
import diamond.data.SPO;

@SuppressWarnings("serial")
public class LinkedDataCache extends HashMap<URI, List<RDFTriple>> {
	
	private Repository repository = null;
	private RepositoryConnection connection = null;
	private OutputStream out = null;
	private RDFWriter rdfOut = null;
	
	public LinkedDataCache(File cacheFile) throws Exception {
		super();
		
        try {
            // set up and initialize repository
            repository = new SailRepository(new MemoryStore());
            repository.initialize();
            connection = repository.getConnection();
            out = new FileOutputStream(cacheFile, true);
            rdfOut = Rio.createWriter(RDFFormat.N3, out);
            rdfOut.startRDF();
            boolean urlConnectionEstablished = false;
            
            try {
            	connection.add(cacheFile, null, RDFFormat.N3);
            	urlConnectionEstablished = true;
            } catch (Exception e) {
            	e.printStackTrace();
                try {
                	connection.close();
                } catch(RepositoryException re) {};
            }
            
            if(urlConnectionEstablished) {
                // Extract results in set form
                RepositoryResult<Statement> statements = connection.getStatements(null, null, null, false);
                
                // iterate through triples and set triple token
                while (statements.hasNext()) {
                    Statement statement = statements.next();
                    Element subject = formElement(SPO.SUBJECT, statement.getSubject().toString());
                    Element predicate = formElement(SPO.PREDICATE, statement.getPredicate().toString());
                    Element object = formElement(SPO.OBJECT, statement.getObject().toString());
                    
                    if(subject.getDataType() == DataType.URL) {
                    	addToMap(new URI(subject.getData()), new RDFTriple(subject, predicate, object));
                    }
                }
            }
        } finally {
        	if(connection != null) try {
                connection.close();
            } catch (RepositoryException e) {
            	e.printStackTrace();
            }
            if(repository != null) try {
                repository.shutDown();
            } catch (RepositoryException e) {
            	e.printStackTrace();
            }
        }
	}
	
	public void addToMap(URI uri, RDFTriple rdfTriple) {
		List<RDFTriple> triples = this.get(uri);
		if(triples == null) {
			triples = new LinkedList<RDFTriple>();
		}
		triples.add(rdfTriple);
		this.put(uri, triples);
	}
	
	public void addEmptyToMap(URI uri) {
		this.put(uri, new LinkedList<RDFTriple>());
	}
	
	public void addToCache(URI uri, RDFTriple rdfTriple) {
		//write this statement to cache file
		MemURI subject = new MemURI(this, rdfTriple.getSubject().getData(), "");
		MemURI predicate = new MemURI(this, rdfTriple.getPredicate().getData(), "");
		MemURI object = new MemURI(this, rdfTriple.getObject().getData(), "");
		//since snapshot... random 1 since not using snapshot atm
		MemStatement stmt = new MemStatement(subject, predicate, object, null, 1); 
		try {
			rdfOut.handleStatement(stmt);
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
        try {
			rdfOut.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
        try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
}

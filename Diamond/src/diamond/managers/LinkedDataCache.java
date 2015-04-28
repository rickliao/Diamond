package diamond.managers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.RDFTriple;
import diamond.data.SPO;

public class LinkedDataCache {
	
	private Repository repository = null;
	private RepositoryConnection connection = null;
	private ValueFactory factory = null;
	
	public LinkedDataCache(File cacheFile) throws Exception {
        try {
            // set up and initialize repository
            repository = new SailRepository(new MemoryStore());
            //repository = new SailRepository(new NativeStore(cacheFile));
            repository.initialize();
            connection = repository.getConnection();
            factory = connection.getValueFactory();
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void addToCache(java.net.URI uri, RDFTriple rdfTriple) {
		//check if previously empty
		List<RDFTriple> triples = dereference(uri);
		if(triples != null && triples.size() == 1) {
			RDFTriple prev = triples.get(0);
			if(prev.getPredicate().getData().toString().equals("http://null.null")) {
				//remove filler
				URI context = factory.createURI(uri.toString());
				Resource invalidSubject = factory.createURI("http://null.null");
		    	URI invalidPredicate = factory.createURI("http://null.null");
		    	Value invalidObject = factory.createLiteral("http://null.null");
				try {
					connection.remove(invalidSubject, invalidPredicate, invalidObject, context);
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		//write this statement to cache
		Resource subject = factory.createURI(rdfTriple.getSubject().getData());
		URI predicate = factory.createURI(rdfTriple.getPredicate().getData());
		Literal object = factory.createLiteral(rdfTriple.getObject().getData());
		URI context = factory.createURI(uri.toString());

		try {
			connection.add(subject, predicate, object, context);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} 
	}
	
	public void addEmptyToCache(java.net.URI key) {
		URI context = factory.createURI(key.toString());
		Resource invalidSubject = factory.createURI("http://null.null");
    	URI invalidPredicate = factory.createURI("http://null.null");
    	Value invalidObject = factory.createLiteral("http://null.null");
		try{
			connection.add(invalidSubject, invalidPredicate, invalidObject, context);
		} catch(RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	public List<RDFTriple> dereference(java.net.URI uri) {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		try {
			URI context = factory.createURI(uri.toString());
			RepositoryResult<Statement> rs  = connection.getStatements(null, null, null, false, context);
	      	  
			while (rs.hasNext()) {
                Statement statement = rs.next();
                Element subject = formElement(SPO.SUBJECT, statement.getSubject().toString());
                Element predicate = formElement(SPO.PREDICATE, statement.getPredicate().toString());
                Element object = formElement(SPO.OBJECT, statement.getObject().stringValue());
                if(subject.getDataType() == DataType.URL) {
                	RDFTriple triple = new RDFTriple(subject, predicate, object);
                	triples.add(triple);
                }
            }
         } catch(Exception e) {
        	 e.printStackTrace();
         }
		
		//If no result
		if(triples.isEmpty()) {
			return null;
		}
		return triples;
	}
	
	public long size() {
		try {
			return connection.size();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
	
}

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
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
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
	private URI invalidSubject = null;
	private URI invalidPredicate = null;
	private Literal invalidObject = null;
	
	public LinkedDataCache(File cacheFile) throws Exception {
        try {
            // set up and initialize repository
            repository = new SailRepository(new MemoryStore());
            //repository = new SailRepository(new NativeStore(cacheFile));
            repository.initialize();
            connection = repository.getConnection();
            factory = ValueFactoryImpl.getInstance();
            invalidSubject = factory.createURI("http://null.null");
        	invalidPredicate = factory.createURI("http://null.null");
        	invalidObject = factory.createLiteral("http://null.null");
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void addToCache(java.net.URI uri, RDFTriple rdfTriple) {
		//check if previously empty
		/*List<RDFTriple> triples = dereference(uri);
		if(triples != null && triples.size() == 1) {
			RDFTriple prev = triples.get(0);
			if(prev.getPredicate().getData().equals("http://null.null")) {
				//remove filler
				URI context = factory.createURI(uri.toString());
				try {
					connection.remove(invalidSubject, invalidPredicate, invalidObject, context);
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}*/
		//write this statement to cache
		URI subject = factory.createURI(rdfTriple.getSubject().getData());
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
                Element object = formElement(SPO.OBJECT, statement.getObject().toString());
                
                if(subject.getDataType() == DataType.URL) {
                	RDFTriple triple = new RDFTriple(subject, predicate, object);
                	triples.add(triple);
                }
            }
	      	  
			/*
			String queryString = "SELECT ?x ?p ?y  WHERE { GRAPH <"+uri.toString()+"> { ?x ?p ?y} } ";
	      	  
		  	  TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		
		  	  TupleQueryResult result = tupleQuery.evaluate();
		  	  try {
		  	      // iterate over the result
		  		  while (result.hasNext()) {  
		  			  BindingSet bindingSet = result.next();
		  			  Value sub = bindingSet.getValue("x");
		  			  Value pred = bindingSet.getValue("p");
		  			  Value obj = bindingSet.getValue("y");
		
		  			  RDFTriple triple = new RDFTriple();
		  			  triple.setSubject(formElement(SPO.SUBJECT, sub.toString()));
		  			  triple.setPredicate(formElement(SPO.PREDICATE, pred.toString()));
		  			  triple.setObject(formElement(SPO.OBJECT, obj.toString()));
		  			  triples.add(triple);
		        }
		  	  }
		  	  finally {
		  	      result.close();
		  	  }*/
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

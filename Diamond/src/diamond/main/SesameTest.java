package diamond.main;

import java.io.File;

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
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemURI;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.sail.nativerdf.model.NativeURI;

import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.RDFTriple;
import diamond.data.SPO;

public class SesameTest {
    
	public static void main(String[] args) throws RepositoryException {
		Repository repository = new SailRepository(new MemoryStore(new File("/Diamond/cache")));
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();
        
        try {
        	//connection.add(new File("cache.n3"), null, RDFFormat.N3);
        	connection.clearNamespaces();
        	System.out.println(connection.isEmpty());
        	ValueFactory factory = ValueFactoryImpl.getInstance();
        	URI sub = factory.createURI("http://example.com");
        	URI pred = factory.createURI("http://example.com");
        	URI obj = factory.createURI("http://example.com");
        	URI context = factory.createURI("http://hi.com");
        	connection.add(sub, pred, obj, context);
        } catch (Exception e) {
        	e.printStackTrace();
            try {
            	connection.close();
            } catch(RepositoryException re) {};
        }
        
        RepositoryResult<Statement> statements = connection.getStatements(null, null, null, false);
     // iterate through triples and set triple token
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Element subject = formElement(SPO.SUBJECT, statement.getSubject().toString());
            Element predicate = formElement(SPO.PREDICATE, statement.getPredicate().toString());
            Element object = formElement(SPO.OBJECT, statement.getObject().toString());
            
            if(subject.getDataType() == DataType.URL) {
            	System.out.println(subject+" top");
            }
        }
        
        try {
      	  String queryString = "SELECT ?x ?y FROM <http://hi.com> WHERE { ?x ?p ?y } ";
      	  TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

      	  TupleQueryResult result = tupleQuery.evaluate();
      	  try {
                while (result.hasNext()) {  // iterate over the result
	      			BindingSet bindingSet = result.next();
	      			Value valueOfX = bindingSet.getValue("x");
	      			Value valueOfY = bindingSet.getValue("y");
	
	      			System.out.println(valueOfX+" "+valueOfY);
                }
      	  }
      	  finally {
      	      result.close();
      	  }
         } catch(Exception e) {
        	 System.err.println(e);
         }
        connection.close();
	}
	
	private static Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
}

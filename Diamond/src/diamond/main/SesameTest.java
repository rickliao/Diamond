package diamond.main;

import java.io.File;
import java.util.ArrayList;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.BNodeImpl;
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
		Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        RepositoryConnection connection = repository.getConnection();
        
        Repository repository1 = new SailRepository(new MemoryStore());
        repository1.initialize();
        RepositoryConnection connection1 = repository.getConnection();
        
        try {
        	//connection.add(new File("cache.n3"), null, RDFFormat.N3);
        	ValueFactory factory = ValueFactoryImpl.getInstance();
        	BNodeImpl sub = new BNodeImpl("1234");
        	URI pred = factory.createURI("http://predicate.com");
        	URI obj = factory.createURI("http://object.com");
        	URI context = factory.createURI("http://context.com");
        	connection.add(sub, pred, obj, context);
        	
        	BNodeImpl sub1 = new BNodeImpl("123");
        	URI pred1 = factory.createURI("http://predicate.com");
        	URI obj1 = factory.createURI("http://object.com");
        	URI context1 = factory.createURI("http://context.com");
        	connection.add(sub1, pred1, obj1, context1);
        	
        	connection1.add(sub, pred, obj, context);
        	connection1.add(sub1, pred1, obj1, context1);
        	System.out.println(connection.equals(connection1));
        	
        } catch (Exception e) {
        	e.printStackTrace();
            try {
            	connection.close();
            } catch(RepositoryException re) {};
        }
        
        ValueFactory factory = ValueFactoryImpl.getInstance();
        URI context = factory.createURI("http://context.com");
        URI pred = factory.createURI("http://predicate.com");
    	URI obj = factory.createURI("http://object.com");
        RepositoryResult<Statement> statements = connection.getStatements(null, pred, obj, false, context);
     // iterate through triples and set triple token
        System.out.println(statements.hasNext());
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Element subject = formElement(SPO.SUBJECT, statement.getSubject().toString());
            Element predicate = formElement(SPO.PREDICATE, statement.getPredicate().toString());
            Element object = formElement(SPO.OBJECT, statement.getObject().toString());
            
            //if(subject.getDataType() == DataType.URL) {
            	System.out.println(subject+" "+predicate+" "+object);
            //}
        }
        
        statements = connection1.getStatements(null, pred, obj, false, context);
        // iterate through triples and set triple token
           System.out.println(statements.hasNext());
           while (statements.hasNext()) {
               Statement statement = statements.next();
               Element subject = formElement(SPO.SUBJECT, statement.getSubject().toString());
               Element predicate = formElement(SPO.PREDICATE, statement.getPredicate().toString());
               Element object = formElement(SPO.OBJECT, statement.getObject().toString());
               
               //if(subject.getDataType() == DataType.URL) {
               	System.out.println(subject+" "+predicate+" "+object);
               //}
           }
        /*
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
         }*/
        connection.close();
	}
	
	private static Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
}

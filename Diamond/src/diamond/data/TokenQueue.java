package diamond.data;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.net.URI;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Queue of <code>TripleToken</code>s that implement a FIFO policy
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class TokenQueue implements Iterable<TripleToken> {

    private final LinkedList<TripleToken> tokens;
    
    public TokenQueue() {
        this.tokens = new LinkedList<TripleToken>();
    }
    
    /**
     * Add tokens to the queue using a data file.
     */
    public boolean enqueue(File dataFile) throws Exception {
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        
        RepositoryConnection con = null;
        try {
        	boolean urlConnectionEstablished = false;
        	for(RDFFormat format : new RDFFormat[] {RDFFormat.N3, RDFFormat.NTRIPLES,
        			RDFFormat.RDFXML, RDFFormat.TRIG, RDFFormat.TRIX, RDFFormat.TURTLE}) {
            	if(!urlConnectionEstablished) try {
            		con = repository.getConnection();
                    con.add(dataFile, null, format);
                    urlConnectionEstablished = true;
                } catch (Exception e) {
                	if(con != null) try {
                		con.close();
                	} catch(RepositoryException re) {};
                }
            }

            if(!urlConnectionEstablished) {
                System.err.println("Unable to parse the data file");
                System.exit(1);
            } else {
                RepositoryResult<Statement> statements = con.getStatements(null, null, null, false);
                
                while (statements.hasNext()) {
                    Statement statement = statements.next();
                    String subject = statement.getSubject().toString();
                    String predicate = statement.getPredicate().toString();
                    String object = statement.getObject().toString();
                    addTriple(formElement(SPO.SUBJECT, subject), formElement(SPO.PREDICATE, predicate),
                    		formElement(SPO.OBJECT, object), null);
                }
            }
        } catch (Exception e) {
            System.err.println("Caught exception while processing file; Exception: " + e);
        } finally {
            if(con != null) try {
                con.close();
            } catch (RepositoryException e) {}
            if(repository != null) try {
                repository.shutDown();
            } catch (RepositoryException e) {}
        }
        return true;
    }
    
    public void addTriple(RDFTriple triple, URI origin) {
    	addTriple(triple.getSubject(), triple.getPredicate(), triple.getObject(), origin);
    }
    
    /**
     * Add new properly formatted element to the queue.
     */
    public void addTriple(Element subject, Element predicate, Element object, URI origin) {
        Binding binding = new Binding();
        binding.setRDFTripleSubject(subject);
        binding.setRDFTriplePredicate(predicate);
        binding.setRDFTripleObject(object);
        TripleToken tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
        tripleToken.addTriple(binding);
        tripleToken.urlWhereTripleTokenCameFrom = origin;
        tokens.add(tripleToken);
    }
    
    public void addAll(List<RDFTriple> triples, URI origin) {
    	for(RDFTriple triple : triples) {
    		addTriple(triple, origin);
    	}
    }
    
    /**
     * If the queue is not empty, return the element inserted first.
     */
    public TripleToken dequeue() {
        TripleToken tokenToReturn = null;
        boolean done = false;
        for (int i = 0; i < tokens.size() && !done; ++i) {
            tokenToReturn = tokens.remove(0);
            done = true;
        }
        return tokenToReturn;
    }
    
    @Override
	public Iterator<TripleToken> iterator() {
		return tokens.iterator();
	}
    
    public int size() { return tokens.size(); }
    public boolean isEmpty() { return tokens.isEmpty(); }
    public void shuffle() { Collections.shuffle(tokens); }
    public TripleToken peek() { return tokens.peek(); }
    public void add(TripleToken token) { tokens.add(token); }
    
    /**
     * Return an element that is formed from data (which is a String).
     */
    private Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
    }
}

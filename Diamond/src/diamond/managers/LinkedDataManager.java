package diamond.managers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import diamond.data.Binding;
import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.SPO;
import diamond.data.SolutionSet;
import diamond.data.Timer;
import diamond.data.Timestamp;
import diamond.data.TokenQueue;
import diamond.data.TokenTag;
import diamond.data.TripleToken;
import diamond.processors.QueryProcessor;
import diamond.processors.URLProcessor;
import diamond.retenetwork.ReteNetwork;

/**
 * System Manager controls transactions within different packages of Diamond
 * 
 * @author Slavcho Slavchev
 * @author Rodolfo Kaplan Depena
 */
public class LinkedDataManager {

    private static final int NUM_THREADS = 4;
    
    private final QueryProcessor queryProcessor;
    private final ReteNetwork reteNetwork;
    private final TokenQueue tokenQueue;
    private final URLProcessor urlProcessor;
    private final URLManager urlManager;
    private final ExecutorService executor;

    /**
     * Create System Manager to manage transactions of the entire Diamond system
     * 
     * @param queryProcessor
     * @throws Exception
     */
    public LinkedDataManager(QueryProcessor queryProcessor) throws Exception {
        this.queryProcessor = queryProcessor;
        this.reteNetwork = initializeReteNetwork(this.queryProcessor);
        this.tokenQueue = new TokenQueue();
        this.urlProcessor = new URLProcessor();
        this.urlManager = new URLManager();
        urlManager.add(queryProcessor.getSparqlParser().getURLs());
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
    }

    /**
     * Will traverse the web and print out results as it goes along
     * 
     * @throws Exception
     */
    public void executeQueryOnWebOfLinkedData(Integer steps, boolean hasTimer) throws Exception {
        Timer timer = new Timer();
        boolean done = false;
        int counter = 0, numTriples = 0;
        Timer t2 = new Timer();
        long timeSpentInRete = 0;
        timer.start();
        while (done == false) {
            if(!tokenQueue.isEmpty()) {	
                TripleToken tripleToken = tokenQueue.dequeue();
                t2.start();
                boolean matched = reteNetwork.insertTokenIntoNetwork(tripleToken);
                t2.stop();
                timeSpentInRete += t2.timeInNanoseconds();
                
                if(matched) {
                    Binding binding = tripleToken.getBindings().iterator().next();
                    urlManager.add(urlProcessor.extractURLsFromBinding(binding));
                }
            } else { // <-- token queue is empty
                if(urlManager.urlsStillNeedToBeDereferenced()) {
                    Map<URL, Future<List<TripleToken>>> extractedData = new HashMap<URL, Future<List<TripleToken>>>();
                    
                    for(URL url : urlManager.getAllURLsForDereferencing()) {
                        //System.out.println("Dereference: " + url);
                        Callable<List<TripleToken>> derefURL = new DereferenceURL(url);
                        extractedData.put(url, executor.submit(derefURL));
                        ++counter;
                    }
                    
                    for(Entry<URL, Future<List<TripleToken>>> entry : extractedData.entrySet()) {
                        boolean successfulDereference = false;
                        try {
                            List<TripleToken >extractedTriples = entry.getValue().get();
                            numTriples += extractedTriples.size();
                            tokenQueue.addAll(extractedTriples);
                            successfulDereference = true;
                        } catch (Exception e) {}
                        urlManager.updateStatusOfURLDereferenced(entry.getKey(), successfulDereference);
                    }
                } else {
                    done = true;
                }
            }
        }
        timer.stop();
        
        SolutionSet solutionSet = reteNetwork.getSolutionSet();
        System.out.println("Solution Set ... " + solutionSet.size() + " sols");
        System.out.println(solutionSet.toString());
        System.out.println("Dereferened URLs: " + counter + "; Tripples: " + numTriples);
        System.out.println(timer.toString() +
                " vs Time Spent In Rete " + timeSpentInRete/1000000000.0 + " sec");
    }
    
    /**
     * Extract a rete network from a query processor
     * 
     * @param queryProcessor <code>QueryProcessor</code>
     * @return <code>ReteNetwork</code>
     * @throws <code>Exception</code>
     */
    private ReteNetwork initializeReteNetwork(QueryProcessor queryProcessor) throws Exception {
        if (queryProcessor != null) {
            ReteNetwork reteNetwork = queryProcessor.getSparqlParser().getReteNetwork();
            reteNetwork.createNetwork();
            return reteNetwork;
        } else {
            throw new IllegalArgumentException(
                    "query processor is null, when it shouldn't be because it has to produce rete network.");
        }
    }
    
    /**
     * Callable to Dereference URL and extract all RDF data. 
     * 
     * @author Slavcho Salvchev
     */
    private static class DereferenceURL implements Callable<List<TripleToken>> {

        private final URL url;
        
        public DereferenceURL(URL url) { this.url = url; }
        
        @Override
        public List<TripleToken> call() throws Exception {
            if(url == null) throw new IllegalArgumentException();
            List<TripleToken> result = new LinkedList<TripleToken>();
            Repository repository = null;
            RepositoryConnection connection = null;
            
            try {
                // set up and initialize repository
                repository = new SailRepository(new MemoryStore());
                repository.initialize();

                // set up connection to repository
                connection = repository.getConnection();
            
                URLConnection urlConnection = null;
                InputStream instream = null;
                boolean urlConnectionEstablished = false;
                
                for(RDFFormat rdfFormat : RDFFormat.values()) {
                    if(!urlConnectionEstablished) {
                        try {
                            rdfFormat = RDFFormat.N3;
                            urlConnection = url.openConnection();
                            urlConnection.addRequestProperty("accept", rdfFormat.getDefaultMIMEType());
                            instream = urlConnection.getInputStream();
                            connection.add(instream, url.toString(), rdfFormat);
                            urlConnectionEstablished = true;
                        } catch (Exception e) { // Ignore
                        } finally {
                            if(instream != null) try {
                                instream.close();
                            } catch (IOException e) {}
                            instream = null;
                        }
                    } else break;
                }
                
                if(urlConnectionEstablished) {
                    // Extract results in set form
                    RepositoryResult<Statement> statements = connection.getStatements(null, null, null, false);
                    
                    // iterate through triples and set triple token
                    while (statements.hasNext()) {
                        Statement statement = statements.next();
                        String subject = statement.getSubject().toString();
                        String predicate = statement.getPredicate().toString();
                        String object = statement.getObject().toString();
                        
                        Binding binding = new Binding();
                        binding.setRDFTripleSubject(formElement(SPO.SUBJECT, subject));
                        binding.setRDFTriplePredicate(formElement(SPO.PREDICATE, predicate));
                        binding.setRDFTripleObject(formElement(SPO.OBJECT, object));
                        
                        TripleToken tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
                        tripleToken.addTriple(binding);
                        tripleToken.urlWhereTripleTokenCameFrom = url;
                        result.add(tripleToken);
                    }
                }
            } finally {
                if(connection != null) try {
                    connection.close();
                } catch (RepositoryException e) {}
                if(repository != null) try {
                    repository.shutDown();
                } catch (RepositoryException e) {}
            }
            return result;
        }
        
        /**
         * Return an element that is formed from data (which is a String).
         */
        private Element formElement(SPO spo, String data) {
            return new Element(spo, DataType.determineDataType(data), data);
        }
    }
}
/*
 * Copyright (c) 2010, Rodolfo Kaplan Depena All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions and use of source code, binary forms, and documentation
 * are for personal and educational use only. 2. Redistributions and use of
 * source code, binary forms, and documentation must not be used for monetary
 * gain and/or for business purposes (PROFIT AND NON-PROFIT) of any sort without
 * the express written permission of Rodolfo Kaplan Depena. 3. Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 4. Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution. 5. All advertising materials mentioning features or
 * use of this software must display the following acknowledgment: This product
 * includes software developed by Rodolfo Kaplan Depena. Any use of this
 * software for monetary gain (or business purposes) of any sort without the
 * express written consent of Rodolfo Kaplan Depena is not allowed. 6. Neither
 * the name of the Rodolfo Kaplan Depena nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY RODOLFO KAPLAN DEPENA (AND CONTRIBUTORS) ''AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL RODOLFO KAPLAN DEPENA (AND
 * CONTRIBUTORS) BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
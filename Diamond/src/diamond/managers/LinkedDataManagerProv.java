package diamond.managers;

import java.io.*;
import java.net.URI;
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
import diamond.data.RDFTriple;
import diamond.data.SPO;
import diamond.data.SolutionSet;
import diamond.data.Timer;
import diamond.data.TokenQueue;
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
public class LinkedDataManagerProv {

    private static final int NUM_THREADS = 16;
    
    private final QueryProcessor queryProcessor;
    private final ReteNetwork reteNetwork;
    private final TokenQueue tokenQueue;
    private final URLProcessor urlProcessor;
    private final URLManager urlManager;
    private final ExecutorService executor;
    private String query;

    /**
     * Create System Manager to manage transactions of the entire Diamond system
     * 
     * @param queryProcessor
     * @throws Exception
     */
    public LinkedDataManagerProv(QueryProcessor queryProcessor, String meinQuery) throws Exception {
        this.queryProcessor = queryProcessor;
        this.reteNetwork = initializeReteNetwork(this.queryProcessor);
        this.tokenQueue = new TokenQueue();
        this.urlProcessor = new URLProcessor();
        this.urlManager = new URLManager();
        urlManager.add(queryProcessor.getSparqlParser().getURLs());
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        query = meinQuery;
    }

    /**
     * Will traverse the web and print out results as it goes along
     * 
     * @throws Exception
     */
    public QueryStats executeQueryOnWebOfLinkedData(LinkedDataCacheProv myCache, Integer steps, boolean hasTimer, boolean verbose) throws Exception {
        Timer timer = new Timer();
        boolean done = false;
        int counter = 0, numTriples = 0;
        boolean useCache = (myCache != null);
        LinkedDataCacheProv cache = useCache ? myCache : null;
        
        if(useCache && verbose) System.out.println("Cached data for " + cache.size() + " URLs");
        
        timer.start();
        while (done == false) {
            if(!tokenQueue.isEmpty()) {
            	if(verbose) System.out.println("Inserting " + tokenQueue.size() + " triples into Rete.");
            	int numExtractedURLs = 0;
            	while(!tokenQueue.isEmpty()) {
            		TripleToken tripleToken = tokenQueue.dequeue();
            		boolean matched = reteNetwork.insertTokenIntoNetwork(tripleToken);
            		
            		if(matched) {
            			Binding binding = tripleToken.getBindings().iterator().next();
            			List<URL> extractedURLs = urlProcessor.extractURLsFromBinding(binding);
            			numExtractedURLs += extractedURLs.size();
            			urlManager.add(extractedURLs);
            		}
            	}
            	if(verbose) System.out.println("Done inserting triples! Extracted " + numExtractedURLs + " URLs.");
            } else { // <-- token queue is empty
                if((steps == null || (--steps) >= 0) && urlManager.urlsStillNeedToBeDereferenced()) {
                    Map<URI, Future<List<RDFTriple>>> extractedData = new HashMap<URI, Future<List<RDFTriple>>>();
                    List<URI> cacheHitURLs = new LinkedList<URI>();
                    
                    for(URI url : urlManager.getAllURLsForDereferencing()) {
                    	boolean cacheHit = false;
                    	if(useCache) {
                    		List<RDFTriple> cachedTriples = cache.dereference(url, query);
                    		//If already dereferenced the triple and returned nothing
                    		if(cachedTriples != null && cachedTriples.size() == 1) {
                    			RDFTriple triple = cachedTriples.get(0);
                    			if(triple.getPredicate().getData().equals("http://null.null")) {
                    				if(verbose) System.out.println("Cache hit for uri: " + url);
                    				if(verbose) System.out.println(cachedTriples.size() + " entries enqueued! Queue size: " + tokenQueue.size());
                    				cacheHitURLs.add(url);
                    				cacheHit = true;
                    			}
                    		} else if(cachedTriples != null) {
                    			if(verbose) System.out.println("Cache hit for uri: " + url);
                    			tokenQueue.addAll(cachedTriples, url);
                    			if(verbose) System.out.println(cachedTriples.size() + " entries enqueued! Queue size: " + tokenQueue.size());
                    			cacheHitURLs.add(url);
                    			cacheHit = true;
                    		}
                    	}

                    	if(!cacheHit) {
                    		if(verbose) System.out.println("Dereference: " + url);
                    		Callable<List<RDFTriple>> derefURL = new DereferenceURL(url);
                    		extractedData.put(url, executor.submit(derefURL));
                    		++counter;
                    	}
                    } for(URI cacheHitURL : cacheHitURLs) {
                    	urlManager.updateStatusOfURLDereferenced(cacheHitURL, true);
                    }
                    
                    for(Entry<URI, Future<List<RDFTriple>>> entry : extractedData.entrySet()) {
                        boolean successfulDereference = false;
                        try {
                            if(verbose) System.out.println("Gathering data from URL: " + entry.getKey());
                            List<RDFTriple> extractedTriples = null;
                            
                            try {
                            	extractedTriples = entry.getValue().get();
                            } catch(InterruptedException e) {
                            	System.out.println("Timed-out connecting to URL: " + entry.getKey());
                            }
                            if(extractedTriples == null) {
                            	extractedTriples = new LinkedList<RDFTriple>();
                            }
                            if(extractedTriples.size() == 0) {
                            	//If nothing is dereferenced add filler to repo
                                //Otherwise, program will dereference it again next time
                            	cache.addEmptyToCache(entry.getKey(), query);
                            }
                            
                            numTriples += extractedTriples.size();
                            
                            if(verbose) System.out.println("Extracted " + extractedTriples.size()  + " triples from URL: " + entry.getKey());
                            tokenQueue.addAll(extractedTriples, entry.getKey());
                            //write to cache
                            for(RDFTriple triple:extractedTriples) {
                            	cache.addToCache(entry.getKey(), query, triple);
                            }
                            if(verbose) System.out.println(extractedTriples.size() + " entries enqueued! Queue size: " + tokenQueue.size());
                            successfulDereference = true;
                        } catch (Exception e) {
                        	System.out.println("Caught exception while gathering data for URL: " + entry.getKey() + "\n" + e);
                        	e.printStackTrace();
                        }
                        urlManager.updateStatusOfURLDereferenced(entry.getKey(), successfulDereference);
                    }
                } else {
                    done = true;
                }
            }
        }
        timer.stop();
        
        SolutionSet solutionSet = reteNetwork.getSolutionSet();
        System.out.println("\nSolutions: " + solutionSet.size() + "; Dereferened URLs: " +
        		counter + "; Tripples: " + numTriples);
        if(hasTimer) System.out.println(timer.toString());
        QueryStats result = new QueryStats(solutionSet, counter, numTriples);
        return result;
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
    private static class DereferenceURL implements Callable<List<RDFTriple>> {

        private final URI url;
        private static int DEFAULT_TIMEOUT = 10000;
        
        public DereferenceURL(URI url) { this.url = url; }
        
        @Override
        public List<RDFTriple> call() throws Exception {
            if(url == null) throw new IllegalArgumentException();
            List<RDFTriple> result = new LinkedList<RDFTriple>();
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
                            //rdfFormat = RDFFormat.N3;
                            urlConnection = url.toURL().openConnection();
                            urlConnection.setConnectTimeout(DEFAULT_TIMEOUT);
                            urlConnection.addRequestProperty("accept", rdfFormat.getDefaultMIMEType());
                            instream = urlConnection.getInputStream();
                            connection.add(instream, url.toString(), rdfFormat);
                            urlConnectionEstablished = true;
                        } catch (Exception e) {
                        	System.err.println(e);// Ignore
                        } finally {
                            if(instream != null) try {
                                instream.close();
                            } catch (IOException e) {
                            	System.err.println(e);
                            }
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
                        
                        RDFTriple rdfTriple = new RDFTriple(formElement(SPO.SUBJECT, subject),
                        	formElement(SPO.PREDICATE, predicate), formElement(SPO.OBJECT, object));
                        result.add(rdfTriple);
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
        private static Element formElement(SPO spo, String data) {
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
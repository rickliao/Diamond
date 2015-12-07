package diamond.managers;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private ArrayList<URI> reDereference;
    public String result;

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
        reDereference = new ArrayList<URI>();
        
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
                    		//If already dereferenced the uri and returned one triple
                    		if(cachedTriples != null && cachedTriples.size() == 1) {
                    			if(verbose) System.out.println("Cache hit for uri: " + url);
                				if(verbose) System.out.println(cachedTriples.size() + " entries enqueued! Queue size: " + tokenQueue.size());
                    			RDFTriple triple = cachedTriples.get(0);
                    			//if that triple is not null
                    			if(!triple.getPredicate().getData().equals("http://null.null")) {
                    				tokenQueue.addAll(cachedTriples, url);
                    				//executor.submit(new UpdateCacheAndReteNetwork(reteNetwork, cache, query, url, cachedTriples, executor, verbose));
                    			}
                    			cacheHitURLs.add(url);
                				cacheHit = true;
                				reDereference.add(url);
                				
                    		} else if(cachedTriples != null) {
                    			if(verbose) System.out.println("Cache hit for uri: " + url);
                    			tokenQueue.addAll(cachedTriples, url);
                    			if(verbose) System.out.println(cachedTriples.size() + " entries enqueued! Queue size: " + tokenQueue.size());
                    			cacheHitURLs.add(url);
                    			cacheHit = true;
                    			
                    			//executor.submit(new UpdateCacheAndReteNetwork(reteNetwork, cache, query, url, cachedTriples, executor, verbose));
                    			reDereference.add(url);
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
                            	extractedTriples = entry.getValue().get(1, TimeUnit.MINUTES);
                            } catch(InterruptedException e) {
                            	System.out.println("Interrupted: " + entry.getKey());
                            } catch(TimeoutException e) {
                            	System.out.println("Timed-out connecting to URL: " + entry.getKey());
                            	e.printStackTrace();
                            }
                            if(extractedTriples == null) {
                            	extractedTriples = new LinkedList<RDFTriple>();
                            }
                            if(useCache && extractedTriples.size() == 0) {
                            	//If nothing is dereferenced add filler to repo
                                //Otherwise, program will dereference it again next time
                            	cache.addEmptyToCache(entry.getKey(), query);
                            }
                            
                            numTriples += extractedTriples.size();
                            
                            if(verbose) System.out.println("Extracted " + extractedTriples.size()  + " triples from URL: " + entry.getKey());
                            tokenQueue.addAll(extractedTriples, entry.getKey());
                            
                            //write to cache
                            if(useCache) {
	                            for(RDFTriple triple:extractedTriples) {
	                            	cache.addToCache(entry.getKey(), query, triple);
	                            }
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
        if(verbose) {
        	System.out.println("\nSolutions: " + solutionSet.size() + "; Dereferened URLs: " +
        		counter + "; Tripples: " + numTriples);
        }
        if(hasTimer) System.out.println("Execute: " + timer.toString());
        QueryStats result = new QueryStats(solutionSet, counter, numTriples);
        
        return result;
    }
    
    
    /**
     * Return the list of URIs to be re-dereferenced for optimistic execution
     * reDereference only gets populated after executeQueryOnWebOfLinkedData is run
     * 
     * @return reDereference
     */
    public List<URI> getRedereferenceURIs() {
    	return reDereference;
    }
    
    /**
     * Run optimistic execution. Dereference URIs again, check differences from cache, update cache and rete
     * 
     * @param reDereference list of URIs to dereference again
     * @param cache 
     * @param verbose
     * @throws Exception
     */
    public SolutionSet runOptimisticExecution(List<URI> reDereference, LinkedDataCacheProv cache, boolean hasTimer, boolean verbose) throws Exception {
    	Timer timerOptimistic = new Timer();
    	double optimisticTime = 0;
    	Timer timerEventual = new Timer();
    	timerEventual.start();
    	for(int i = 0; i < reDereference.size(); i++) {
    		// start timing for optimistic part
    		timerOptimistic.start();
    		
        	URI uri = reDereference.get(i);
        	List<RDFTriple> cachedTriples = cache.dereference(uri, query);
        	
	        if (verbose) System.out.println(uri);
			// dereference the uri again to see if there are differences
			Callable<List<RDFTriple>> derefURL = new DereferenceURL(uri);
			Future<List<RDFTriple>> futureExtracted = executor.submit(derefURL);
			List<RDFTriple> extractedTriples = null;
			try {
				extractedTriples = futureExtracted.get();
			} catch(InterruptedException e) {
	        	System.out.println("Interrupted: " + uri);
	        } catch(ExecutionException e) {
	        	e.printStackTrace();
	        }
			
			//Separate blank nodes from normal nodes
			List<List<RDFTriple>> cached = separateNodes(cachedTriples);
			List<RDFTriple> cachedNotBlank = cached.get(0);
			List<RDFTriple> cachedBlank = cached.get(1);
			
			List<List<RDFTriple>> extracted = separateNodes(extractedTriples);
			List<RDFTriple> extractedNotBlank = extracted.get(0);
			List<RDFTriple> extractedBlank = extracted.get(1);
			
			// minus token
			List<RDFTriple> temp = new ArrayList<RDFTriple>(cachedNotBlank);
			cachedNotBlank.removeAll(extractedNotBlank);
			if(verbose) System.out.println("Delete non-blank: " + cachedNotBlank);
			// plus token
			extractedNotBlank.removeAll(temp);
			if(verbose) System.out.println("Add non-blank: " + extractedNotBlank);
			
			//Calculate difference blank triples
			List<List<RDFTriple>> diff = calculateBlankDifference(cachedBlank, extractedBlank);
			if(verbose) System.out.println("Delete blank: " + diff.get(0));
			if(verbose) System.out.println("Add blank: " + diff.get(1));
			
			// Stop timing optimistic after all the difference had been calculated
			timerOptimistic.stop();
			optimisticTime +=timerOptimistic.timeInSeconds();
			
			// update cache
			cache.updateCache(extractedNotBlank, cachedNotBlank, uri, query);
			cache.updateCache(diff.get(1), diff.get(0), uri, query);
			
			// insert into rete non-blank
			for(RDFTriple triple: cachedNotBlank) {
				if(!triple.getPredicate().getData().equals("http://null.null")) {
					reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(false, uri));
				}
			}
			for(RDFTriple triple: extractedNotBlank) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(true, uri));
			}
			
			// insert into rete blank
			for(RDFTriple triple: diff.get(0)) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(false, uri));
			}
			for(RDFTriple triple: diff.get(1)) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(true, uri));
			}
        }
    	timerEventual.stop();
    	if(hasTimer) {
    		System.out.println("Optimistic: " + optimisticTime);
    		System.out.println("Eventual: " + timerEventual.toString());
    	}
    	
    	// If there is something to dereference
    	if(reDereference.size() > 0) {
    		return reteNetwork.getSolutionSet();
    	}
    	return null;
    }
    
    /**
     * Calculate difference of two sets of triples with blank nodes
     * 
     * @param cachedBlank
     * @param extractedBlank
     * @return Index 0: token to be removed, Index 1: token to be inserted
     */
    public List<List<RDFTriple>> calculateBlankDifference(List<RDFTriple> cachedBlank, List<RDFTriple> extractedBlank) {
    	// make blank nodes into subgraphs with key being the blank nodes
    	Map<Element, List<RDFTriple>> cachedSubgraphs = groupNodes(cachedBlank);
    	Map<Element, List<RDFTriple>> extractedSubgraphs = groupNodes(extractedBlank);
    	
    	// Create match between extracted and subgraph
    	Map<Element, Boolean> extractedMatched = new HashMap<Element, Boolean>();
    	List<RDFTriple> minus = new ArrayList<RDFTriple>();
    	List<RDFTriple> plus = new ArrayList<RDFTriple>();
    	
    	// This whole loop: For each subgraph in cached, c, find a subgraph, e, from extracted
    	// such that e has the maximum number of triples that is the same as c 
    	for(Map.Entry<Element, List<RDFTriple>> entry : cachedSubgraphs.entrySet()) {
    		List<RDFTriple> subgraph = entry.getValue();
    		Element maxElement = null;
    		int max = 0;
    		//Remember which triples are the same so we can find difference faster later
    		List<RDFTriple> sameTriplesCache = new ArrayList<RDFTriple>();
			List<RDFTriple> sameTriplesExt = new ArrayList<RDFTriple>();
    		
    		for(Map.Entry<Element, List<RDFTriple>> entryExt : extractedSubgraphs.entrySet()) {
    			//If this entry of extracted has not already being matched
    			if(extractedMatched.get(entryExt.getKey()) == null) {
	    			List<RDFTriple> subgraphExt = entryExt.getValue();
	    			int maxForEntry = 0;
	    			List<RDFTriple> sameTriplesCacheTemp = new ArrayList<RDFTriple>();
	    			List<RDFTriple> sameTriplesExtTemp = new ArrayList<RDFTriple>();
	    			for(RDFTriple triple: subgraph) {
	    				//See if blank is in sub or obj and compare pred and sub or pred and obj, respectively
	    				if(DataType.isBlankNode(triple.getSubject().toString())) {
							Element pred = triple.getPredicate();
			    			Element obj = triple.getObject();
			    			for(RDFTriple tripleExt: subgraphExt) {
			    				if(tripleExt.getPredicate().equals(pred) && tripleExt.getObject().equals(obj)) {
			    					maxForEntry++;
			    					sameTriplesCacheTemp.add(triple);
			    					sameTriplesExtTemp.add(tripleExt);
			    					break;
			    				}
		    				}
						} else {
							Element pred = triple.getPredicate();
			    			Element sub = triple.getSubject();
			    			for(RDFTriple tripleExt: subgraphExt) {
			    				if(tripleExt.getPredicate().equals(pred) && tripleExt.getSubject().equals(sub)) {
			    					maxForEntry++;
			    					sameTriplesCacheTemp.add(triple);
			    					sameTriplesExtTemp.add(tripleExt);
			    					break;
			    				}
		    				}
						}
	    			}
	    			if(maxForEntry > max) {
	    				max = maxForEntry;
	    				maxElement = entryExt.getKey();
	    				sameTriplesCache = sameTriplesCacheTemp;
	    				sameTriplesExt = sameTriplesExtTemp;
	    			}
    			}
    		}
    		
    		//If no match for cached delete everything
    		if(maxElement == null) {
    			minus.addAll(subgraph);
    		} else {
    			subgraph.removeAll(sameTriplesCache);
    			List<RDFTriple> subgraphExt = extractedSubgraphs.get(maxElement);
    			subgraphExt.removeAll(sameTriplesExt);
    			minus.addAll(subgraph);
    			plus.addAll(subgraphExt);
    			//mark this extracted entry as matched
    			extractedMatched.put(maxElement, true);
    		}
    		
    	}
    	
    	//Add left overs to plus
    	for(Map.Entry<Element, List<RDFTriple>> entryExt : extractedSubgraphs.entrySet()) {
    		if(extractedMatched.get(entryExt.getKey()) == null) {
    			plus.addAll(entryExt.getValue());
    		}
    	}
    	
    	return Arrays.asList(minus, plus);
    }
    
    /**
     * Group triples by blank nodes. if they have the same blank node, they are group together.
     * 
     * @param triples list of RDFTriple
     * @return a map from Element containing a blank node to list of triples containing that blank node
     */
    public Map<Element, List<RDFTriple>> groupNodes(List<RDFTriple> triples) {
    	Map<Element, List<RDFTriple>> subgraph = new HashMap<Element, List<RDFTriple>>();
    	for(RDFTriple triple: triples) {
    		//get blank node
    		Element blankName;
    		if(DataType.isBlankNode(triple.getSubject().toString())) {
    			blankName = triple.getSubject();
    		} else {
    			blankName = triple.getObject();
    		}
    		//insert into map
    		if(subgraph.get(blankName) == null) {
    			List<RDFTriple> curTriple = new ArrayList<RDFTriple>();
    			curTriple.add(triple);
    			subgraph.put(blankName, curTriple);
    		} else {
    			subgraph.get(blankName).add(triple);
    		}
    	}
    	
    	return subgraph;
    }
    
    /**
     * Separate list of triples into list of triples containing blank nodes and list without blank nodes
     * 
     * @param list
     * @return list of size 2. Index 0: list of non-blank triples Index 1: list of blank triples
     */
    public List<List<RDFTriple>> separateNodes(List<RDFTriple> list) {
    	List<RDFTriple> blank = new ArrayList<RDFTriple>();
    	List<RDFTriple> notBlank = new ArrayList<RDFTriple>();
    	for(RDFTriple triple: list) {
    		if(triple.containsBlankNode()) {
    			blank.add(triple);
    		} else {
    			notBlank.add(triple);
    		}
    	}
    	return Arrays.asList(notBlank, blank);
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
     * Callable for optimistic execution of cache: that is to deference an URI again and update cache
     * and rete network
     * 
     * @author Rick Liao
     */
    private static class UpdateCacheAndReteNetwork implements Callable<SolutionSet> {
    	private ReteNetwork reteNetwork;
    	private URI uri;
    	private List<RDFTriple> cachedTriples;
    	private final ExecutorService executor;
    	private boolean verbose;
    	private LinkedDataCacheProv cache;
    	private String query;
    	
    	public UpdateCacheAndReteNetwork(ReteNetwork reteNetwork, LinkedDataCacheProv cache, String query, URI uri, List<RDFTriple> cachedTriples, ExecutorService executor, boolean verbose) {
    		this.reteNetwork = reteNetwork;
    		this.cache = cache;
    		this.query = query;
    		this.uri = uri;
    		this.cachedTriples = cachedTriples;
    		this.executor = executor;
    		this.verbose = verbose;
    	}
    	
    	@Override
    	public SolutionSet call() throws Exception {
    		/*Timer timer = new Timer();
        	timer.start();
        	List<RDFTriple> cachedTriples = cache.dereference(uri, query);
        	
	        if (verbose) System.out.println(uri);
			// dereference the uri again to see if there are differences
			Callable<List<RDFTriple>> derefURL = new DereferenceURL(uri);
			Future<List<RDFTriple>> futureExtracted = executor.submit(derefURL);
			List<RDFTriple> extractedTriples = null;
			try {
				extractedTriples = futureExtracted.get();
			} catch(InterruptedException e) {
	        	System.out.println("Interrupted: " + uri);
	        } catch(ExecutionException e) {
	        	e.printStackTrace();
	        }
			
			//Separate blank nodes from normal nodes
			List<List<RDFTriple>> cached = separateNodes(cachedTriples);
			List<RDFTriple> cachedNotBlank = cached.get(0);
			List<RDFTriple> cachedBlank = cached.get(1);
			
			List<List<RDFTriple>> extracted = separateNodes(extractedTriples);
			List<RDFTriple> extractedNotBlank = extracted.get(0);
			List<RDFTriple> extractedBlank = extracted.get(1);
			
			// minus token
			List<RDFTriple> temp = new ArrayList<RDFTriple>(cachedNotBlank);
			cachedNotBlank.removeAll(extractedNotBlank);
			if(verbose) System.out.println("Delete non-blank: " + cachedNotBlank);
			// plus token
			extractedNotBlank.removeAll(temp);
			if(verbose) System.out.println("Add non-blank: " + extractedNotBlank);
			
			//insert into rete
			for(RDFTriple triple: cachedNotBlank) {
				if(!triple.getPredicate().getData().equals("http://null.null")) {
					reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(false, uri));
				}
			}
			for(RDFTriple triple: extractedNotBlank) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(true, uri));
			}
			
			//Calculate difference blank triples
			List<List<RDFTriple>> diff = calculateBlankDifference(cachedBlank, extractedBlank);
			if(verbose) System.out.println("Delete blank: " + diff.get(0));
			if(verbose) System.out.println("Add blank: " + diff.get(1));
			
			//update cache
			cache.updateCache(extractedNotBlank, cachedNotBlank, uri, query);
			cache.updateCache(diff.get(1), diff.get(0), uri, query);
			
			//insert into rete
			for(RDFTriple triple: diff.get(0)) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(false, uri));
			}
			for(RDFTriple triple: diff.get(1)) {
				reteNetwork.insertTokenIntoNetwork(triple.convertToTripleToken(true, uri));
			}
        	timer.stop();
        	if(hasTimer) System.out.println("Optimistic: " + timer.toString());
        	
        	// If there is something to dereference
        	return reteNetwork.getSolutionSet();*/
    		return null;
    	}
    }
    
    /**
     * Callable to Dereference URL and extract all RDF data. 
     * 
     * @author Slavcho Salvchev
     */
    public static class DereferenceURL implements Callable<List<RDFTriple>> {

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
                        	//System.err.println(e);// Ignore
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
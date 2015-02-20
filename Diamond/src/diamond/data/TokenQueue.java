/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.data;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.net.URL;
import java.net.URLConnection;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Queue of <code>TripleToken</code>s that implement a FIFO policy
 */
public class TokenQueue extends LinkedList<TripleToken> {

    private static final long serialVersionUID = 6110069189867879449L;
    
    /**
     * Create <code>TokenQueue</code>
     */
    public TokenQueue() {
        super();
    }

    /**
     * Add tokens to the queue using a data file
     * 
     * @param dataFile <code>File</code>
     * @param rdfFormat <code>RDFFormat</code>
     * @return <code>true</code> if enqueue is successful, otherwise
     *         <code>false</code>
     * @throws <code>Exception</code>
     */
    public boolean enqueue(File dataFile) throws Exception {
        // set up and initialize repository pointing to file of triple data
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        // set up connection to repository
        RepositoryConnection con = repository.getConnection();

        try {
            try {
                boolean formatFailure = false;

                try {
                    con.add(dataFile, null, RDFFormat.N3);
                } catch (Exception e) {
                    formatFailure = true;
                    con = repository.getConnection();
                }

                if (formatFailure == true) {
                    try {
                        con.add(dataFile, null, RDFFormat.NTRIPLES);
                        formatFailure = false;
                    } catch (Exception e) {
                        formatFailure = true;
                        con = repository.getConnection();
                    }
                }

                if (formatFailure == true) {
                    try {
                        con.add(dataFile, null, RDFFormat.RDFXML);
                        formatFailure = false;
                    } catch (Exception e) {
                        formatFailure = true;
                        con = repository.getConnection();
                    }
                }

                if (formatFailure == true) {
                    try {
                        con.add(dataFile, null, RDFFormat.TRIG);
                        formatFailure = false;
                    } catch (Exception e) {
                        formatFailure = true;
                        con = repository.getConnection();
                    }
                }

                if (formatFailure == true) {
                    try {
                        con.add(dataFile, null, RDFFormat.TRIX);
                        formatFailure = false;
                    } catch (Exception e) {
                        formatFailure = true;
                        con = repository.getConnection();
                    }
                }

                if (formatFailure == true) {
                    try {
                        con.add(dataFile, null, RDFFormat.TURTLE);
                        formatFailure = false;
                    } catch (Exception e) {
                        formatFailure = true;
                        con = repository.getConnection();
                    }
                }

                if (formatFailure == true) {
                    con.close();
                    repository.shutDown();
                    return false;
                }

            } catch (Exception e) {
                System.err.println("Unexpected error after attempting to parse the data file");
                System.exit(1);
            }

            // extract statements
            RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);

            // iterate through triples and set triple token
            while (statements.hasNext()) {
                Statement statement = statements.next();
                //System.out.println(statement);
                
                Binding binding = new Binding();

                // form subject
                String subjectString = statement.getSubject().toString();
                Element subj = formElement(SPO.SUBJECT, subjectString);
                // tripleToken.setSubject(subj);
                binding.setRDFTripleSubject(subj);
                
                //System.out.println(binding);

                // form predicate
                String predicateString = statement.getPredicate().toString();
                Element pred = formElement(SPO.PREDICATE, predicateString);
                // tripleToken.setPredicate(pred);
                binding.setRDFTriplePredicate(pred);

                //System.out.println(binding);
                
                // form object
                String objectString = statement.getObject().toString();
                Element obj = formElement(SPO.OBJECT, objectString);
                // tripleToken.setObject(obj);
                binding.setRDFTripleObject(obj);

                //System.out.println(binding);
                
                // initialize triple token
                TripleToken tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());

                // add binding to triple
                tripleToken.addTriple(binding);
                
                this.add(tripleToken);

            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return true;
    }

    /**
     * Add tokens to the queue using a dereference-able <code>URL</code>
     * 
     * @param url <code>URL</code>
     * @return <code>true</code> if enqueue was successful, otherwise false
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public boolean enqueue(URL url) throws Exception {
        // set up and initialize repository
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        // set up connection to repository
        RepositoryConnection con = repository.getConnection();

        // dereference url
        if (url == null) {
            return false;
        }
        
        //System.out.println("URL = " + url);
        
        URLConnection urlConnection = url.openConnection();
        InputStream instream = null;
        boolean urlConnectionFailure = false;
        boolean success = false;

        // use to suppress unnecessary err print streams from Sesame libraries
        System.setErr(null);
        
        try {
            try {
                urlConnection.addRequestProperty("accept", RDFFormat.N3.getDefaultMIMEType());
                instream = urlConnection.getInputStream();
                con.add(instream, url.toString(), RDFFormat.N3);
            } catch (Exception e) {
                urlConnectionFailure = true;
                urlConnection = url.openConnection();
                instream = null;
            }

            if (urlConnectionFailure == true) {
                try {
                    urlConnection.addRequestProperty("accept", RDFFormat.RDFXML.getDefaultMIMEType());
                    instream = urlConnection.getInputStream();
                    con.add(instream, url.toString(), RDFFormat.RDFXML);
                    urlConnectionFailure = false;
                } catch (Exception e) {
                    urlConnectionFailure = true;
                    urlConnection = url.openConnection();
                    instream = null;
                }
            }

            if (urlConnectionFailure == true) {
                try {
                    urlConnection.addRequestProperty("accept", RDFFormat.NTRIPLES.getDefaultMIMEType());
                    instream = urlConnection.getInputStream();
                    con.add(instream, url.toString(), RDFFormat.NTRIPLES);
                    urlConnectionFailure = false;
                } catch (Exception e) {
                    urlConnectionFailure = true;
                    urlConnection = url.openConnection();
                    instream = null;
                }
            }

            if (urlConnectionFailure == true) {
                try {
                    urlConnection.addRequestProperty("accept", RDFFormat.TURTLE.getDefaultMIMEType());
                    instream = urlConnection.getInputStream();
                    con.add(instream, url.toString(), RDFFormat.TURTLE);
                    urlConnectionFailure = false;
                } catch (Exception e) {
                    urlConnectionFailure = true;
                    urlConnection = url.openConnection();
                    instream = null;
                }
            }

            if (urlConnectionFailure == true) {
                try {
                    urlConnection.addRequestProperty("accept", RDFFormat.TRIG.getDefaultMIMEType());
                    instream = urlConnection.getInputStream();
                    con.add(instream, url.toString(), RDFFormat.TRIG);
                    urlConnectionFailure = false;
                } catch (Exception e) {
                    urlConnectionFailure = true;
                    urlConnection = url.openConnection();
                    instream = null;
                }
            }

            if (urlConnectionFailure == true) {
                try {
                    urlConnection.addRequestProperty("accept", RDFFormat.TRIX.getDefaultMIMEType());
                    instream = urlConnection.getInputStream();
                    con.add(instream, url.toString(), RDFFormat.TRIX);
                    urlConnectionFailure = false;
                } catch (Exception e) {
                    urlConnectionFailure = true;
                    urlConnection = url.openConnection();
                    instream = null;
                }
            }

            if (urlConnectionFailure == true) {
                System.out.println("Failed");
                con.close();
                repository.shutDown();
                return false;
            }

            // Extract results in set form
            RepositoryResult<Statement> statements = con.getStatements(null, null, null, false);
            
            // iterate through triples and set triple token
            while (statements.hasNext()) {
                Statement statement = statements.next();

                //System.out.println("--> " + statement);
                
                Binding binding = new Binding();

                // form subject
                String subjectString = statement.getSubject().toString();
                Element subj = formElement(SPO.SUBJECT, subjectString);
                binding.setRDFTripleSubject(subj);

                // form predicate
                String predicateString = statement.getPredicate().toString();
                Element pred = formElement(SPO.PREDICATE, predicateString);
                binding.setRDFTriplePredicate(pred);

                // form object
                String objectString = statement.getObject().toString();
                Element obj = formElement(SPO.OBJECT, objectString);
                binding.setRDFTripleObject(obj);

                // initialize triple token
                TripleToken tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());

                // add binding to triple
                tripleToken.addTriple(binding);

                tripleToken.urlWhereTripleTokenCameFrom = url;

                super.add(tripleToken);

                success = true;
            }
            
        } finally {
            con.close();
            repository.shutDown();
            return success;
        }
    }

    public void addTriple(String subject, String predicate, String object, URL origin) {
        Binding binding = new Binding();
        binding.setRDFTripleSubject(formElement(SPO.SUBJECT, subject));
        binding.setRDFTriplePredicate(formElement(SPO.PREDICATE, predicate));
        binding.setRDFTripleObject(formElement(SPO.OBJECT, object));
        TripleToken tripleToken = new TripleToken(TokenTag.PLUS, Timestamp.nextTimestamp());
        tripleToken.addTriple(binding);
        tripleToken.urlWhereTripleTokenCameFrom = origin;
        //System.out.println("TT: " + tripleToken);
        this.add(tripleToken);
    }
    
    /**
     * If the queue is not empty, return the last <code>TripleToken</code> in
     * the <code>TokenQueue</code>
     * 
     * @return <code>TripleToken</code>
     */
    public TripleToken dequeue() {
        TripleToken tokenToReturn = null;
        boolean done = false;

        for (int i = 0; i < this.size() && !done; ++i) {
            tokenToReturn = this.remove(0);
            done = true;
        }

        return tokenToReturn;
    }
    
    /**
     * Return an element that is formed from data (which is a String).
     */
    private Element formElement(SPO spo, String data) {
        return new Element(spo, DataType.determineDataType(data), data);
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

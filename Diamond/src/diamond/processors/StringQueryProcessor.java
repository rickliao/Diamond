/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.processors;

import diamond.parser.ParseException;
import diamond.parser.Parser;
import diamond.parser.syntaxtree.Query;
import diamond.parser.visitor.SparqlParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * StringQueryProcessor will process SPARQL query string
 */
public class StringQueryProcessor extends QueryProcessor {

    private String queryString;
    private boolean debug;
    private SparqlParser sparqlParser;

    /**
     * Creates StringQueryProcessor which is necessary to process SPARQL query
     * strings
     * 
     * @param queryString
     * @param debug
     * 
     */
    public StringQueryProcessor(String queryString, boolean debug) {
        this.queryString = queryString;
        this.debug = debug;
    }

    /**
     * Process a query string by sending it to a SparqlParser for parsing
     * 
     * @throws Exception
     */
    @Override
    public void process() throws Exception {
        // convert query into byte[]
        byte[] queryBytes = queryString.getBytes("UTF-8");
        // convert byte[] into input stream and send to input stream for parsing
        parser(new ByteArrayInputStream(queryBytes));
    }

    /**
     * Does the real work of process() by using the SparqlParser to assist in
     * parsing the query string
     * 
     * @param is
     * @throws ParseException
     */
    public void parser(InputStream is) throws ParseException {
        // send input stream to Parser
        Parser parser = new Parser(is);
        // extract query out of Parser
        Query query = parser.Query();
        // initialize parse tree visitor
        sparqlParser = new SparqlParser(debug);
        // visit parse tree for query
        query.accept(sparqlParser);
    }

    /**
     * Return SparqlParser used to parse the SPARQL query string
     * 
     * @return
     */
    @Override
    public SparqlParser getSparqlParser() {
        return sparqlParser;
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
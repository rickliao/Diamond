/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import diamond.parser.ParseException;
import diamond.parser.Parser;
import diamond.parser.syntaxtree.Query;
import diamond.parser.syntaxtree.SparqlSpin;
import diamond.parser.visitor.SparqlParser;

/**
 * FileQueryProcessor will process SPARQL query files by passing the work onto a
 * parser.
 */
public class FileQueryProcessor extends QueryProcessor {

    // instance variables
    private File queryFile;
    private boolean debug;
    private SparqlParser sparqlParser;

    /**
     * Creates a FileQueryProcessor with which to assist query processing from a
     * SPARQL query file.
     * 
     * @param queryFile
     * @param debug
     */
    public FileQueryProcessor(File queryFile, boolean debug) {
        this.queryFile = queryFile;
        this.debug = debug;
    }

    /**
     * Process a SPARQL query file.
     * 
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws Exception
     */
    @Override
    public void process() throws FileNotFoundException, ParseException {
        // create query file to an input stream and send to a parser
        parser(new FileInputStream(queryFile));
    }

    public void processSpinRule() throws FileNotFoundException, ParseException {
        // create query file to an input stream and send to a parser
        FileInputStream is = new FileInputStream(queryFile);
        Parser parser = new Parser(is);
        SparqlSpin spin = parser.SparqlSpin();
        sparqlParser = new SparqlParser(debug);
        spin.accept(sparqlParser);
    }

    /**
     * Does the real work of process() by using the SparqlParser to assist in
     * parsing the query file.
     * 
     * @param is
     */
    private void parser(InputStream is) throws ParseException {
        // send input stream to Parser
        Parser parser = new Parser(is);
        // extract query out of Parser
        Query query = parser.Query();
        // initialize parse tree visitor
        sparqlParser = new SparqlParser(debug);
        // visit parse tree for query using a visitor (namely SparqlParser)
        query.accept(sparqlParser);
    }

    /**
     * Return SparqlParser used to query the processor.
     * 
     * @return sparqlParser
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
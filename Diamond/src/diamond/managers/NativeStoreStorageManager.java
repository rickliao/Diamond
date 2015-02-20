/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.managers;

import java.io.File;

import diamond.data.SolutionSet;
import diamond.data.Timer;
import diamond.data.TokenQueue;
import diamond.data.TripleToken;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;
import diamond.retenetwork.ReteNetwork;

/**
 * <code>NativeStoreStorageManager</code> implements a {@link StorageManager}
 * interface that interacts with native triple stores of data in a variety of
 * formats. Uses the <a href="http://www.openrdf.org">Sesame Framework</a>.
 */
public class NativeStoreStorageManager {

    private File file;
    private QueryProcessor queryProcessor;
    private ReteNetwork reteNetwork;
    private TokenQueue tokenQueue;

    /**
     * Create a <code>NativeSToreStorageManager</code>
     * 
     * @param file
     * @throws <code>Exception</code>
     */
    public NativeStoreStorageManager(QueryProcessor queryProcessor, File file) throws Exception {
        this.file = file;
        this.queryProcessor = queryProcessor;
        reteNetwork = initializeReteNetwork(this.queryProcessor);
        tokenQueue = new TokenQueue();
    }

    /**
     * Execute Query hunts for tokens from the data file and inserts them into a
     * queue and then processes the tokens in a network
     * 
     * @throws <code>Exception</code>
     */
    public double executeQuery(boolean hasTimer) throws Exception {
        Timer timer = new Timer();
        tokenQueue.enqueue(file);
        timer.start();

        while (tokenQueue.size() > 0) {
            TripleToken tripleToken = tokenQueue.dequeue();

            if (tripleToken != null) {
                reteNetwork.insertTokenIntoNetwork(tripleToken);
            } else {
                throw new Exception("The TripleToken is unexpectedly null");
            }
        }

        timer.stop();
        SolutionSet solnSet = reteNetwork.getSolutionSet();

        System.out.println("Solution Set ...\n");
        System.out.println(solnSet);
        System.out.println("Size: " + solnSet.size());

        if (timer.getStatus() == false && hasTimer) {
            System.out.println(timer.toString());
            return timer.timeInSeconds();
        }
        return 0;
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

    /***************************************************************/

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("$NativeStoreStorageManager RULES DATA");
            System.exit(0);
        }

        File rules = new File(args[0]);
        File data = new File(args[1]);

        // Compile and Parse Rules
        FileQueryProcessor queryProcessor = new FileQueryProcessor(rules, true);
        queryProcessor.process();

        NativeStoreStorageManager manager = new NativeStoreStorageManager(queryProcessor, data);
        manager.executeQuery(false);
    }

    /**
     * Return a Rete Network
     * 
     * @return <code>ReteNetwork</code>
     */
    public ReteNetwork getReteNetwork() {
        return reteNetwork;
    }

    public void setFile(File file) {
        this.file = file;
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
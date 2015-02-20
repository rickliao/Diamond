package diamond.managers;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * URLManager manages the URLs that are to be dereferenced
 * 
 * @author Rodolfo Kaplan Depena
 * @author Slavcho Slavchev
 */
public class URLManager {

    private final Set<URL> urlsNotDereferencedYet;
    private final Set<URL> urlsSuccessfullyDereferenced;
    private final Set<URL> urlsUnsuccessfullyDereferenced;

    public URLManager() {
        this.urlsNotDereferencedYet = new HashSet<URL>();
        this.urlsSuccessfullyDereferenced = new HashSet<URL>();
        this.urlsUnsuccessfullyDereferenced = new HashSet<URL>();
    }

    public Set<URL> getURLsNotDereferencedYet() { return urlsNotDereferencedYet; }
    public Set<URL> getURLsSuccessfullyDereferenced() { return urlsSuccessfullyDereferenced; }
    public Set<URL> getURLsUnsuccessfullyDereferenced() { return urlsUnsuccessfullyDereferenced; }
    
    /**
     * Add URL if it wasn't present before.
     */
    public boolean add(URL url) {
        boolean success = false;
        if(!urlsNotDereferencedYet.contains(url) && !urlsSuccessfullyDereferenced.contains(url)
                && !urlsUnsuccessfullyDereferenced.contains(url)) {
            urlsNotDereferencedYet.add(url);
            success = true;
        }
        return success;
    }
    
    public void add(Collection<URL> urls) {
        for(URL url : urls) add(url);
    }

    /**
     * Return a URL for dereferencing
     */
    public URL getURLForDereferencing() {
        URL urlToReturn = null;
        if(!urlsNotDereferencedYet.isEmpty()) {
            urlToReturn = urlsNotDereferencedYet.iterator().next();
        }
        return urlToReturn;
    }
    
    public Set<URL> getAllURLsForDereferencing() {
        return urlsNotDereferencedYet;
    }

    /**
     * Signals with a 'true' or 'false' as to whether or not URLs need to be
     * dereferenced
     * 
     * @return true if there are still urls to be dereferenced, otherwise false
     */
    public boolean urlsStillNeedToBeDereferenced() {
        return !urlsNotDereferencedYet.isEmpty();
    }

    /**
     * Update the URLManager on the status of the attempt to dereference a url.
     * 
     * @param url - the url in question
     * @param URLSuccessfullyDereferenced - boolean status on successful
     *            dereference (or not)
     */
    public void updateStatusOfURLDereferenced(URL url, boolean URLSuccessfullyDereferenced) {
        if (URLSuccessfullyDereferenced) {
            urlsSuccessfullyDereferenced.add(url);
        } else {
            urlsUnsuccessfullyDereferenced.add(url);
        }
        urlsNotDereferencedYet.remove(url);
    }
    
    @Override
    public String toString() {
        return "URLManager [urlsNotDereferencedYet=" + urlsNotDereferencedYet + ", urlsSuccessfullyDereferenced="
                + urlsSuccessfullyDereferenced + ", urlsUnsuccessfullyDereferenced=" + urlsUnsuccessfullyDereferenced
                + "]";
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
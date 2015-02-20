package diamond.processors;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import diamond.data.Binding;
import diamond.data.Element;
import diamond.data.DataType;
import diamond.data.TriplePattern;
import diamond.bookkeeping.PrefixLibrary;

/**
 * URLProcessor is responsible extracting the URLs of Triples.
 * 
 * @author Slavcho Slavchev
 */
public class URLProcessor {

    private PrefixLibrary prefixLibrary;

    public URLProcessor() {
        prefixLibrary = new PrefixLibrary();
    }
    
    /**
     * Extracts URLs from triple patterns
     * 
     * @throws <code>Exception</code>
     */
    public List<URL> extractURLsFromTriplePattern(TriplePattern triplePattern) throws Exception {
        Element subject = triplePattern.getSubject();
        Element object = triplePattern.getObject();
        List<URL> urls = new ArrayList<URL>();
        
        if(subject.getData().contains("http") || subject.getDataType() == DataType.URL) {
            urls.add(formatStringIntoURL(subject.getData()));
        }
        if(object.getData().contains("http") || object.getDataType() == DataType.URL) {
            urls.add(formatStringIntoURL(object.getData()));
        }
        return urls;
    }
    
    /**
     * Extracts URLs from binding
     * 
     * @throws <code>Exception</code>
     */
    public List<URL> extractURLsFromBinding(Binding binding) throws Exception {
        Element subject = binding.getRDFTripleSubject();
        Element object = binding.getRDFTripleObject();
        List<URL> urls = new ArrayList<URL>();
        
        if(subject.getDataType() == DataType.URL && subject.getData().contains("http")) {
            urls.add(formatStringIntoURL(subject.getData()));
        }
        if(object.getDataType() == DataType.URL && object.getData().contains("http")) {
            urls.add(formatStringIntoURL(object.getData()));
        }
        return urls;
    }

    /**
     * Turns strings, that could potentially be URLs, into an actual URL (with
     * appropriate formatting).
     * 
     * @param potentialURL <code>String</code>
     * @return <code>URL</code>
     * @throws <code>Exception</code>
     */
    private URL formatStringIntoURL(String potentialURL) throws Exception {
        if (potentialURL != null) {
            int potentialURLsLength = potentialURL.length();
            if (potentialURLsLength > 1)             {
                int locationOfColon = potentialURL.indexOf(':');
                int lastCharLocation = potentialURLsLength - 1;
                // if potential URL has colon, then its a good chance it has prefix
                if (locationOfColon != -1 && potentialURL.contains("//") == false) {
                    // make sure colon is not last char (else this is an unexpected string)
                    if (locationOfColon < lastCharLocation) {
                        // extract prefix
                        String prefix = potentialURL.substring(0, locationOfColon + 1);
                        // get url using prefix
                        String url = prefixLibrary.get(prefix);
                        if (url != null) {
                            return new URL(formatPotentialURL(url + potentialURL.substring(locationOfColon + 1)));
                        } else return null;
                    } else {
                        throw new Exception("This is unexpected: " + potentialURL);
                    }
                } else try { // potentialURL has no colon, and therefore is not a prefixed name
                    return new URL(formatPotentialURL(potentialURL));
                } catch (Exception e) {}
            }
        }
        return null;
    }

    /**
     * Formats potential URLs
     * 
     * @param potentialURL <code>String</code>
     * @return <code>String</code>
     */
    private String formatPotentialURL(String potentialURL) {
        if (potentialURL != null) {
            int potentialURLsLength = potentialURL.length();
            if (potentialURLsLength > 1) {
                char firstCharOfPotentialURL = potentialURL.charAt(0);
                char lastCharOfPotentialURL = potentialURL.charAt(potentialURLsLength - 1);
                if (firstCharOfPotentialURL == '<' && lastCharOfPotentialURL == '>') {
                    return potentialURL.substring(1, potentialURLsLength - 1);
                } else {
                    if (potentialURL.startsWith("http")) {
                        return potentialURL;
                    }
                }
            }
        }
        return null;
    }
}
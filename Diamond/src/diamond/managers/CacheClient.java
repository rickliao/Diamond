package diamond.managers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FutureResponseListener;

import java.io.File;
import java.io.IOException;

public class CacheClient {
	
	private HttpClient httpClient = null;
	
	public void start() throws Exception {
		// Instantiate HttpClient
        httpClient = new HttpClient();
         
        // Configure HttpClient
        httpClient.setFollowRedirects(false);
         
        // Start HttpClient
        httpClient.start();
	}
	
	public String executeQuery(File query, int steps, boolean timer, boolean verbose) throws InterruptedException, TimeoutException, ExecutionException, IOException, ClassNotFoundException {
		Request request = httpClient.newRequest("http://localhost:3000/?query="+query.toString()+"&steps="+steps+"&timer="+timer+"&verbose="+verbose)
                .timeout(1000000, TimeUnit.SECONDS);
		// Increase response content buffer to 500 MB 
		FutureResponseListener listener = new FutureResponseListener(request, 500 * 1024 * 1024);
		request.send(listener);
		ContentResponse response = listener.get(1000000, TimeUnit.SECONDS);
		
        int status = response.getStatus();
        if(status == 200) {
        	return response.getContentAsString();
        } else {
        	return "response error: "+status;
        }
	}

}

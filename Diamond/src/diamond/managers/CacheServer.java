package diamond.managers;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
public class CacheServer {
	
	public static void main(String[] args) throws Exception {
		// The Server
        Server server = new Server();
 
        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);
 
        // Set the connector
        server.addConnector(http);
 
        // Set a handler
        server.setHandler(new LinkedDataCacheHandler(new File("cache.n3")));
 
        // Start the server
        server.start();
        server.join();
	}
}

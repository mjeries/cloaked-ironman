import java.io.* ;
import java.net.* ;
import javax.swing.*;

public class WebServer
{
  public static void main(String argv[]) throws Exception
  {
    // set the port number to something higher than 1024
    int port;
    
    // create and establish the main server socket
    ServerSocket serverSocket = null;
    //TODO:  make sure you bind the socket to the port#
    //       refer to lecture slides for an example...
    
    port = 8092;
    
    try {
    serverSocket = new ServerSocket(port);
    } catch (IOException e) {
    System.err.println("Could not listen on port"); 
    e.printStackTrace(); // shows you details
    System.exit(1);
    }
    
    // handle incoming http requests
    while (true) {
        // listen and accept TCP connection request off the main serverSocket
    	Socket newSocket = null;

        //TODO:  You need to accept the connection from the server socket and 
        //       have it bound to this new socket.
        //       Make sure you also do some error handling here as well (try - catch)
        try {
        	newSocket = serverSocket.accept();
                
        	} catch (IOException e) {
        	System.err.println("accept failed");
        	System.exit(1);
        	}
        HttpHandler x = new HttpHandler(newSocket);
		Thread thread = new Thread(x);       
		thread.start(); 
        
        
        // construct the HttpHandler object to process the incoming request
        
        
        
        // pass it off to a new thread so that it runs in parallel
                          
    }         
  }
}
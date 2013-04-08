import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.text.*;
import javax.swing.*;

public class HttpHandler implements Runnable {
	String CRLF = "\r\n";  // this is specifically used in the HTTP protocols
	Socket socket;

	public HttpHandler(Socket socket) throws Exception 
	{
		this.socket = socket;
	}
  
	public void run()   // run() method of Runnable interface
	{
		try {
			processRequest();
 	   } catch (Exception e) {
		   System.out.println(e);
	   }
	}

	private void processRequest() throws Exception
	{
		System.out.println("*** Processing Request ******** " +System.currentTimeMillis()%10000 + " *********");
		// TODO: get references to the socket's input and output streams
		InputStream is = socket.getInputStream();
		OutputStream ops = socket.getOutputStream();
		DataOutputStream os = new DataOutputStream(ops);
		String requestedFileName = "";
	
		// set up input stream filters
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// get the request line of the HTTP request message
		String requestLine = br.readLine();

		// display the request line
		System.out.println(requestLine);  
	
		// -get and display the header lines
		// -note that the requests contain varying nubmer of header lines
		// -Q:when the headerLine has a lenght of zero, 
		//    what does it mean? see HTTP protocol
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}  
	
		// grab the file name, including the '/', and append a '.' in front of it. 
	
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();  // ignore GET token
		requestedFileName = tokens.nextToken();
		requestedFileName = "." + requestedFileName; // look within current directory
		
		/* figure out which file is being requested and (determine whether or not such a file exists)
			construct a properly formatted HTTP response message
			send the response 
		*/
	
		// open/access the file
	
		FileInputStream fis = null;
		boolean fileFound;
		try {
			fis = new FileInputStream(requestedFileName);
			fileFound = true;
		}  catch  (FileNotFoundException e) {
			fileFound = false;
		}
	 
		// Create response message.
		String statusCode = null;
		String contentTypem = null;
		String content = null;
		
		if(fileFound) {
			statusCode = "HTTP/1.1 200\n";
			contentTypem = "Content-Type: " + contentType(requestedFileName);
			
		} else {
			statusCode = "HTTP/1.1 404\n";
			contentTypem = "Content-Type: text/html\n";
			content = "<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
		}
		
		// Send response message
		
		os.writeBytes(statusCode);
		os.writeBytes(contentTypem);		
		os.writeBytes(CRLF);
			
		// send out the actual contents of the file
	
		if (fileFound) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(content);
		}
		
	    // close everything 
		os.close();
	    br.close();
	    socket.close();
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
	{    
	    byte[] buf = new byte[1024];
	    int bytes = 0;                           // number of bytes read/grabbed from the file    
	    while((bytes = fis.read(buf)) != -1 ) {  // grab from file into the buffer
			os.write(buf, 0, bytes);            // write it into the socket's output stream
		}
	}
    
	private static String contentType(String fileName)
	{
		if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
		{
			return "text/html\n";
		}
		if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg"))
		{
			return "image/jpeg\n";
		}
		
		if(fileName.endsWith(".ico"))
		{
			return "image/x-icon\n";
		}
		return "application/octet-stream\n";
	}
  }
import java.net.*;
import java.io.*;

public class Server {
	public static void main(String[] args) throws IOException {

		int port = 4445;

		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		boolean serverIsRunning = true;
		
		try {
			System.out.println("Starting up server... \n");
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + port + ".");
			System.exit(1);
		}
		
		while (serverIsRunning) {

			try {
				System.out.println("Waiting for connection... \n");
				clientSocket = serverSocket.accept();
				System.out.println("Welcome to our chat at: "+clientSocket.getInetAddress().getHostName());
				(new Thread(new ConnectionHandler(clientSocket))).start();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			} finally {
			}

		}

		serverSocket.close();

	}
}
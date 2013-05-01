import java.io.*;
import java.net.*;

public class ConnectionHandler implements Runnable {

	Socket clientConnection;
	String inputLine = null, outputLine = null;
	PrintWriter out = null;
	BufferedReader in = null;

	public ConnectionHandler(Socket clientConnection) {
		this.clientConnection = clientConnection;
	}

	@Override
	public void run() {

		try {
			out = new PrintWriter(clientConnection.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to create outgoing PrintWriter.");
		}

		try {
			in = new BufferedReader(new InputStreamReader(
					clientConnection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.err
					.println("Failed to create incoming BufferedReader from client connection.");
		}

		outputLine = null;
		out.println(outputLine);

		try {
			while ((inputLine = in.readLine()) != null) {
				outputLine = inputLine;
				out.println(outputLine);
				if (outputLine.equals("Bye."))
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while processing text");
		}

		out.close();

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close incoming BufferedReader.");
		}

		try {
			clientConnection.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close incoming client connection.");
		}
	}
}

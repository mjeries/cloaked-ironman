import java.io.*;
import java.net.*;


public class ConnectionHandler implements Runnable{

	Socket clientConnection;
	
	public ConnectionHandler(Socket clientConnection){
		this.clientConnection = clientConnection;
	}
	
	@Override
	public void run() {

		
		PrintWriter out = null;
		try {
			out = new PrintWriter(clientConnection.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					clientConnection.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputLine, outputLine;
		ChatHandler kkp = new ChatHandler();

		outputLine = kkp.processInput(null);
		out.println(outputLine);

		try {
			while ((inputLine = in.readLine()) != null) {
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
				if (outputLine.equals("Bye."))
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			clientConnection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

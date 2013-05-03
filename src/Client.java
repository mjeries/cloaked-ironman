import java.io.*;
import java.net.*;

public class Client
{
	//drew integration..
	private String server, username;
	private ChatLog cg;
	private int port;
	private ObjectOutputStream sOutput;
	private ObjectInputStream sInput;
	private Socket socket;

	public Client(String server, int port, String username, ChatLog cg)
	{
		this.server = server;
		this.port = port;
		this.username = username;
		this.cg = cg;
	}

	public boolean start()
	{
		boolean ifSuccess = true;
		boolean success;
		//attempt to connect to server

		try{socket = new Socket(server, port);}
		// if fails
		catch(Exception e)
		{
			cg.append("Error connecting to server: " + e.getMessage());
			ifSuccess = false;
		}

		String msg = "Connection accepted " +socket.getInetAddress() + ":" + socket.getPort();
		cg.append(msg);

		// create data streams

		try
		{
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch(IOException ioe)
		{
			cg.append("Error creating new Input/output streams: " + ioe.getMessage());
			ifSuccess = false;
		}

		//create Thread to listen from server
		new ListenFromServer().start();

		// send username to server
		try{sOutput.writeObject(username);}
		catch(Exception e)
		{
			cg.append("Error, can't login: " + e);
			disconnect();
			ifSuccess = false;
		}

		// if all works out continue
		if(ifSuccess =! false){success = true;}
		else{success = false;}
		return success;
	}

	//.. drew integration
	public static void main(String[] args) throws IOException {

		Socket kkSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		int port = 4445;

		try {
			kkSocket = new Socket("localhost", port);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					kkSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: taranis.");
			System.exit(1);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: taranis.");
			System.exit(1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String fromServer;
		String fromUser;

		while ((fromServer = in.readLine()) != null) {
			System.out.println("Server: " + fromServer);
			if (fromServer.equals("Bye."))
				break;

			fromUser = stdIn.readLine();
			if (fromUser != null) {
				// System.out.println("Client: " + fromUser);
				out.println(fromUser);
			}
		}

		fromUser = "Bye.";
		out.close();
		in.close();
		stdIn.close();
		kkSocket.close();
	}

	// integration of Drew's code

	public void sendMessage(ChatMessage msg)
	{
		try{sOutput.writeObject(msg);}
		catch(IOException e){cg.append("Exception writing to server: " + e.getMessage());}
	}

	public void disconnect()
	{
		try
		{
			if(sInput != null) sInput.close();
		}
		catch(Exception e){cg.append("Couldn't close InputStream");}
		try
		{
			if(sOutput != null){sOutput.close();}
		}
		catch(Exception e){cg.append("Couldn't close OutputStream");}
		try
		{
			if(socket != null)socket.close();
		}
		catch(Exception e){cg.append("Couldn't close socket");}
		cg.connectionFailed();
	}


	class ListenFromServer extends Thread
	{
		public void run()
		{
			while(true)
			{
				try
				{
					String msg = (String) sInput.readObject();
					cg.append(msg);
				}
				catch(IOException ioe)
				{
					cg.append("Server has closed the connection: " + ioe);
				}
				catch(ClassNotFoundException cnfe){cg.append("Class not found");}
			}
		}
	}// class listen from server
}//class client
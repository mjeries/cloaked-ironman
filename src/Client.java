import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

<<<<<<< HEAD
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/* Client needs some work.
 * 
 * 
 */


public class Client {

	
	JFrame frame = new JFrame("window");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);

	static Socket kkSocket;
	static PrintWriter out;
	static BufferedReader in;
	static int port = 4445;
	static String host = "localhost";

	static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	static String fromServer;
	static String fromUser;

	public Client() {
		textField.setEditable(true);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, "South");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}

	public static void main(String[] args) throws Exception {

		Client client = new Client();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}

	private void run() {

		kkSocket = null;
		out = null;
		in = null;

		try {
			System.out.println("trying to create socket");
=======
public class Client
{
	//drew integration..
	private String server, username;
	private ClientGUI cg;
	private int port;
	private ObjectOutputStream sOutput;
	private ObjectInputStream sInput;
	private Socket socket;

	public Client(String server, int port, String username, ClientGUI cg)
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
		String host = "localhost";
		String fromServer, fromUser;
		
		int port = 4445;

		try {
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b
			kkSocket = new Socket(host, port);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					kkSocket.getInputStream()));
		} catch (UnknownHostException e) {
<<<<<<< HEAD
			System.err.println("Don't know about host: " + host);
=======
			System.err.println("Don't know about host: \"" + host +  "\".");
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b
			System.exit(1);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: " + host);
			System.exit(1);
		}

<<<<<<< HEAD
		System.out.println("ready to read input.");

		try {
			while ((fromServer = in.readLine()) != null) {
				System.out.println(fromServer);
				messageArea.append(fromServer + "\n");

				fromUser = stdIn.readLine();

				if (fromUser != null) {
					out.println(fromUser);
				}
				if (fromUser == "/exit") {
					break;
				}
=======
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		
		while ((fromServer = in.readLine()) != null) {
			System.out.println("Server: " + fromServer);
			if (fromServer.equals("Bye."))
				break;

			fromUser = stdIn.readLine();
			if (fromUser != null) {
				out.println(fromUser);
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b
			}
			System.out.println("Shutting down client");
			out.flush();
			out.close();
			in.close();
			stdIn.close();
			kkSocket.close();
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: taranis.");
			System.exit(1);
		}
<<<<<<< HEAD
=======

		fromUser = "Bye.";
		out.close();
		in.close();
		stdIn.close();
		kkSocket.close();
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Adapted from http://cs.lmu.edu/~ray/notes/javanetexamples/
 */

public class Server {

	static int port = 4445;
	static String host = "localhost";
	static boolean serverRunning = true;

	static HashSet<String> names = new HashSet<String>();
	static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	static Hashtable<String, ConnectionHandler> table = new Hashtable<String, ConnectionHandler>();
	static ServerSocket server;
	static ServerConsole console = new ServerConsole();
	static Encryption enc = new Encryption();
	static final String symKeyHex = "000102030405060708090A0B0C0D0E0F";

	public static void main(String[] args) throws Exception {
		
		Security.addProvider(new BouncyCastleProvider());


		server = new ServerSocket(port);

		console.start();
		
		System.out.println("Ready.");

		try {
			while (serverRunning) {
				new ConnectionHandler(server.accept()).start();
			}
		} finally {
			sendMessage("SERVERSHUTDOWN");
			server.close();
			System.exit(0);
		}

	}

	public static void sendMessage(String string) {

		try {
			for (PrintWriter writer : writers) {
				//writer.println(string);
				writer.println(Encryption.encrypt(string, symKeyHex));	//encryption version
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Collection modified while iterating through");
			sendMessage(string);
		}
	}

	// one handler for each client
	static class ConnectionHandler extends Thread {

		private static SimpleDateFormat date = new SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss.SS");
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private static Date now = new Date();
		boolean chRunning = true;

		public ConnectionHandler(Socket socketIn) {
			this.socket = socketIn;
		}

		public void run() {

			System.out.println("New connection accepted " + date.format(now));

			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				while (true) {

					out.println(Encryption.encrypt("SUBMITNAME", symKeyHex));
					//out.println("SUBMITNAME");	//this is the old version for the line above
					
					name = Encryption.decrypt(in.readLine(), symKeyHex);
					//name = in.readLine();		//this is the old version for the line above
					if (name == null) {
						return;
					}
					synchronized (names) {
						if (!names.contains(name) && name != "Server") {
							names.add(name);
							break;
						}
					}
				}
				
				out.println(Encryption.encrypt("NAMEACCEPTED", symKeyHex));
				//out.println("NAMEACCEPTED");		//this is the old version for the line above
				System.out.println("Added client: " + name);
				writers.add(out);
				table.put(name, this);
				
				sendMessage(name + " has connected.");

				String input;
				
				while (chRunning) {
					
					input = Encryption.decrypt(in.readLine(), symKeyHex);	//encryption version here
					//input = in.readLine();	//old version here
					
					if (input == null) {
						return;
					}

					if (input.startsWith("<DISCONNECT>")) {
						
						chRunning = false;
						sendMessage("MESSAGE Server: " + name + " has disconnected.");
						break;
					} else if (input.startsWith("<ME>")) {
						input = input.substring(4);
						sendMessage("MESSAGE *" + name + input);
						
					} else if (input.startsWith("<LISTUSERS>")){
						String userList = "";
						
						for (String user : names) {
							userList = userList + user + "\t";
						}
						sendMessage("MESSAGE Server: Connected Users\n" + userList);
					}
					
					else {
						sendMessage("MESSAGE " + name + ": " + input);

					}

				}

			} catch (IOException e) {

				if (name == null) {
					System.err.println("Name Error");
				} else if (e.getMessage() == "Connection reset") {
					System.out.println("A client disconnected");
				} else {
					e.printStackTrace();

				}
			}

			closeConnection();

		}

		private void closeConnection() {
			
			names.remove(name);
			
			out.flush();
			out.close();

			try {
				in.close();
				socket.close();
			} catch (IOException e) {
				System.err.println("Error trying to close input/socket.");
				e.printStackTrace();
			}
		}

		public static String getCreationTime() {
			return date.format(now);
		}

		
		
	}

	private static class ServerConsole extends Thread {

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String fromUser;
		String args;

		public ServerConsole() {

		}

		public void run() {
			try {
				while ((fromUser = stdIn.readLine()) != null) {

					processArgs();

					switch (fromUser) {
					case "exit":
						signalServerOff();
						break;
					case "list":
						listClients();
						break;
					case "broadcast":
						sendMessage(args);
						System.out.println(args);
						break;
					case "query":
						queryUser(args);
						break;
					case "rawwrite":
						rawWrite(args);
						break;
					}

				}

			} catch (Exception e) {
				System.err.println("Error trying to read input");
				e.printStackTrace();
			}
		}

		/** Used mainly for debugging, allows you to write anything
		 * directly to every user, no protocol.
		 * @param args2 what you want to broadcast.
		 */
		private void rawWrite(String args2) {

			if (args2.startsWith("-")) {

			}

		}

		private void processArgs() {

			if (fromUser.startsWith("broadcast")) {
				args = fromUser.substring(9).trim();
				fromUser = "broadcast";
			}
			if (fromUser.startsWith("query")) {
				args = fromUser.substring(5).trim();
				fromUser = "query";
			}
			if (fromUser.startsWith("rawwrite")) {
				args = fromUser.substring(8).trim();
				fromUser = "rawwrite";
			}

		}

		// prints out info about the user (time only for now)
		@SuppressWarnings("static-access")
		private void queryUser(String args2) {
			
			// prints out when the client was created
			System.out.println(args2 + ": " + table.get(args2).getCreationTime());
		}

		private void listClients() {
			for (String user : names) {
				System.out.println(user);
			}
		}

		private void signalServerOff() {
			serverRunning = false;

			try {
				Socket shutdown = new Socket(host, port);
				shutdown.close();
			} catch (UnknownHostException e) {
				System.err.println("Unable to connect back to myself!");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("IOException!");
				e.printStackTrace();
			}

		}
	}
}
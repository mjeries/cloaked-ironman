import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Date;

/*Adapted from http://cs.lmu.edu/~ray/notes/javanetexamples/
 * 
 * 
 */

public class Server {
<<<<<<< HEAD

	static int port = 4445;
	static String host = "localhost";
	static boolean serverRunning = true;

	static HashSet<String> names = new HashSet<String>();
	static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	static ServerSocket server;
	static ServerConsole console = new ServerConsole();

	public static void main(String[] args) throws Exception {

		server = new ServerSocket(port);

		console.start();

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

=======
	public static void main(String[] args) throws IOException {

		int port = 4445;

		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		boolean serverIsRunning = true;
		
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b
		try {
			for (PrintWriter writer : writers) {
				writer.println(string);
			}
		} catch (ConcurrentModificationException e) {
			System.err.println("Collection modified while iterating through");
			sendMessage(string);
		}
<<<<<<< HEAD
	}

	// one handler for each client
	private static class ConnectionHandler extends Thread {

		private Date date = new Date();
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public ConnectionHandler(Socket socketIn) {
			this.socket = socketIn;
		}

		public void run() {

			System.out.println("New connection accepted");
=======
		
		while (serverIsRunning) {
>>>>>>> b3fc46a1cd7490721919e6bb57f726ccd1fe817b

			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				while (true) {
					out.println("SUBMITNAME");
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (names) {
						if (!names.contains(name)) {
							names.add(name);
							break;
						}
					}
				}
				out.println("NAMEACCEPTED");
				System.out.println("Added client: " + name);
				writers.add(out);

				while (true) {
					String input = in.readLine();
					if (input == null) {
						return;
					}
					sendMessage("MESSAGE " + name + ": " + input);
				}

			} catch (IOException e) {
				System.err.println("Name Error");
				e.printStackTrace();
			}

			closeConnection();

		}

		private void closeConnection() {
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

		/**
		 * @return the date
		 */
		public Date getDate() {
			return date;
		}

		/**
		 * @param date
		 *            the date to set
		 */
		public void setDate(Date date) {
			this.date = date;
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
					}

				}

			} catch (Exception e) {
				System.err.println("Error trying to read input");
				e.printStackTrace();
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
		}

		private void queryUser(String args2) {
			// need some way of matching username in hashset with date

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
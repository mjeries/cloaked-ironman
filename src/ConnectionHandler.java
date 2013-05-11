import java.io.*;
import java.net.*;
import java.util.HashSet;

public class ConnectionHandler extends Thread {

	Socket clientConnection;
	PrintWriter out;
	BufferedReader in;
	String inputLine, outputLine;
	ChatHandler ch;
	static HashSet<PrintWriter> writers;

	public ConnectionHandler(Socket clientConnection,
			HashSet<PrintWriter> writers) {
		this.clientConnection = clientConnection;
		ch = new ChatHandler(clientConnection);
		this.writers = writers;
	}

	@Override
	public void start() {

		try {
			in = new BufferedReader(new InputStreamReader(
					clientConnection.getInputStream()));
			out = new PrintWriter(clientConnection.getOutputStream(), true);
			writers.add(out);

			outputLine = null;

			// String input = null;
			while ((inputLine = in.readLine()) != null) {
				String input = in.readLine();

				for (PrintWriter writer : writers) {
					writer.println("Server: " + input);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * try { out = new PrintWriter(clientConnection.getOutputStream(),
		 * true); in = new BufferedReader(new InputStreamReader(
		 * clientConnection.getInputStream())); } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 * outputLine = null; out.println(outputLine);
		 * 
		 * try { while ((inputLine = in.readLine()) != null) { outputLine =
		 * inputLine; out.println(outputLine); if (outputLine.equals("Bye."))
		 * break; } out.close();
		 * 
		 * in.close(); clientConnection.close(); } catch (IOException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}

	private static class ChatHandler extends Thread {

		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private String inputLine, outputLine;

		public ChatHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				writers.add(out);

				// String input = null;
				while (true) {
					String input = in.readLine();

					if (input == null) {
						return;
					}
					for (PrintWriter writer : writers) {
						writer.println("Server: " + input);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * try { while ((inputLine = in.readLine()) != null) { outputLine =
			 * in.readLine(); out.println(outputLine); } } catch (IOException e)
			 * { // TODO Auto-generated catch block e.printStackTrace(); }
			 */

		}

	}

	private static class ChatHandler extends Thread {

		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private String inputLine, outputLine;

		public ChatHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				writers.add(out);

				// String input = null;
				while (true) {
					String input = in.readLine();

					if (input == null) {
						return;
					}
					for (PrintWriter writer : writers) {
						writer.println("Server: " + input);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * try { while ((inputLine = in.readLine()) != null) { outputLine =
			 * in.readLine(); out.println(outputLine); } } catch (IOException e)
			 * { // TODO Auto-generated catch block e.printStackTrace(); }
			 */

		}

	}
}

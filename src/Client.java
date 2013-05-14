//new
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Security;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Adapted from http://cs.lmu.edu/~ray/notes/javanetexamples/ A simple
 * Swing-based client for the chat server. Graphically it is a frame with a text
 * field for entering messages and a textarea to see the whole dialog.
 * 
 * The client follows the Chat Protocol which is as follows. When the server
 * sends "SUBMITNAME" the client replies with the desired screen name. The
 * server will keep sending "SUBMITNAME" requests as long as the client submits
 * screen names that are already in use. When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE " then all characters
 * following this string should be displayed in its message area.
 */
public class Client {

	// hardcoded key :(
	public static final String symKeyHex = "000102030405060708090A0B0C0D0E0F";
	BufferedReader in;
	static PrintWriter out;
	JFrame frame = new JFrame("Cloaked Iron");
	static JTextField textField = new JTextField(45);
	static JTextArea messageArea = new JTextArea(8, 40);
	static ChatHandler ch = new ChatHandler();
	static Encryption enc = new Encryption();
	static boolean running = true;
	static JScrollPane scrollPane = new JScrollPane(messageArea);

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Return in the listener sends the
	 * textfield contents to the server. Note however that the textfield is
	 * initially NOT editable, and only becomes editable AFTER the client
	 * receives the NAMEACCEPTED message from the server.
	 */
	public Client() {

		// Layout GUI
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, "South");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messageArea.setLineWrap(true);

		frame.pack();

		// Add Listeners
		textField.addActionListener(new ActionListener() {
			/**
			 * Responds to pressing the enter key in the textfield by sending
			 * the contents of the text field to the server. Then clear the text
			 * area in preparation for the next message.
			 */
			public void actionPerformed(ActionEvent e) {
				// out.println(textField.getText()); this is the old, main
				// version that send the message directly to the server
				ch.process(textField.getText());
				textField.setText("");
			}
		});
	}

	/**
	 * Prompt for and return the address of the server.
	 */
	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame,
				"Enter IP Address of the Server:", "Welcome to the Chatroom",
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Prompt for and return the desired screen name.
	 */
	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:",
				"Screen name selection", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Connects to the server then enters the processing loop.
	 */
	private void run() {

		try {

			// Make connection and initialize streams
			String serverAddress = getServerAddress();
			Socket socket = new Socket(serverAddress, 4445);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			// Process all messages from server, according to the protocol.
			String line;

			while (running) {
				line = enc.decrypt(in.readLine(), symKeyHex);
				// line = in.readLine(); //old version
				if (line.startsWith("SUBMITNAME")) {
					out.println(getName());
				} else if (line.startsWith("NAMEACCEPTED")) {
					textField.setEditable(true);
				} else if (line.startsWith("MESSAGE")) {
					messageArea.append(line.substring(8) + "\n");
					messageArea.setCaretPosition(messageArea.getDocument()
							.getLength());
				} else if (line.startsWith("SERVERSHUTDOWN")) {
					closeClient(socket);
				}
			}
			closeClient(socket);

		} catch (IOException e) {
			if (e.getMessage() == "Stream closed") {
				System.out
						.println("The server shut down. (Error: \"Stream closed.\")");
			} else {
				e.printStackTrace();
			}
		}
	}

	public void closeClient(Socket socket) {
		try {
			out.flush();
			out.close();
			in.close();
			socket.close();
			System.exit(0);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: taranis.");
			System.exit(1);
		}
	}

	/**
	 * Runs the client as an application with a closeable frame.
	 */
	public static void main(String[] args) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		Client client = new Client();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}

	/**
	 * This class is supposed to look at the incoming messages and decide if
	 * something needs to be done. For example, This class watches for commands
	 * such as /exit.
	 */
	static protected class ChatHandler {

		public ChatHandler() {

		}

		public void process(String inputmessage) {

			if (inputmessage.startsWith("/")) { // all the different commands go
												// here
				inputmessage = inputmessage.substring(1);

				if (inputmessage.startsWith("exit")) {
					out.println("<DISCONNECT>");
					// out.println(enc.encrypt("<DISCONNECT>", symKeyHex));
					running = false;
				}

				if (inputmessage.startsWith("me")) {
					inputmessage = inputmessage.substring(2).trim();
					out.println("<ME> " + inputmessage);
					// out.println(enc.encrypt("<ME>" + inputmessage,
					// symKeyHex));
				}
				if (inputmessage.startsWith("help")) {
					inputmessage = inputmessage.substring(4).trim();
					out.println("<ME>" + inputmessage);
					// out.println(enc.encrypt("<ME>" + inputmessage,
					// symKeyHex));
				}
			} else { // otherwise we just send the message to the encryption
						// class.

				out.println(inputmessage);
				// out.println(enc.encrypt(inputmessage, symKeyHex));
				// //encyption version

				// out.println(textField.getText()); //older version
			}

		}
	}
}
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

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
			kkSocket = new Socket(host, port);
			out = new PrintWriter(kkSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					kkSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + host);
			System.exit(1);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: " + host);
			System.exit(1);
		}

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
	}
}
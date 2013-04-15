import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

public class KnockKnockClientWindow extends JFrame {

	public KnockKnockClientWindow() {
		initUI();
	}

	public final void initUI() {

		JPanel panel = new JPanel();
		JEditorPane chatview = new JEditorPane();
		JFormattedTextField textfield = new JFormattedTextField();
		BorderLayout bl = new BorderLayout();
		FlowLayout fl = new FlowLayout();

		getContentPane().add(panel);

		chatview.setEditable(false);
		chatview.setEnabled(true);
		chatview.setBounds(0, 0, 400, 250);
		chatview.setText("this is a test");

		textfield.setText("type here");
		textfield.setEditable(true);
		textfield.setBounds(0, 200, 400, 50);

		panel.setLayout(bl);

		final JButton quitButton = new JButton("Quit");
		final JButton notsureButton = new JButton("No");
		final JButton amsureButton = new JButton("Yes");

		quitButton.setBounds(50, 60, 80, 30);
		notsureButton.setBounds(50, 60, 80, 30);
		amsureButton.setBounds(50, 140, 80, 30);

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				quitButton.setVisible(false);
				notsureButton.setVisible(true);
				amsureButton.setVisible(true);
			}
		});

		notsureButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				quitButton.setVisible(true);
				notsureButton.setVisible(false);
				amsureButton.setVisible(false);
			}
		});

		amsureButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		// panel.add(quitButton);
		// panel.add(notsureButton);
		// panel.add(amsureButton);
		panel.add(chatview, BorderLayout.NORTH);
		panel.add(textfield, BorderLayout.SOUTH);

		setTitle("Program");
		setSize(400, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/*
	 * public static void main(String[] args) { SwingUtilities.invokeLater(new
	 * Runnable() { public void run() { Example ex = new Example();
	 * ex.setVisible(true); } }); }
	 */
}
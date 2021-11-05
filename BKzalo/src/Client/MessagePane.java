package Client;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.event.*;
import java.io.IOException;
import java.awt.BorderLayout;

public class MessagePane extends JPanel implements MessageListener {

	private String login;
	private ChatClient client;

	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> messageList = new JList<>(listModel);
	private JTextField inputField = new JTextField();

	public MessagePane(ChatClient client, String login) {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.login = login;

		client.addMessageListener(this);

		setLayout(new BorderLayout());
		add(new JScrollPane(messageList), BorderLayout.CENTER);
		add(inputField, BorderLayout.SOUTH);

		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = inputField.getText();
					client.msg(login, inputField.getText());
					listModel.addElement("you: " + text);
					inputField.setText("");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onMessgae(String fromLogin, String msgContent) {
		if (login.equalsIgnoreCase(fromLogin)) {
			String line = fromLogin + ": " + msgContent;
			listModel.addElement(line);
		}
	}

}

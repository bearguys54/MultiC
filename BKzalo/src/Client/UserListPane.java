package Client;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.*;
import java.io.IOException;


public class UserListPane extends JPanel implements UserStatusListener {
private ChatClient client;
private JList<String> userListUI;
private DefaultListModel<String> userListModel;

public UserListPane(ChatClient client) {
		// TODO Auto-generated constructor stub
	this.client = client;
	this.client.addUserStatusListener(this);
	
	
	userListModel = new DefaultListModel<>();
	userListUI = new JList<>(userListModel);
	setLayout(new BorderLayout());
	add(new JScrollPane(userListUI), BorderLayout.CENTER);
	
	userListUI.addMouseListener(new MouseAdapter(){
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >1) {
				String login = userListUI.getSelectedValue();
				MessagePane messagePane = new MessagePane(client, login);
				
				JFrame f = new JFrame("Message: " +login);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setSize(500 , 500);
				f.getContentPane().add(messagePane, BorderLayout.CENTER);
				f.setVisible(true);
			}
		}
	});
	}

public static void main(String[] args) {
    ChatClient client = new ChatClient("localhost", 7100);
    UserListPane userListPane = new UserListPane(client);
    JFrame frame = new JFrame("User list");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400 , 600);
    
    frame.getContentPane().add(userListPane, BorderLayout.CENTER);
    frame.setVisible(true);

    if (client.connect()) {
        try {
			client.login("a", "a");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

@Override
public void online(String login) {
	// TODO Auto-generated method stub
	userListModel.addElement(login);
}

@Override
public void offline(String login) {
	// TODO Auto-generated method stub
	userListModel.removeElement(login);
}
}

package Client;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
	JTextField loginField = new JTextField();
	JPasswordField passwordField = new JPasswordField();
	JButton loginButton = new JButton("Login");
	private ChatClient client;
	
	public LoginWindow() {
		super("Login");
		
		this.client = new ChatClient("localhost", 7100);
		client.connect();
				
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(loginField);
		p.add(passwordField);
		p.add(loginButton);
		
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				doLogin();
			}

		});
		 
		getContentPane().add(p, BorderLayout.CENTER);
		 
		pack();
		
		setVisible(true);
	}
	private void doLogin() {
		// TODO Auto-generated method stub
		String login = loginField.getText();
		String password = passwordField.getText();
		try {
			if (client.login(login,password)) {
			    UserListPane userListPane = new UserListPane(client);
			    JFrame frame = new JFrame("User list");
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.setSize(400 , 600);
			    
			    frame.getContentPane().add(userListPane, BorderLayout.CENTER);
			    frame.setVisible(true);

				setVisible(false);
				
			}else {
				JOptionPane.showMessageDialog(this, "invalid username/password");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		LoginWindow loginWin = new LoginWindow();
		loginWin.setVisible(true);

		
	}
	
}

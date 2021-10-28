/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author hoang
 */
public class ChatClient {

    private final int serverPort;
    private final String serverName;
    private Socket socket;
	private OutputStream serverOut;
	private InputStream serverIn;
	private BufferedReader bufferIn;

	private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    
    
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 7100);
        client.addUserStatusListener(new UserStatusListener() {
			
			@Override
			public void online(String login) {
				// TODO Auto-generated method stub
				System.out.println("online: "+login);
			}
			
			@Override
			public void offline(String login) {
				// TODO Auto-generated method stub
				System.out.println("offline: "+login);

			}
		});
        if (!client.connect()) {
			System.out.println("conection failed.");
		}else {
			System.out.println("connection successful.");
		}
        if(client.login("a","a")) {
        	System.out.println("logged in sucessful");
        }else {
        	System.out.println("login failed");
        }
        
    }
    
    private boolean login(String login, String password) throws IOException {
		String cmd = "login "+ login + " " + password + "\n";
		System.out.println(cmd);
		serverOut.write(cmd.getBytes());
		String respond =  bufferIn.readLine();
		System.out.println("respond is "+respond);
		if ("loged in".equalsIgnoreCase(respond)) {
			startMessageReader();
			return true;
		}else {
			return false;
		}
		
	}

	private void startMessageReader() {
		// TODO Auto-generated method stub
		System.out.println("starting msgReader");
		Thread t = new Thread() {
			public void run() {
				readMessageLoop();
			}
		};
		t.start();
	}	

	private void readMessageLoop() {
		// TODO Auto-generated method stub
		System.out.println("starting msgloop");
		try {
			String line;
			while ((line=bufferIn.readLine()) != null) {
				System.out.println("linerecored: "+line);
				String[] tokens = StringUtils.split(line);
	            if (tokens != null && tokens.length > 0) {
	                String cmd = tokens[0];
	                if ("online".equalsIgnoreCase(cmd)) {
						handleOnline(tokens);
					}else if ("offline".equalsIgnoreCase(cmd)) {
						handleOffline(tokens);
					}
	            }
			}
		}catch (Exception exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
			try {
				socket.close();
				System.out.println("closed socket due to exception found in messageloop method");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private void handleOffline(String[] tokens) {
		// TODO Auto-generated method stub
		String login = tokens[1];
		for (UserStatusListener listener : userStatusListeners) {
			listener.offline(login);
		}
	}

	private void handleOnline(String[] tokens) {
		// TODO Auto-generated method stub
		String login = tokens[1];
		for (UserStatusListener listener : userStatusListeners) {
			listener.online(login);
		}
	}

	public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }
    
    public void addUserStatusListener (UserStatusListener listener) {
    	userStatusListeners.add(listener);
    }
    public void removeUserStatusListener (UserStatusListener listener) {
    	userStatusListeners.remove(listener);
    }

}

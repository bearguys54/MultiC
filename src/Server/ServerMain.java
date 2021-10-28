package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 7100;
		Server server = new Server(port);		
		server.start();

}


}

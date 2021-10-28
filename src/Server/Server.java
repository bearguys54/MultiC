package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

	private final int serverport;
	private final ArrayList<ServerWorker> workerList = new ArrayList<>();
	
	public Server(int serverport) {
		this.serverport = serverport;
	}
	
	public List<ServerWorker> getWorkerList(){
		return workerList;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverport);
			System.out.println("server has started");
			
			while(true) {
				System.out.println("about to accept client conn");
			Socket clientSocket = serverSocket.accept();
			System.out.println("accepted client conn from: " + clientSocket);
			ServerWorker sWorker = new ServerWorker(this, clientSocket);
			workerList.add(sWorker);
			sWorker.start();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void removeWorker(ServerWorker serverWorker) {
		workerList.remove(serverWorker);
		
	}

}

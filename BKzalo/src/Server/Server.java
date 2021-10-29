package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

	private final int serverport;
	private final ArrayList<ServerWorker> workerList = new ArrayList<>();    // list chua danh sach user online
	
	public Server(int serverport) {
		this.serverport = serverport;
	}
	
	public List<ServerWorker> getWorkerList(){	//lay danh sach user
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
			ServerWorker sWorker = new ServerWorker(this, clientSocket);		//khoi tao mot serverworker cho moi client ket noi vao server
			workerList.add(sWorker);											//them serverworker vuwa tao vao danh sach user dang online
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

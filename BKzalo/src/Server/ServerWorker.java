package Server;


import java.io.BufferedReader;
import org.apache.commons.lang3.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private String login = null;
    private Server server;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();		//danh sach cac groupchat (topic)

    public ServerWorker(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            handleClientSocket();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        //outputStream.write(("conected \n").getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {				//lien tuc doc thong tin gui den tu client de xu ly
            String[] tokens = StringUtils.split(line);				//tach chuoi command client gui den de thuc thi
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);				//dang nhap
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] msgtokens = StringUtils.split(line, null, 3);
                    handleMessage(msgtokens);						//nhan tin
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);								//tham gia group
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);							//leave group
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }

            }
        }

        clientSocket.close();
    }

    private void handleMessage(String[] tokens) throws IOException {
        //cmd format: msg  username  text
        String sendTo = tokens[1];
        String textmsg = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList) {
            if (isTopic) { //send to a group
                if (worker.isMemberOfTopic(sendTo)) {
                    String sentmsg = "msg " + sendTo + " " + login + ":" + textmsg + "\n";
                    worker.send(sentmsg);
                }
            } else {  //direct message
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String sentmsg = "msg " + login + ":" + textmsg + "\n";
                    worker.send(sentmsg);
                }
            }

        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        String offlinemsg = "offline" +" "+login+ "\n";
        for (ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(offlinemsg);
            }
        }
        clientSocket.close();

    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String uname = tokens[1];
            String password = tokens[2];
            if ((uname.equals("a") && password.equals("a")) || (uname.equals("d") && password.equals("d"))) {
                String msg = "loged in\n";
                outputStream.write(msg.getBytes());
                this.login = uname;
                System.out.println("loged in:" + login);
                List<ServerWorker> workerList = server.getWorkerList();

                for (ServerWorker worker : workerList) {		//login thanh cong thi gui danh sach user dang online
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = worker.getLogin() + " is online" + "\n \n";
                            send(msg2);
                        }
                    }
                }

                String onlinemsg ="online" +" "+login+ "\n";
                for (ServerWorker worker : workerList) {		//gui cho tat ca user trang thai online cua minh
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlinemsg);
                    }
                }

            } else {
                String msg = "error log in\n";
                outputStream.write(msg.getBytes());
            }
        }

    }

    private void send(String msg) throws IOException {
        // TODO Auto-generated method stub
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);

        }

    }

    private void handleLeave(String[] tokens) {
                if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);

        }
    }

}

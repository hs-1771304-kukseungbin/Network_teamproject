import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class hc_ChatClient extends JFrame{
	
	protected static hc_ChatClientGUI mainMenu;
	protected static hc_ChatClientRoomListGUI roomList;
	protected static hc_ChatClientRoomGUI roomChat;
	
	protected static int serverPort;
	protected static String serverAddress;
	
	protected static Socket socket;
	protected static ObjectOutputStream out;
	private static Thread receiveThread = null;
	
	
	public hc_ChatClient() {
		this.serverAddress = "localhost";
		this.serverPort = 54321;
	}
	
	//서버 연결 
	public static void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			receiveThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while(receiveThread == Thread.currentThread()) {
						receiveMessages();
					}
				}
			});
			receiveThread.start();
		} catch (UnknownHostException e) {
			System.err.println("알수없는 서버" + e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.err.println("서버 연결 실패");
			System.exit(0);
		}
	}
	
	public static void receiveMessages() {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			ObjectMsg objectMsg;
			while((objectMsg=(ObjectMsg)in.readObject()) != null) {
				if(ObjectMsg.MODE_TX_STRING == objectMsg.mode){
					roomChat.printDisplay(objectMsg);
				}
				else if(ObjectMsg.MODE_TX_FILE == objectMsg.mode) {
					
				}
				else if(ObjectMsg.MODE_TX_IMAGE == objectMsg.mode){
					roomChat.printDisplayImage(objectMsg);
				}
			}
		} catch (IOException e) {
			//printDisplay("읽기 오류" + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void send(ObjectMsg msg) {
		try {
			out.writeObject(msg);
            out.flush();
		}catch (IOException e) {
            System.err.println("클라이언트 일반 전송 오류>" + e.getMessage());
        }
	}
	
	public static void main(String[] args) {
		mainMenu = new hc_ChatClientGUI();
	}
}

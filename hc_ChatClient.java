import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class hc_ChatClient extends JFrame{
	
	protected static hc_ChatClientGUI mainMenu;
	protected static hc_ChatClientRoomListGUI roomList;
	protected static hc_ChatClientRoomGUI roomChat;
	
	protected static int serverPort;
	protected static String serverAddress;
	
	protected static Socket socket;
	protected static ObjectOutputStream out;
	protected static BufferedOutputStream Bos;
	private static Thread receiveThread = null;
	
	public hc_ChatClient() {
		this.serverAddress = "localhost";
		this.serverPort = 50321;
	}
	
	//서버 연결 
	public static void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
			Bos = new BufferedOutputStream(socket.getOutputStream());
			out = new ObjectOutputStream(Bos);
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
	
	protected void disconnect() {
		try {
			receiveThread = null;
			socket.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void receiveMessages() {
		try {
			BufferedInputStream Bin = new BufferedInputStream(socket.getInputStream());
			ObjectInputStream in = new ObjectInputStream(Bin);
			File file = null;
			FileOutputStream fo;
			ObjectMsg objectMsg;
			while((objectMsg=(ObjectMsg)in.readObject()) != null) {
				if(ObjectMsg.MODE_TX_STRING == objectMsg.mode){
					roomChat.printDisplay(objectMsg);
				}
				else if(ObjectMsg.MODE_TX_FILE == objectMsg.mode) {
					long size = objectMsg.fileSize;
					String fileName = objectMsg.message;
					byte[] buffer = new byte[1024];
					int nRead = 0;
					file = new File(fileName);
					fo = new FileOutputStream(file);
					while (size > 0) {
						nRead = Bin.read(buffer);
						size -= nRead;
						fo.write(buffer, 0, nRead);
					}
					fo.close();
					roomChat.printDisplay(objectMsg);
				}
				else if(ObjectMsg.MODE_TX_IMAGE == objectMsg.mode){
					roomChat.printDisplayImage(objectMsg);
				}
				else if(ObjectMsg.MODE_LOGIN == objectMsg.mode) {
					roomList.updateUserListAdd(objectMsg.userName);
				}
				else if(ObjectMsg.MODE_LOGOUT == objectMsg.mode) {
					roomList.updateUserListDelete(objectMsg.userName);
				}
				else if(ObjectMsg.MODE_CREATE_ROOM == objectMsg.mode) {
					roomList.updateRoomAdd(objectMsg.room_name);
				}
				else if(ObjectMsg.MODE_ID_ERROR == objectMsg.mode) {
					//서버에서 관리하는 유저정보중에 중복이되어 아이디중복문제모드로 왔기에 연결을 끊고 해당 유저 이름은 중복된 아이디임을 알려주는 기능.
					roomList.dispose();
					mainMenu.setVisible(true);
					mainMenu.disconnect();
					JOptionPane.showMessageDialog(null, "아이디가 중복되었습니다. 다시 입력해주세요.");
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
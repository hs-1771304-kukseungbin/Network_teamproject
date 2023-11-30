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
	
	//���� ���� 
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
			System.err.println("�˼����� ����" + e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.err.println("���� ���� ����");
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
					//�������� �����ϴ� ���������߿� �ߺ��̵Ǿ� ���̵��ߺ��������� �Ա⿡ ������ ���� �ش� ���� �̸��� �ߺ��� ���̵����� �˷��ִ� ���.
					roomList.dispose();
					mainMenu.setVisible(true);
					mainMenu.disconnect();
					JOptionPane.showMessageDialog(null, "���̵� �ߺ��Ǿ����ϴ�. �ٽ� �Է����ּ���.");
				}
			}
		} catch (IOException e) {
			//printDisplay("�б� ����" + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void send(ObjectMsg msg) {
		try {
			out.writeObject(msg);
            out.flush();
		}catch (IOException e) {
            System.err.println("Ŭ���̾�Ʈ �Ϲ� ���� ����>" + e.getMessage());
        }
	}
	
	public static void main(String[] args) {
		mainMenu = new hc_ChatClientGUI();
	}
}
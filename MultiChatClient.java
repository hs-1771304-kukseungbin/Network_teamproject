import javax.swing.JFrame;

public class MultiChatClient extends JFrame{
	
	protected static MultiChatClientGUI mainMenu;
	//protected static MultiChatClientRoomListGUI roomList;
	//protected static MultiChatClientRoomGUI roomChat;
	
	protected int serverPort;
	protected String serverAddress;
	protected String userId;
	
	
	public MultiChatClient() {
		this.serverAddress = "localhost";
		this.serverPort = 54321;
	}
	
	public static void main(String[] args) {
		mainMenu = new MultiChatClientGUI();
	}
}

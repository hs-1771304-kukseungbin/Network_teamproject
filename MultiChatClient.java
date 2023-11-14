import javax.swing.JFrame;

public class MultiChatClient extends JFrame{
	
	protected static MultiChatClientGUI mainMenu;
	
	protected int serverPort;
	protected String serverAddress;
	
	
	public MultiChatClient() {
		this.serverAddress = "localhost";
		this.serverPort = 54321;
	}
	
	
	public static void main(String[] args) {
		mainMenu = new MultiChatClientGUI();
	}
}

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class hc_ChatClient extends MultiChatClient {
	
	private JTextArea t_user, t_roomArea, t_roomText, t_roomTitle;
	private JButton b_add;

	public hc_ChatClient() {
		this.setTitle("Hansung Talk");
		
		buildGUI();
		
		setSize(300,500);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	private void buildGUI() {
		add(createUserPanel(), BorderLayout.WEST);
		add(createRoomListPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createUserPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		
//		t_user = new JTextArea();
//		t_user.setEnabled(false);
		
		//유저가 추가되거나 삭제되는 시점에 화면이 재생성되도록 해야함
		
//		p.add(new JScrollPane(t_user), BorderLayout.CENTER);
	
		return p;
	}
	
	private JPanel createRoomListPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
//		t_roomArea = new JTextArea();
//		t_roomArea.setEnabled(false);
		
		b_add = new JButton("+");
		//방이 생성되는 시점이나 없어지는 시점에 화면에 RoomPanel을 추가해야함
//		p.add(new JScrollPane(t_roomArea), BorderLayout.CENTER);
		p.add(b_add, BorderLayout.SOUTH);
		
		return p;
	}
	
	private JPanel createRoomPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		
		return p;
	}
	
	public static void main(String[] args) {
		hc_ChatClient client = new hc_ChatClient();
	}
}

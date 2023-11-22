import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class hc_ChatClientRoomListGUI extends hc_ChatClient {
	
	protected JTextArea t_user, t_roomArea, t_roomText, t_roomTitle;
	private JButton b_add, b_disconnect;
	protected Vector<String> users;

	public hc_ChatClientRoomListGUI() {
		this.setTitle("Hansung Talk");
		users = new Vector<>();
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
		JPanel p = new JPanel(new CardLayout());
		
		t_user = new JTextArea();
		t_user.setText("-- 유저 목록 -- \n");
		t_user.setEnabled(false);
		
		//유저가 추가되거나 삭제되는 시점에 화면이 재생성되도록 해야함
		p.add(new JScrollPane(t_user));
	
		return p;
	}
	
	public void updateUserListAdd(String user) {
		//유저 중복 불가능하게 처리
		if(users.size() == 0) users.add(user);
		for(int i = 0; i<users.size(); i++) {
			if(!user.equals(users.get(i))) users.add(user);
		}
		
		t_user.setText("-- 유저 목록 -- \n");
		for(int i = 0; i<users.size(); i++) {
			t_user.append( "  " + users.get(i) + "\n");
		}
	}
	
	public void updateUserListDelete(String user) {
		for(int i = 0; i<users.size(); i++) {
			if(user.equals(users.get(i))) {
				users.remove(i);
			}
		}
		t_user.setText("-- 유저 목록 -- \n");
		for(int i = 0; i<users.size(); i++) {
			t_user.append( "  " + users.get(i) + "\n");
		}
	}
	
	private JPanel createRoomListPanel() {
		JPanel p = new JPanel(new BorderLayout());
		JPanel b_p = new JPanel(new GridLayout(0,2));
//		t_roomArea = new JTextArea();
//		t_roomArea.setEnabled(false);
		
		b_add = new JButton("+");
		b_disconnect = new JButton("접속종료");
		b_disconnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				roomList.dispose();
				mainMenu.setVisible(true);
				send(new ObjectMsg(ObjectMsg.MODE_LOGOUT,mainMenu.userId));
				mainMenu.disconnect();
			}
		});
		//방 만들기 모드를 서버에 보내고 해당 방에 접속하기
		b_add.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				String title = JOptionPane.showInputDialog("방이름 설정");
				send(new ObjectMsg(ObjectMsg.MODE_CREATE_ROOM, mainMenu.userId, null, title));
				send(new ObjectMsg(ObjectMsg.MODE_JOIN_ROOM, mainMenu.userId, null, title));
				roomList.setVisible(false);
				roomChat = new hc_ChatClientRoomGUI(title);
			}
		});
		//방이 생성되는 시점이나 없어지는 시점에 화면에 RoomPanel을 추가해야함
//		p.add(new JScrollPane(t_roomArea), BorderLayout.CENTER);
		b_p.add(b_add);
		b_p.add(b_disconnect);
		p.add(b_p, BorderLayout.SOUTH);
		
		return p;
	}
	
	private JPanel createRoomPanel() {
		JPanel p = new JPanel(new BorderLayout());
		return p;
	}
	
}
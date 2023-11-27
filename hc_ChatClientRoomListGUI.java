import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class hc_ChatClientRoomListGUI extends hc_ChatClient {
	
	protected JTextArea t_user, t_roomText, t_roomTitle;
	protected JPanel roomPanel;
	private JButton b_add, b_disconnect;
	private JList<String> rooms;
	protected Vector<String> users;
	protected DefaultListModel<String> roomName;

	public hc_ChatClientRoomListGUI() {
		this.setTitle("Hansung Talk");
		users = new Vector<>();
		roomName = new DefaultListModel<>();
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
		boolean isUser = true;
		if(users.size() == 0) users.add(user);
		for(int i = 0; i<users.size(); i++) {
			if(users.get(i).equals(user)) {
				isUser = false;
				break;
			}
		}
		if(isUser) {
			users.add(user);
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
	
	public void updateRoomAdd(String room_Name) {
		roomName.addElement(room_Name);
	}
	
	public void updateRoomDelete(String room_Name) {
		roomName.removeElement(room_Name);
	}
	
	private JPanel createRoomListPanel() {
		JPanel p = new JPanel(new BorderLayout());
		JPanel b_p = new JPanel(new GridLayout(0,2));
		
		// 해당 클라이언트 접속 종료버튼 추가
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
		b_add = new JButton("+");
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
		// 해당 방이 만들어 질 때마다 해당 방 영역을 생성해야함
		roomPanel = new JPanel();
		rooms = new JList<String>(roomName);
		roomPanel.add(rooms);
		
		rooms.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();
                if (evt.getClickCount() == 2) {
                	 int index = list.locationToIndex(evt.getPoint());
                     if (index >= 0) {
                    	 String item = list.getModel().getElementAt(index);
                         send(new ObjectMsg(ObjectMsg.MODE_JOIN_ROOM, mainMenu.userId, null, item));
                         roomList.setVisible(false);
         				 roomChat = new hc_ChatClientRoomGUI(item);
                     }
                }
            }
        });
		
		p.add(new JScrollPane(roomPanel), BorderLayout.CENTER);
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
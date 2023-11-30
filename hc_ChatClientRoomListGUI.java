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
		setSize(300, 500);
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

		// 유저가 추가되거나 삭제되는 시점에 화면이 재생성되도록 해야함
		p.add(new JScrollPane(t_user));

		return p;
	}

	public void updateUserListAdd(String user) {

		//처음 유저정보가 아무것도 없으면 무조건 추가.
		if (users.size() == 0)
			users.add(user);
		//서버로 부터 받은 유저 정보중 하나라도 같은 이름의 유저가 존재할 경우 유저를 추가하지 않음.
		boolean isUser = true;
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).equals(user)) {
				isUser = false;
				break;
			}
		}
		if (isUser) {
			users.add(user);
		}
		//추가된 모든 유저 정보들을 출력.
		t_user.setText("-- 유저 목록 -- \n");
		for (int i = 0; i < users.size(); i++) {
			t_user.append("  " + users.get(i) + "\n");
		}
	}

	public void updateUserListDelete(String user) {
		//받은 유저 정보중에 같은 이름이 있을 경우 해당 유저 삭제.
		for (int i = 0; i < users.size(); i++) {
			if (user.equals(users.get(i))) {
				users.remove(i);
			}
		}
		t_user.setText("-- 유저 목록 -- \n");
		for (int i = 0; i < users.size(); i++) {
			t_user.append("  " + users.get(i) + "\n");
		}
	}

	public void updateRoomAdd(String room_Name) {
		//JList에 서버에서 받은 방 이름을 추가.
		roomName.addElement(room_Name);
	}

	private JPanel createRoomListPanel() {
		JPanel p = new JPanel(new BorderLayout());
		JPanel b_p = new JPanel(new GridLayout(0, 2));

		// 해당 클라이언트 접속 종료버튼 추가
		b_disconnect = new JButton("접속종료");
		b_disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				roomList.dispose();
				mainMenu.setVisible(true);
				send(new ObjectMsg(ObjectMsg.MODE_LOGOUT, mainMenu.userId));
				mainMenu.disconnect();
			}
		});
		// 방 만들기 모드를 서버에 보내고 해당 방에 접속하기
		b_add = new JButton("+");
		b_add.addActionListener(new ActionListener() {
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
		// 방이 만들어지면 해당 방을 마우스로 2번 클릭한 경우 해당 방제목을 가져와 해당방에 접속했다는 정보를 방접속모드로 서버에 보냄.
		rooms.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				// 이벤트(evt)에서 소스를 가져와서(JList<String>) 형변환하여 list라는 이름의 변수에 할당.
				JList<String> list = (JList<String>) evt.getSource();
				// evt.getClickCount()를 사용하여 클릭 횟수를 확인하고, 더블 클릭(클릭 횟수가 2일 때)을 확인하는 if문.
				if (evt.getClickCount() == 2) {
					//마우스 클릭 지점에 해당하는 목록의 인덱스를 가져옴.
					int index = list.locationToIndex(evt.getPoint());
					//가져온 인덱스가 유효한 경우(index >= 0), 해당 인덱스의 항목을 가져와서 item 변수에 저장하여 item에 해당하는 메세지를 서버에 전송.
					//여기서 item은 결국 해당 마우스 클릭 지점의 방이름임.
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
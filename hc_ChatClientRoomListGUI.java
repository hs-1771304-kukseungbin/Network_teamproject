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
		t_user.setText("-- ���� ��� -- \n");
		t_user.setEnabled(false);
		
		//������ �߰��ǰų� �����Ǵ� ������ ȭ���� ������ǵ��� �ؾ���
		p.add(new JScrollPane(t_user));
	
		return p;
	}
	
	public void updateUserListAdd(String user) {
		//���� �ߺ� �Ұ����ϰ� ó��
		if(users.size() == 0) users.add(user);
		for(int i = 0; i<users.size(); i++) {
			if(!user.equals(users.get(i))) users.add(user);
		}
		
		t_user.setText("-- ���� ��� -- \n");
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
		t_user.setText("-- ���� ��� -- \n");
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
		b_disconnect = new JButton("��������");
		b_disconnect.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				roomList.dispose();
				mainMenu.setVisible(true);
				send(new ObjectMsg(ObjectMsg.MODE_LOGOUT,mainMenu.userId));
				mainMenu.disconnect();
			}
		});
		//�� ����� ��带 ������ ������ �ش� �濡 �����ϱ�
		b_add.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				String title = JOptionPane.showInputDialog("���̸� ����");
				send(new ObjectMsg(ObjectMsg.MODE_CREATE_ROOM, mainMenu.userId, null, title));
				send(new ObjectMsg(ObjectMsg.MODE_JOIN_ROOM, mainMenu.userId, null, title));
				roomList.setVisible(false);
				roomChat = new hc_ChatClientRoomGUI(title);
			}
		});
		//���� �����Ǵ� �����̳� �������� ������ ȭ�鿡 RoomPanel�� �߰��ؾ���
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
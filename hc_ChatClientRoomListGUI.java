import java.awt.BorderLayout;
import java.awt.CardLayout;
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
	private JButton b_add;

	public hc_ChatClientRoomListGUI() {
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
		JPanel p = new JPanel(new CardLayout());
		
		t_user = new JTextArea();
		t_user.setText("-- ���� ��� -- \n");
		t_user.setEnabled(false);
		
		//������ �߰��ǰų� �����Ǵ� ������ ȭ���� ������ǵ��� �ؾ���
		p.add(new JScrollPane(t_user));
	
		return p;
	}
	
	public void updateUserList(Vector<String> users) {
		t_user.setText("-- ���� ��� -- \n");
		for(int i = 0; i<users.size(); i++) {
			t_user.append( "  " + users.get(i) + "\n");
		}
	}
	
	private JPanel createRoomListPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
//		t_roomArea = new JTextArea();
//		t_roomArea.setEnabled(false);
		
		b_add = new JButton("+");
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
		p.add(b_add, BorderLayout.SOUTH);
		
		return p;
	}
	
	private JPanel createRoomPanel() {
		JPanel p = new JPanel(new BorderLayout());
		return p;
	}
	
}
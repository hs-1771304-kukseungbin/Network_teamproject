import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class hc_ChatClientGUI extends hc_ChatClient{
	
	private JButton b_connect, b_exit;
	private JTextField t_userID, t_hostAddr, t_portNum;
	private JLabel main_Title;
	private JLabel userID;
	private JLabel hostAddr;
	private JLabel portNum;
	public String userId;
	
	public hc_ChatClientGUI() {
		this.setTitle("Hansung Talk");
		
		buildGUI();
		
		setSize(350,500);
		setLocation(400,300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	private void buildGUI() {
		add(createMainIcon(), BorderLayout.CENTER);
		add(createInputInfo(), BorderLayout.SOUTH);
	}
	
	private JPanel createMainIcon() {
		JPanel p = new JPanel(new BorderLayout());
		main_Title = new JLabel("Hansung Talk");
		main_Title.setFont(main_Title.getFont().deriveFont(15.0f));
		main_Title.setHorizontalAlignment(JLabel.CENTER); //가운데 정렬
		p.add(main_Title, BorderLayout.SOUTH);
		
		ImagePanel mainIcon = new ImagePanel();
		p.add(mainIcon, BorderLayout.CENTER);
		return p;
	}
	
	private JPanel createInputInfo() {
		JPanel p = new JPanel(new GridLayout(4,2));
		hostAddr = new JLabel("서버주소 : ");
		portNum = new JLabel("포트번호 : ");
		userID = new JLabel("아이디 : ");
		hostAddr.setHorizontalAlignment(JLabel.CENTER);
		portNum.setHorizontalAlignment(JLabel.CENTER);
		userID.setHorizontalAlignment(JLabel.CENTER);
		
		t_portNum = new JTextField(); t_portNum.setText(serverPort + "");
		t_hostAddr = new JTextField(); t_hostAddr.setText(serverAddress);
		t_userID = new JTextField();
		
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				mainMenu.setVisible(false);
				userId = t_userID.getText();
				send(new ObjectMsg(ObjectMsg.MODE_LOGIN, userId));
				//서버와 연결 및 다음 방 리스트화면으로 넘어가기
				roomList = new hc_ChatClientRoomListGUI();
			}
		});
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		p.add(hostAddr); p.add(t_hostAddr);
		p.add(portNum); p.add(t_portNum);
		p.add(userID); p.add(t_userID);
		p.add(b_connect); p.add(b_exit);
		return p;
	}
}

class ImagePanel extends JPanel {
	private Image MainImageIcon = new ImageIcon("image/Main_Icon.png").getImage();

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(MainImageIcon, 0, 0, getWidth(), getHeight(), this);
	}
}

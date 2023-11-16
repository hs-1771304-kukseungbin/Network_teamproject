import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class hc_Chatroom extends MultiChatClient{
	
	private JTextArea t_display;
	private JTextField t_input;
	private JLabel l_hanbuk;
	private JLabel l_title;
	private ImageIcon i_hanbuk;
	private JButton b_back;
	private JButton b_send;
	private JButton b_sendResoruce;
	
	private String title = "ROOM 1";
	

	public hc_Chatroom(int port) {
		this.setTitle("Hansung Talk");
		
		buildGUI();
		
		setSize(300,500);
		setLocation(400,0);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	private void buildGUI() {
		add(createTitlePanel(), BorderLayout.NORTH);
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createInputPanel(),BorderLayout.SOUTH);
	}
	
	private JPanel createDisplayPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		t_display = new JTextArea();
		t_display.setEditable(false);
		t_display.setBackground(new Color(135,206,235));
		
		p.add(new JScrollPane(t_display),BorderLayout.CENTER);
		return p;
	}
	
	private JPanel createTitlePanel() {
		JPanel p = new JPanel(new BorderLayout());
		i_hanbuk = new ImageIcon("image/sangbuk.jpg");
		Image img = i_hanbuk.getImage();
		Image resizedImage = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(resizedImage);
		l_hanbuk = new JLabel(resizedIcon);
		
		l_title = new JLabel();
		l_title.setText(title);
		l_title.setHorizontalAlignment(JLabel.CENTER);
		l_title.setOpaque(true);
		l_title.setBackground(new Color(0,123,255));
		l_title.setForeground(Color.white);
		
		b_back = new JButton("◀");
		
		p.add(l_hanbuk, BorderLayout.WEST);
		p.add(l_title, BorderLayout.CENTER);
		p.add(b_back, BorderLayout.EAST);
		
		return p;
		
	}
	
	private JPanel createInputPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		t_input = new JTextField(30);
		
		b_sendResoruce = new JButton("+");
		
		b_send = new JButton("보내기");
		
		p.add(b_sendResoruce, BorderLayout.WEST);
		p.add(t_input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);
		
		return p;
	}
	
//	private JPanel ImoticonPanel() {
//		
//	}
	
	public static void main(String[] args) {
		new hc_Chatroom(12345);
	}
}

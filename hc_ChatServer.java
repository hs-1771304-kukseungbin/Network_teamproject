import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class hc_ChatServer extends MultiChatClient {
	private JTextArea t_display;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	
	
	public hc_ChatServer() {
		this.setTitle("Hansung Talk Server");
		buildGUI();
		
		setSize(400,300);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
	}
	
	private void buildGUI() {
		// TODO Auto-generated method stub
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createControllPanel(), BorderLayout.SOUTH);

	}
	
	private JPanel createDisplayPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		t_display = new JTextArea();
		t_display.setEnabled(false);
		
		p.add(new JScrollPane(t_display), BorderLayout.CENTER);
		
		return p;
	}
	
	private JPanel createControllPanel() {
		JPanel p = new JPanel(new GridLayout(0,3));
		b_connect = new JButton("서버 시작");
		b_disconnect = new JButton("서버 종료");
		b_exit = new JButton("종료");
		
		p.add(b_connect);
		p.add(b_disconnect);
		p.add(b_exit);
		
		return p;
	}
	
	public static void main(String[] args) {
		hc_ChatServer server = new hc_ChatServer();
	}
}

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class hc_ChatClientRoomGUI extends hc_ChatClient{
	
	private JTextPane t_display;
	private JTextField t_input;
	private JLabel l_hanbuk;
	private JLabel l_title;
	private ImageIcon i_hanbuk;
	private JButton b_back;
	private JButton b_send;
	private JButton b_sendResource;
	private String title;
	private DefaultStyledDocument document;
	

	public hc_ChatClientRoomGUI(String title) {
		this.setTitle("Hansung Talk");
		this.title = title;
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

		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		t_display.setBackground(new Color(135,206,235));
		t_display.setEditable(false);
		
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
		b_back.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				//접속한 방 나가기 모드를 서버에 전송
				send(new ObjectMsg(ObjectMsg.MODE_OUT_ROOM, mainMenu.userId));
				roomChat.dispose();
				roomList.setVisible(true);;
			}
		});
		
		p.add(l_hanbuk, BorderLayout.WEST);
		p.add(l_title, BorderLayout.CENTER);
		p.add(b_back, BorderLayout.EAST);
		
		return p;	
	}
	
	private JPanel createInputPanel() {
		JPanel p = new JPanel(new BorderLayout());
		
		t_input = new JTextField(30);
		
		b_sendResource = new JButton("+");
		
		b_send = new JButton("보내기");
		b_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = t_input.getText();
				if(message.isEmpty()) return;
				send(new ObjectMsg(ObjectMsg.MODE_TX_STRING, mainMenu.userId, message));
				t_input.setText("");
			}
		});
		
		p.add(b_sendResource, BorderLayout.WEST);
		p.add(t_input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);
		
		return p;
	}
	
	public void printDisplay(ObjectMsg msg) {
	    document = (DefaultStyledDocument) t_display.getStyledDocument();
	    int len = document.getLength();
	    
	    try {
	        SimpleAttributeSet attrs = new SimpleAttributeSet();
	        //자기 자신이 보내는 메세지이면 왼쪽 정렬하여 출력
	        if (mainMenu.userId.equals(msg.userName)) {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, msg.userName + ": " + msg.message + "\n", null);
	        }
	        //자기 자신이 아닌 다른 유저가 보낸 메세지이면 오른쪽으로 정렬하여 출력
	        else {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_RIGHT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, msg.message + " :" + msg.userName + "\n", null);
	        }

	        t_display.setCaretPosition(document.getLength());
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	}
	
	public void printDisplayImage(ObjectMsg msg) {
		t_display.setCaretPosition(t_display.getDocument().getLength());
		ImageIcon icon = msg.Image;
		if(icon.getIconWidth() > 300) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
		}
		
		document = (DefaultStyledDocument) t_display.getStyledDocument();
	    int len = document.getLength();
	    
	    try {
	        SimpleAttributeSet attrs = new SimpleAttributeSet();
	        //자기 자신이 보내는 이모티콘이면 왼쪽 정렬하여 출력
	        if (mainMenu.userId.equals(msg.userName)) {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, msg.userName + ": " + "\n", null);
	            t_display.insertIcon(icon);
	        }
	        //자기 자신이 아닌 다른 유저가 보낸 이모티콘이면 오른쪽으로 정렬하여 출력
	        else {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_RIGHT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, " :" + msg.userName + "\n", null);
	            t_display.insertIcon(icon);
	        }

	        t_display.setCaretPosition(document.getLength());
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	}
}
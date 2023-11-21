import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class hc_ChatClientRoomGUI extends hc_ChatClient{
	
	private JTextPane t_display;
	private JTextField t_input;
	private JLabel l_hanbuk;
	private JLabel l_title;
	private ImageIcon i_hanbuk;
	private JButton b_back;
	private JButton b_send;
	private JButton b_sendImageIcon;
	private String title;
	private DefaultStyledDocument document;
	private SelectImoticon selectImageGUI;
	

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
		
		b_back = new JButton("��");
		b_back.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				//������ �� ������ ��带 ������ ����
				send(new ObjectMsg(ObjectMsg.MODE_OUT_ROOM, mainMenu.userId));
				roomChat.dispose();
				if(selectImageGUI != null) selectImageGUI.dispose();
				roomList.setVisible(true);
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
		t_input.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = t_input.getText();
				if(message.isEmpty()) return;
				send(new ObjectMsg(ObjectMsg.MODE_TX_STRING, mainMenu.userId, message));
				t_input.setText("");
			}
		});
		
		b_sendImageIcon = new JButton("@");
		b_sendImageIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Point location = roomChat.getLocation();
		        int x = (int) location.getX();
		        int y = (int) location.getY();
				ImageIcon image = new ImageIcon();
				selectImageGUI = new SelectImoticon(x,y);
				//send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, image));
			}
		});
		
		b_send = new JButton("������");
		b_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = t_input.getText();
				if(message.isEmpty()) return;
				send(new ObjectMsg(ObjectMsg.MODE_TX_STRING, mainMenu.userId, message));
				t_input.setText("");
			}
		});
		
		p.add(b_sendImageIcon, BorderLayout.WEST);
		p.add(t_input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);
		
		return p;
	}
	
	public void printDisplay(ObjectMsg msg) {
	    document = (DefaultStyledDocument) t_display.getStyledDocument();
	    int len = document.getLength();
	    
	    try {
	        SimpleAttributeSet attrs = new SimpleAttributeSet();
	        //�ڱ� �ڽ��� ������ �޼����̸� ���� �����Ͽ� ���
	        if (mainMenu.userId.equals(msg.userName)) {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, msg.userName + ": " + msg.message + "\n", null);
	        }
	        //�ڱ� �ڽ��� �ƴ� �ٸ� ������ ���� �޼����̸� ���������� �����Ͽ� ���
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
		document = (DefaultStyledDocument) t_display.getStyledDocument();
	    int len = document.getLength();
	    SimpleAttributeSet attrs = new SimpleAttributeSet();
	    try {
	        //�ڱ� �ڽ��� ������ �̸�Ƽ���̸� ���� �����Ͽ� ���
	        if (mainMenu.userId.equals(msg.userName)) {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, "\n", null);
	            t_display.insertIcon(msg.Image);
	            document.insertString(len, msg.userName + ": " + "\n", null);
	        }
	        //�ڱ� �ڽ��� �ƴ� �ٸ� ������ ���� �̸�Ƽ���̸� ���������� �����Ͽ� ���
	        else {
	            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_RIGHT);
	            t_display.setParagraphAttributes(attrs, true);
	            document.insertString(len, "\n", null);
	            t_display.insertIcon(msg.Image);
	            document.insertString(len, " :" + msg.userName + "\n", null);
	        }
	        t_display.setCaretPosition(document.getLength());
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	}
	
	class SelectImoticon extends JFrame{
		private int x;
		private int y;
		
		private JButton b_1, b_2, b_3, b_4, b_5, b_6, b_exit;
		private ImageIcon i_1, i_2, i_3, i_4, i_5, i_6;
		
		public SelectImoticon(int x, int y) {
			this.x = x;
			this.y = y;
			setSize(200,280);
			setLocation(x, y+500);
			
			setUndecorated(true);
			setVisible(true);
			buildGUI();
		}
		
		private void buildGUI() {
			add(createSelectImageIcon(),BorderLayout.CENTER);
			add(createButton(),BorderLayout.SOUTH);
		}
		
		private JPanel createSelectImageIcon() {
			JPanel p = new JPanel(new GridLayout(3,3));
			
			i_1 = new ImageIcon("image/1.png"); b_1 = new JButton(i_1);
			i_2 = new ImageIcon("image/2.png"); b_2 = new JButton(i_2);
			i_3 = new ImageIcon("image/3.png"); b_3 = new JButton(i_3);
			i_4 = new ImageIcon("image/4.png"); b_4 = new JButton(i_4);
			i_5 = new ImageIcon("image/5.png"); b_5 = new JButton(i_5);
			i_6 = new ImageIcon("image/6.png"); b_6 = new JButton(i_6);
			
			
			b_1.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_1));
				}
			});
			
			b_2.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_2));
				}
			});
			
			b_3.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_3));
				}
			});
			
			b_4.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_4));
				}
			});
			
			b_5.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_5));
				}
			});
			
			b_6.addActionListener(new ActionListener () {
				@Override
				public void actionPerformed(ActionEvent e) {
					send(new ObjectMsg(ObjectMsg.MODE_TX_IMAGE, mainMenu.userId, null, i_6));
				}
			});
			
			p.add(b_1); p.add(b_2); p.add(b_3);
			p.add(b_4); p.add(b_5); p.add(b_6);
			
			return p;
		}
		
		
		private JPanel createButton() {
			JPanel p = new JPanel(new GridLayout(0,1));
			b_exit = new JButton("������");
			b_exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			p.add(b_exit);
			return p;
		}
	}
}


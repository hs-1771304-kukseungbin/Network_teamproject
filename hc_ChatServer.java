import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class hc_ChatServer extends JFrame {
	private JTextArea t_display;
	private JButton b_connect;
	private JButton b_disconnect;
	private JButton b_exit;
	private Vector<ServerUserManager> userInfo;
	private Vector<ServerRoomManager> roomInfo;
	
	private int port;
	private ServerSocket serverSocket = null;
	
	Thread acceptThread = null;
	
	private ObjectInputStream is;
	private ObjectOutputStream os;
	int mode;
	ImageIcon image;
	ObjectMsg objectMsg;
	
	String message;
	
	
//	로그인 + 로그아웃
	public hc_ChatServer() {
		this.setTitle("Hansung Talk Server");
		buildGUI();
		
		setSize(400,300);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
		
		userInfo = new Vector<>();
		roomInfo = new Vector<>();
		
	}
	
	private void buildGUI() {
		// TODO Auto-generated method stub
		add(createDisplayPanel(), BorderLayout.CENTER);
		add(createControllPanel(), BorderLayout.SOUTH);

		t_display.setForeground(Color.black);
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
		b_connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				acceptThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						startServer();
					}
				});
				acceptThread.start();
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				b_exit.setEnabled(false);
			}
		});
		
		b_disconnect = new JButton("서버 종료");
		b_disconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				disconnect();
				
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				b_exit.setEnabled(true);
			}
		});
		
		b_exit = new JButton("종료");
		b_exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				if(serverSocket != null) {
						try {
							serverSocket.close();
							System.exit(0);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}
		});
		
		p.add(b_connect);
		p.add(b_disconnect);
		p.add(b_exit);
		
		
		
		return p;
	}
	
	private void startServer() {
		Socket clientSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			t_display.append("서버가 시작되었습니다. \n");
			
			
			while(acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				t_display.append("클라이언트가 연결되었습니다. \n");
				ServerUserManager um = new ServerUserManager(clientSocket);
				userInfo.add(um);
				um.start();

			}
		} catch(SocketException e) {
			t_display.append("서버가 종료되었습니다.\n");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
				try {
					if (serverSocket != null)serverSocket.close();
					if (clientSocket != null)clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	private void disconnect() {
		try {
			acceptThread = null;
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("서버 닫기 오류 >> "+e.getMessage());
			System.exit(-1);
		}
	}
	
	class ServerUserManager extends Thread {
		private Socket clientSocket;
		private ObjectOutputStream out;
		private String uid;
		
		public ServerUserManager(Socket clientSocket) {
			// TODO Auto-generated constructor stub
			this.clientSocket = clientSocket;
		}
		
		private void receiveMessages(Socket cs) {
			try {
			
			ObjectInputStream in;
				in = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
				ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
				
				String message;
				ObjectMsg msg;
				
				while((msg = (ObjectMsg)in.readObject()) != null) {
					if(msg.mode == ObjectMsg.MODE_LOGIN) {
						uid = msg.userName;
						
						t_display.append("로그인 : "+uid);
						t_display.append("현재 참가자 수 : "+userInfo.size());
						continue;
					}
					else if(msg.mode == ObjectMsg.MODE_LOGOUT) {
						break;
					}
//				else if(msg.mode == ObjectMsg.MODE_ID_ERROR) {
//					t_display.append("ID 중복");
//					
//					break;
//				}
					else if(msg.mode == ObjectMsg.MODE_TX_STRING) {
						broadCasting(msg);
					}
					else if(msg.mode == ObjectMsg.MODE_TX_IMAGE) {
						broadCasting(msg);
					}
					else if(msg.mode == ObjectMsg.MODE_TX_FILE) {
						broadCasting(msg);
					}
			}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		
		private void send(ObjectMsg msg) {
			try {
				out.writeObject(msg);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void broadCasting(ObjectMsg msg) {
			for(ServerUserManager um : userInfo) {
				um.send(msg);
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			receiveMessages(clientSocket);
		}
	}
	
	
	
	public static void main(String[] args) {
		hc_ChatServer server = new hc_ChatServer();
	}
}

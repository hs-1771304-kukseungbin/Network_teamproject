import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
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
import javax.swing.JTextField;

public class Test_Server extends JFrame{
	private int port;
	private ServerSocket serverSocket = null;
	private JTextArea t_display;
	private JButton b_exit;
	private JButton b_connect;
	private JButton b_disconnect;
	private Thread acceptThread = null;
	private Vector<ClientHandler> users;
	int count = 0;
	
	
	public Test_Server(int port) {
		super("hc_ChatServer");
		buildGUI();
		
		setSize(400,300);
		setLocation(800,300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
		this.port = port;
	}
	
	private void buildGUI() {
		add(createDisplayPanel(),BorderLayout.CENTER);
		
		JPanel p_input = new JPanel(new GridLayout(1,0));
		p_input.add(createControlPanel());
		add(p_input,BorderLayout.SOUTH);
	}
	
	private JPanel createDisplayPanel() {
		JPanel p = new JPanel(new BorderLayout());
		t_display = new JTextArea();
		t_display.setEditable(false);
		p.add(new JScrollPane(t_display),BorderLayout.CENTER);
		return p;
	}
	
	private JPanel createControlPanel() {
		JPanel p = new JPanel(new GridLayout(0,3));
		b_connect = new JButton("서버 시작");
		b_connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				acceptThread = new Thread(new Runnable() {

					@Override
					public void run() {
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
				disconnect();
			}
			
		});
		
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		
		p.add(b_connect);
		p.add(b_disconnect);
		p.add(b_exit);
		
		b_disconnect.setEnabled(false);
		return p;
	}
	
	private void disconnect() {
		try {
			acceptThread = null;
			serverSocket.close();
			b_connect.setEnabled(true);
			b_disconnect.setEnabled(false);
			b_exit.setEnabled(true);
		}catch(IOException e) {
			System.err.println("서버 소캣 닫기 오류>" + e.getMessage());
			System.exit(-1);
		}
	}
	
	public void startServer() {
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			t_display.append("서버가 시작되었습니다.\n");
			users = new Vector<ClientHandler>();
			while (acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				t_display.append("클라이언트가 연결되었습니다.\n");
				ClientHandler cHandler = new ClientHandler(clientSocket);
				
				cHandler.start();
				users.add(cHandler);
			}
		} catch (SocketException e) {
			printDisplay("서버 소캣 종료");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(clientSocket != null) clientSocket.close();
				if(serverSocket != null) serverSocket.close();
			} catch (IOException e) {
				System.err.println("서버 닫기 오류> " + e.getMessage());
				System.exit(-1);
			}
		}
	}
	
	
	private class ClientHandler extends Thread{
		private Socket clientSocket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private BufferedInputStream Bin;
		private BufferedOutputStream Bout;
		private ObjectMsg chatMsg;
		
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		void receiveMessages(Socket cs) {	
			try {
				Bin = new BufferedInputStream(cs.getInputStream());
				Bout = new BufferedOutputStream(cs.getOutputStream());
				in = new ObjectInputStream(Bin);
				out = new ObjectOutputStream(Bout);
				
				while((chatMsg = (ObjectMsg)((ObjectInputStream) in).readObject()) != null) {
					if(ObjectMsg.MODE_LOGIN == chatMsg.mode) {
						printDisplay(chatMsg.userName + "연결 성공\n");
						for(int i = 0; i<users.size(); i++) {
							broadcasting(users.get(i).chatMsg);
						}
					}
					else if(ObjectMsg.MODE_LOGOUT == chatMsg.mode) {
						printDisplay(chatMsg.userName + "연결 해제\n");
						broadcasting(chatMsg);
						break;
					}
					else if(ObjectMsg.MODE_TX_STRING == chatMsg.mode){
						printDisplay(chatMsg.userName + ": " + chatMsg.message);
						broadcasting(chatMsg);
					}
					else if(ObjectMsg.MODE_TX_FILE == chatMsg.mode) {
						String filename = chatMsg.message;
						File file = new File(filename);
						if(!file.exists()) {
							System.out.println(">> 파일이 존재하지 않습니다: " + filename + "\n");
							return;
						}

						BufferedInputStream bis = null;
						try {
							
							bis = new BufferedInputStream(new FileInputStream(file));
							Bout = new BufferedOutputStream(new FileOutputStream(file));
							
							byte[] buffer = new byte[1024];
							int nRead;
							while((nRead=Bin.read(buffer)) != -1) {
								Bout.write(buffer, 0, nRead);
							}
							sendMessage(new ObjectMsg(ObjectMsg.MODE_TX_FILE, chatMsg.userName, filename));
							Bout.flush();
							printDisplay(">> 전송을 완료했습니다: " + filename);

						} catch (FileNotFoundException e1) {
							System.out.println(">> 파일이 존재하지 않습니다:" + e1.getMessage() + "\n");
							return;
						} catch (IOException e1) {
							System.out.println(">> 파일을 읽을 수 없습니다:" + e1.getMessage() + "\n"+ filename);
							return;
						} finally {
							try {
								if (bis != null) bis.close();
							} catch (IOException e) {
								printDisplay(">> 파일을 닫을 수 없습니다: " + e.getMessage());
								return;
							}
						}
						
					}
					else if(ObjectMsg.MODE_TX_IMAGE == chatMsg.mode){
						printDisplay(chatMsg.userName + ": " + chatMsg.Image);
						broadcasting(chatMsg);
					}
					else if(ObjectMsg.MODE_CREATE_ROOM == chatMsg.mode) {
						printDisplay(chatMsg.userName + "가" + chatMsg.room_name + "방 생성");
					}
					else if(ObjectMsg.MODE_JOIN_ROOM == chatMsg.mode) {
						printDisplay(chatMsg.userName + "가" + chatMsg.room_name + "방 접속");
					}
				}
				users.removeElement(this);
			} catch (IOException e) {
				printDisplay("서버 읽기 오류>" + e.getMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					cs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		
		void sendMessage(ObjectMsg cmsg) {
			try {
				out.writeObject(cmsg);
				out.flush();
			} catch (IOException e) {
				System.err.println("클라이언트 일반 전송 오류>" + e.getMessage());
			}
		}
		
		void broadcasting(ObjectMsg cmsg) {
			for(int i = 0; i<users.size();i++) {
				users.get(i).sendMessage(cmsg);
			}
		}
		

		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
		
	}
	
	private void printDisplay(String msg) {
		t_display.append(msg + "\n");
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}
	
	public static void main(String[] args) {
		int port = 54321;

		Test_Server server = new Test_Server(port);
		//server.startServer();
	}
}

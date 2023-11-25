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

public class testServer2 extends JFrame{
   private int port;
   private ServerSocket serverSocket = null;
   private JTextArea t_display;
   private JButton b_exit;
   private JButton b_connect;
   private JButton b_disconnect;
   private Thread acceptThread = null;
   private Vector<ClientHandler> users;
   private Vector<ServerHandler> rooms;
   
   int count = 0;
   
   
   public testServer2(int port) {
      super("Hansung Chat Server");
      buildGUI();
      
      setSize(400,300);
      setLocation(800,300);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      setVisible(true);
      this.port = port;
      
      rooms = new Vector<ServerHandler>();
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
      b_connect = new JButton("���� ����");
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
      
      b_disconnect = new JButton("���� ����");
      b_disconnect.addActionListener(new ActionListener() {
      
         @Override
         public void actionPerformed(ActionEvent e) {
            disconnect();
         }
         
      });
      
      b_exit = new JButton("�����ϱ�");
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
         System.err.println("���� ��Ĺ �ݱ� ����>" + e.getMessage());
         System.exit(-1);
      }
   }
   
   public void startServer() {
      Socket clientSocket = null;
      try {
         serverSocket = new ServerSocket(port);
         t_display.append("������ ���۵Ǿ����ϴ�.\n");
         users = new Vector<ClientHandler>();
         while (acceptThread == Thread.currentThread()) {
            clientSocket = serverSocket.accept();
            t_display.append("Ŭ���̾�Ʈ�� ����Ǿ����ϴ�.\n");
            ClientHandler cHandler = new ClientHandler(clientSocket);
            
            cHandler.start();
            users.add(cHandler);
            
            
         }
      } catch (SocketException e) {
         printDisplay("���� ��Ĺ ����");
      } catch (IOException e) {
         e.printStackTrace();
      }
      finally {
         try {
            if(clientSocket != null) clientSocket.close();
            if(serverSocket != null) serverSocket.close();
         } catch (IOException e) {
            System.err.println("���� �ݱ� ����> " + e.getMessage());
            System.exit(-1);
         }
      }
   }
   
   
   private class ClientHandler extends Thread {
      private Socket clientSocket;
      private ObjectOutputStream out;
      private ObjectInputStream in;
      private BufferedInputStream Bin;
      private BufferedOutputStream Bout;
      private FileOutputStream fos;
      private ServerHandler currentRoom;
      
      public ClientHandler(Socket clientSocket) {
         this.clientSocket = clientSocket;
      }
      
      ObjectMsg chatMsg;
      void receiveMessages(Socket cs) {   
         try {
            in = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
            out = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
            while((chatMsg = (ObjectMsg)((ObjectInputStream) in).readObject()) != null) {
               if(ObjectMsg.MODE_LOGIN == chatMsg.mode) {
                  printDisplay(chatMsg.userName + "���� ����\n");
                  //User List(user name, total_user)
//                  ObjectMsg userList = new ObjectMsg(ObjectMsg.MODE_LOGIN, chatMsg.userName, "", null, users.size(), 0, "");
                  for(int i = 0; i<users.size(); i++) {
						broadcasting(users.get(i).chatMsg);
					}
               }
               else if(ObjectMsg.MODE_LOGOUT == chatMsg.mode) {
                  printDisplay(chatMsg.userName + "���� ����\n");
                  //User List
//                  ObjectMsg userList = new ObjectMsg(ObjectMsg.MODE_LOGOUT, chatMsg.userName, "", null, users.size(), 0, "");
                  broadcasting(chatMsg);
                  break;
               }
               else if(ObjectMsg.MODE_TX_STRING == chatMsg.mode){
                  printDisplay(chatMsg.userName + ": " + chatMsg.message);
                  roombroadcasting(chatMsg);
               }
               else if(ObjectMsg.MODE_TX_FILE == chatMsg.mode) {
            	    String filename = chatMsg.message;
            	    File file = new File(filename);
            	    
            	    if(!file.exists()) {
            	        printDisplay(">> ������ �������� �ʽ��ϴ�: " + filename);
            	        return;
            	    }

            	    BufferedInputStream bis = null;
            	    try {
            	        bis = new BufferedInputStream(new FileInputStream(file));
            	        fos = new FileOutputStream(filename);
            	        long filesize = file.length();
            	        byte[] buffer = new byte[1024];
            	        int nRead;
            	        
            	        while((nRead=bis.read(buffer)) != -1) {           	      
            	            Bout = new BufferedOutputStream(clientSocket.getOutputStream());
            	            Bout.write(buffer, 0, nRead);
            	            Bout.flush();
            	            fos.write(buffer, 0, count);
            	            fos.flush();
            	        }
            	        
            	        printDisplay(">> ���� ���� �Ϸ�: " + filename);
            	        
            	        // �ٸ� Ŭ���̾�Ʈ���� ���� ��� ����
            	        broadcastingOthers(chatMsg, filename);
            	        // ���� ������ �ٸ� Ŭ���̾�Ʈ���� ����
            	        redirectStream(bis, filesize);
            	    } catch (FileNotFoundException e1) {
            	        printDisplay(">> ������ �������� �ʽ��ϴ�:" + e1.getMessage());
            	        return;
            	    } catch (IOException e1) {
            	        printDisplay(">> ���� ���� �� ���� �߻�:" + e1.getMessage());
            	        return;
            	    } finally {
            	        try {
            	            if (bis != null) bis.close();
            	            if (fos != null) fos.close();
            	        } catch (IOException e) {
            	            printDisplay(">> ���� �ݱ� ����: " + e.getMessage());
            	            return;
            	        }
            	    }
            	}

               else if(ObjectMsg.MODE_TX_IMAGE == chatMsg.mode){
                  printDisplay(chatMsg.userName + ": " + chatMsg.message);
                  roombroadcasting(chatMsg);
               }
               else if(ObjectMsg.MODE_CREATE_ROOM == chatMsg.mode) {
                  printDisplay(chatMsg.userName + "��" + chatMsg.room_name + "�� ����");
                  ServerHandler sh = new ServerHandler(chatMsg.room_name);
                  rooms.add(sh);
                  ObjectMsg roomInfo = new ObjectMsg(ObjectMsg.MODE_CREATE_ROOM, chatMsg.userName, "", null, users.size(), rooms.size(), chatMsg.room_name);
                  broadcasting(roomInfo);
                  //Room List
               }
               else if(ObjectMsg.MODE_JOIN_ROOM == chatMsg.mode) {
                  printDisplay(chatMsg.userName + "��" + chatMsg.room_name + "�� ����");
                  ServerHandler sh = new ServerHandler(chatMsg.room_name);
                  if(this.currentRoom != null) { // ���� ���� null�� �ƴ϶��, ��, � �濡 ���� �ִٸ�
                      this.currentRoom.quitRoom(this); // ���� �濡�� ������ ó���մϴ�.
                  }

                  for(ServerHandler room : rooms) {
                      if(room.getRoomName().equals(chatMsg.room_name)) {
                          room.addRoom(this);
                          this.currentRoom = room;  // ���⿡ currentRoom�� �������ݴϴ�.
                          break;
                      }
                  }
                  ObjectMsg roomInfo = new ObjectMsg(ObjectMsg.MODE_JOIN_ROOM, chatMsg.userName, "", null, users.size(), rooms.size(), chatMsg.room_name);
                  broadcasting(roomInfo);
               }
               else if(ObjectMsg.MODE_OUT_ROOM == chatMsg.mode) {
                   printDisplay(chatMsg.userName + "��" + chatMsg.room_name + "�� ��������");
                   ServerHandler sh = new ServerHandler(chatMsg.room_name);
                   sh.quitRoom(this);
               }
            }
            users.removeElement(this);
         } catch (IOException e) {
            printDisplay("���� �б� ����>" + e.getMessage());
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
            System.err.println("Ŭ���̾�Ʈ �Ϲ� ���� ����>" + e.getMessage());
         }
      }
      
      void broadcasting(ObjectMsg cmsg) {
         for(int i = 0; i<users.size();i++) {
            users.get(i).sendMessage(cmsg);
         }
      }
      
      void roombroadcasting(ObjectMsg cmsg) {
    	    if(currentRoom != null) {
    	      currentRoom.sendMessage(cmsg);
    	    }
    	  }
      
      void broadcastingOthers(ObjectMsg cmsg, String filename) {
    	    for(int i = 0; i<users.size();i++) {
    	    	ClientHandler currentClient = null;
    	        if(users.get(i) != currentClient) {
    	            users.get(i).sendMessage(new ObjectMsg(ObjectMsg.MODE_TX_FILE, cmsg.userName, filename));
    	        }
    	    }
    	}
    	      
    	void redirectStream(BufferedInputStream bis, long filesize) throws IOException {
    	    byte[] buffer = new byte[1024];
    	    int len;
    	    long total = 0;
    	    
    	    while ((len = bis.read(buffer)) > 0 && total < filesize) {
    	        Bout.write(buffer, 0, len);
    	        Bout.flush();
    	        total += len;
    	    }
    	}
      
      @Override
      public void run() {
         receiveMessages(clientSocket);
      }
      
   }
   
   private class ServerHandler {
	   
	   private Vector<ClientHandler> ch;
	   private String roomName;
	   
	   public ServerHandler(String roomName) {
	        this.roomName = roomName;
	        this.ch = new Vector<ClientHandler>();
	    }
	   
	   public String getRoomName() {
		   return this.roomName;
	   }
	   
	   public void addRoom(ClientHandler client) {
	        this.ch.add(client);
	    }
	   
	   void quitRoom(ClientHandler client) {	   
		   int index = this.ch.indexOf(client);
		   if(index != -1) {
			   this.ch.remove(index);
		   }
	   }
	   
	   void sendMessage(ObjectMsg cmsg) {
		    for(int i = 0; i < ch.size(); i++) {
	    		try {
		        	ch.get(i).out.writeObject(cmsg);
		        	ch.get(i).out.flush();
		        } catch (IOException e) {
		            System.err.println("Ŭ���̾�Ʈ �Ϲ� ���� ����>" + e.getMessage());
		        }
		    }
		}
   }
   
   private void printDisplay(String msg) {
      t_display.append(msg + "\n");
      t_display.setCaretPosition(t_display.getDocument().getLength());
   }
   
   public static void main(String[] args) {
      int port = 54321;

      testServer2 server = new testServer2(port);
      //server.startServer();
   }
}
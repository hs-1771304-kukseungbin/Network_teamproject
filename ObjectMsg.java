import javax.swing.ImageIcon;

public class ObjectMsg {
	
	int mode;
	String userName;
	String message;
	ImageIcon Image;
	long total_user;
	long total_room;
	String room_name;
	
	public final static int MODE_ID_ERROR = 0x0;
	public final static int MODE_LOGIN = 0x1;
	public final static int MODE_LOGOUT = 0x2;
	public final static int MODE_CREATE_ROOM = 0x3;
	public final static int MODE_JOIN_ROOM = 0x4;
	public final static int MODE_TX_STRING = 0x10;
	public final static int MODE_TX_FILE = 0x20;
	public final static int MODE_TX_IMAGE = 0x40;
	
	public ObjectMsg(int mode, String id, String message, ImageIcon Image, long size, long room, String room_Name) {
		this.mode = mode;
		this.Image = Image;
		this.message = message;
		this.userName = id;
		this.total_user = size;
		this.total_room = room;
		this.room_name = room_Name;
	}
	
	public ObjectMsg(int mode, String id) {
		this(mode, id, null, null, 0, 0, null);
	}
	
	public ObjectMsg(int mode, String id, String message) {
		this(mode, id, message, null, 0, 0, null);
	}
	
	public ObjectMsg(int mode, String id, String message, ImageIcon Image) {
		this(mode, id, message, Image, 0, 0, null);
	}
}

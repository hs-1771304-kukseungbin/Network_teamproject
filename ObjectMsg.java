import javax.swing.ImageIcon;

public class ObjectMsg {
	
	int mode;
	String message;
	String userName;
	long total_user;
	long total_room;
	ImageIcon Image;
	
	public final static int MODE_LOGIN = 0x1;
	public final static int MODE_LOGOUT = 0x2;
	public final static int MODE_TX_STRING = 0x10;
	public final static int MODE_TX_FILE = 0x20;
	public final static int MODE_TX_IMAGE = 0x40;
	
	public ObjectMsg(int mode, String id, String message, ImageIcon Image, long size) {
		this.mode = mode;
		this.Image = Image;
		this.message = message;
		this.userName = id;
		this.total_user = size;
	}
	
	public ObjectMsg(int mode, String id) {
		this(mode, id, null, null, 0);
	}
	
	public ObjectMsg(int mode, String id, String message) {
		this(mode, id, message, null, 0);
	}
	
	public ObjectMsg(int mode, String id, String message, ImageIcon Image) {
		this(mode, id, message, Image, 0);
	}
}

package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Tag_Message extends Tag {
	Timestamp time;
	Tag_User sender;
	ArrayList<Tag_User> recipients;
	String text;
	
	public Tag_Message(String tableName, int id, String name, Timestamp time, Tag_User sender, ArrayList<Tag_User> recipients, String text) {
		super(tableName, id, name);
		this.type = "messages";
		this.time = time;
		this.sender = sender;
		this.recipients = recipients;
		this.text = text;
	}
}

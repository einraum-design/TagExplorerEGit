package tagexplorerprocessing2;

import java.sql.Timestamp;

public class Access {
	Timestamp date;
	String comment;
	Tag_User user = null;
	
	public Access(Timestamp date, String comment){
		this.date = date;
		this.comment = comment;
	}
	
	public Access(Timestamp date, String comment, Tag_User user){
		this.date = date;
		this.comment = comment;
		this.user = user;
	}
}

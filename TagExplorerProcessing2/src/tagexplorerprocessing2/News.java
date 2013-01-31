package tagexplorerprocessing2;

import java.sql.Timestamp;

public class News {
	
	Timestamp ts;
	String type; // symbol
	Tag_User user;
	String comment;
	
	
	public News(Timestamp ts){
		this.ts = ts;
		this.type = generateType();
		this.user = generateUser();
		this.comment = generateComment();
	}
	
	
	public News(Timestamp ts, String type){
		this.ts = ts;
		this.type = type;
		this.user = generateUser();
		this.comment = generateComment();
	}
	
	public News(Timestamp ts, String type, Tag_User user, String comment){
		this.ts = ts;
		this.type = type;
		this.user = user;
		this.comment = comment;
	}
	
	
	public Tag_User generateUser(){	
		Tag_User user = new Tag_User("users", 0, "name", "pw");
		return user;
	}
	
	public String generateType(){	
		String type = "call"; // "call", "message", "file", "appointment"
		return type;
	}
	
	public String generateComment(){	
		String comment = "comment";
		return comment;
	}

}

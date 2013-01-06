package tagexplorerprocessing2;

import java.sql.Timestamp;

public class Change {
	Timestamp date;
	String comment;
	
	public Change(Timestamp date, String comment){
		this.date = date;
		this.comment = comment;
	}
}

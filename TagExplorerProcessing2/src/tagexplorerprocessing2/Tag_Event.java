package tagexplorerprocessing2;

import java.sql.Timestamp;

public class Tag_Event extends Tag {
	
	Timestamp time;
	
	public Tag_Event(String tableName, int id, String name, Timestamp time){
		super(tableName, id, name);
		this.type = "events";
		this.time = time;
	}

	@Override
	public String toString() {
		return "Tag_Event [time=" + time + ", id=" + id + ", name=" + name
				+ "]";
	}
}

package tagexplorer;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Tag_File extends Tag {
	

	// files
	float size;
	String path;
	Timestamp creation_time;
	Timestamp expiration_time;
	int origin_ID;
	int score;
	
	ArrayList<Tag> attributes = new ArrayList<Tag>();
	
	String viewName;

	public Tag_File(String tableName, int id, String name, float size,
			String path, Timestamp createion_time, Timestamp expiration_time, int origin_ID,
			int score) {
		super(tableName, id, name);
		this.type = "files";
		this.size = size;
		this.path = path;
		this.creation_time = createion_time;
		this.expiration_time = expiration_time;
		this.origin_ID = origin_ID;
		this.score = score;
		
		viewName = name;
	}
	
	public void setAttributes(ArrayList<Tag> attributes){
		this.attributes = attributes;
	}
	
	public void updateViewName(){
		String anhang = "";
		
		for(Tag t : attributes){
			anhang += ", " + t.type + ": " +  t.name;
		}
		
		viewName = super.name + anhang;
	}
	
	@Override
	public String toString() {
		return "Tag_File [size=" + size + ", path=" + path + ", creation_time="
				+ creation_time + ", expiration_time=" + expiration_time
				+ ", origin_ID=" + origin_ID + ", score=" + score + "]";
	}
}

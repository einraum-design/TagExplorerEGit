package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;


public class Tag_File extends Tag {
	// files
		float size;
		String path;
		Timestamp creation_time;
		Timestamp expiration_time;
		Timestamp delete_time;
		int parent_ID = 0;
		int origin_ID = 0;
		int score;
		
		ArrayList<Tag> attributeBindings = new ArrayList<Tag>();
		ArrayList<Tag_File> fileBindings = new ArrayList<Tag_File>();
		ArrayList<Tag_File> versionBindings = new ArrayList<Tag_File>();
		
		public String viewName;

		public Tag_File(String tableName, int id, String name, float size,
				String path, Timestamp creation_time, Timestamp expiration_time, int parent_ID, int origin_ID,
				int score) {
			super(tableName, id, name);
			this.type = "files";
			this.size = size;
			this.path = path;
			this.creation_time = creation_time;
			this.expiration_time = expiration_time;
			this.parent_ID = parent_ID;
			this.origin_ID = origin_ID;
			this.score = score;
			
			this.viewName = name;
		}
		
		public void setDeleteTime(Timestamp time){
			this.delete_time = time;
		}
		
		public void setAttributeBindings(ArrayList<Tag> attributes){
			this.attributeBindings = attributes;
		}
		
		public void setFileBindings(ArrayList<Tag_File> files){
			this.fileBindings = files;
		}
		
		public void setVersionBindings(ArrayList<Tag_File> files){
			this.versionBindings = files;
		}
		
		public void setVersionBinding(Tag_File file){
			this.versionBindings.add(file);
		}

		
		public void updateViewName(){
			String anhang = "";
			
			for(Tag t : attributeBindings){
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

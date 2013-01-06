package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PImage;

public class Tag_File_Image extends Tag_File {
	
	PImage preview;

	public Tag_File_Image(String tableName, int id, String name, float size, String path, Timestamp creation_time,
			Timestamp expiration_time, int parent_ID, int origin_ID, int score, PImage preview) {
		
		super(tableName, id, name, size, path, creation_time, expiration_time, parent_ID, origin_ID, score);
		this.preview = preview;
		this.fileType = FileType.IMAGE;
	}

}

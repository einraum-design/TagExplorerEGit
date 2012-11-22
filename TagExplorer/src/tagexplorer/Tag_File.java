package tagexplorer;

public class Tag_File extends Tag {
	// files
	float size;
	String path;
	int creation_time;
	int expiration_time;
	int origin_ID;
	int score;

	public Tag_File(String tableName, int id, String name, float size,
			String path, int creation_time, int expiration_time, int origin_ID,
			int score) {
		super(tableName, id, name);
		this.type = "files";
		this.size = size;
		this.path = path;
		this.creation_time = creation_time;
		this.expiration_time = expiration_time;
		this.origin_ID = origin_ID;
		this.score = score;
	}
}

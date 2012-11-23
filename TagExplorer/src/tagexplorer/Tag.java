package tagexplorer;

public class Tag {
	
	int id;
	String name;
	String type = "tag";
	
	public Tag(){
	}
	
	public Tag(String tableName, int id, String name){
		this.id = id;
		this.name = name;
		type = tableName;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
}

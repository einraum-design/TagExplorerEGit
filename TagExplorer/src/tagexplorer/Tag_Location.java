package tagexplorer;

public class Tag_Location extends Tag {
	String coordinates;
	
	public Tag_Location(String tableName, int id, String name, String coordinates){
		super(tableName, id, name);
		this.type = "locations";
		this.coordinates = coordinates;
	}
}

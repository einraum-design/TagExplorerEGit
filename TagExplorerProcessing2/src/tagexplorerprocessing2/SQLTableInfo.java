package tagexplorerprocessing2;

public class SQLTableInfo {
	String name;
	String type;
	
	public SQLTableInfo(String name, String type){
		this.name = name;
		
		if(type.equals("int") || type.equals("tinyint")){
			this.type = "int";
		}else if(type.equals("varchar") || type.equals("text")){
			this.type = "String";
		}else if(type.equals("float")){
			this.type = "flaot";
		}else if(type.equals("timestamp")){
			this.type = "timestamp";
		}
	}
}

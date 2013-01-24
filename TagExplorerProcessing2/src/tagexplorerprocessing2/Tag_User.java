package tagexplorerprocessing2;

import processing.core.PImage;

public class Tag_User extends Tag {
	String password = "pwd";
	PImage img = null;
	
//	public Tag_User(String tableName, int id, String name){
//		super(tableName, id, name);
//		type = "users";
//	}
	
	public Tag_User(String tableName, int id, String name, String password){
		super(tableName, id, name);
		this.password = password;
		type = "users";
	}

	@Override
	public String toString() {
		return "Tag_User [password=" + password + ", id=" + id + ", name="
				+ name + ", type=" + type + "]";
	}


}
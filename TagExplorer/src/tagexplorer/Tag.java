package tagexplorer;

import toxi.physics.VerletParticle;

public class Tag extends VerletParticle{
	
	int id;
	String name;
	String type = "tag";
//	TagField tagField;
	
	public Tag(){
		super(0, 0, 0);
	}
	
	public Tag(String tableName, int id, String name){
		this();
		this.id = id;
		this.name = name;
		type = tableName;
		//this.tagField = new TagField(p5);
		
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", type=" + type + ", x="
				+ x + ", y=" + y + ", z=" + z + "]";
	}
}

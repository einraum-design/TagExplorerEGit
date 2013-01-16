package tagexplorerprocessing2;

import java.util.ArrayList;

import processing.core.PImage;

public class Tag_App extends Tag {
	PImage img = null;
	String imgName = null;
	int matches = 0;
	
	
	ArrayList<Tag> attributeBindings = new ArrayList<Tag>();
	
	public Tag_App(String tableName, int id, String name, String imgName){
		super(tableName, id, name);
		
		this.imgName = imgName;
		
//		this.id = id;
//		this.name = name;
//		type = tableName;
		//this.tagField = new TagField(p5);
		
	}
	
	public void setImage(PImage img){
		this.img = img;
	}
	
	public void setAttributeBindings(ArrayList<Tag> attributes) {
		this.attributeBindings = attributes;
	}
}

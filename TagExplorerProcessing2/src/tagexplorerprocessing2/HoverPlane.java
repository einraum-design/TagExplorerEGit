package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.geom.Vec2D;

public class HoverPlane extends Vec2D {
	
	PApplet p5;
	PShape infoBox;
	String fileName;
	
	public HoverPlane(PApplet p5, Tag_File file, int x, int y){
		super(x, y);
		this.p5 = p5;
		this.fileName = file.name;
		
		infoBox = p5.createShape();
		infoBox.fill(255);
//		infoBox.noStroke();
		infoBox.vertex(0, 0);
		infoBox.vertex(0, -300);
		infoBox.vertex(400, -300);
		infoBox.vertex(400, -50);
		infoBox.vertex(30, -50);
		infoBox.end(p5.CLOSE);
	}
	
	public void render(){
		p5.shape(infoBox, x, y);
		p5.fill(0);
		p5.text(fileName, x+30, y-260);
		
	}
}

package tagexplorerprocessing2;

import processing.core.PApplet;
import treemap.SimpleMapItem;

public class MapItem extends SimpleMapItem{
	//int count;
	int id;
	PApplet p5;
	
	public MapItem(PApplet p5, int id, int count){
		this.p5 = p5;
		this.id = id;
		this.size = count;
	}
	
	public void draw() {
		    // frames
		    // inheritance: x, y, w, h
		    p5.strokeWeight(1.25f);
		    p5.stroke(0);
		    p5.fill(255);
		    p5.rect(x, y, w, h);
		    
		    //System.out.println(id + " " + x + " " + y + " " + w + " " + h);
	 }
}

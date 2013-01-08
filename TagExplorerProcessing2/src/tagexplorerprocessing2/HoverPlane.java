package tagexplorerprocessing2;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PShape;
import toxi.geom.Vec2D;

public class HoverPlane extends Vec2D {
	
	PApplet p5;
	PShape infoBox;
	String fileName;
	
	int w = 0;
	int h = 70;
	
	ArrayList<Button> buttonList = new ArrayList<Button>();
	
	public HoverPlane(PApplet p5, Tag file, int x, int y){
		super(x, y);
		this.p5 = p5;
		this.fileName = file.name;
		
		w = (int)p5.textWidth(fileName) + 30;
		if(w<200){
			w = 200;
		}
		
		infoBox = p5.createShape();
		infoBox.fill(255);
//		infoBox.stroke(100);
		infoBox.vertex(0, 0);
		infoBox.vertex(0, -h);
		infoBox.vertex(w, -h);
		infoBox.vertex(w, -20);
		infoBox.vertex(12, -20);
		infoBox.end(p5.CLOSE);
		
		Button b = new Button(p5, "open", x+50, y-60, 50, 20);
		buttonList.add(b);
		
		//System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		return "HoverPlane [p5=" + p5 + ", infoBox=" + infoBox + ", fileName=" + fileName + ", w=" + w + ", h=" + h
				+ "]";
	}

	public void render(){
		p5.shape(infoBox, x, y);
		p5.fill(0);
		p5.text(fileName, x+15, y-h+20);
		
		for(Button b:buttonList){
			b.render();
		}
		
	}
	
	public boolean mouseOver(){
		boolean over = false;
		if(p5.mouseX >= x-10 && p5.mouseX < x + w && p5.mouseY < y +10 && p5.mouseY > y-h) {
			over = true;
		}	
		return over;
	}
}

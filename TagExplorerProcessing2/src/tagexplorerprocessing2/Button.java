package tagexplorerprocessing2;

import processing.core.PApplet;
import toxi.geom.Vec2D;

public class Button extends Vec2D{
	PApplet p5;
	int w, h;
	String label;
	
	public Button(PApplet p5, String label, int w, int h){
		super(0, 0);
		this.p5 = p5;
		this.w = w;
		this.h = h;
		this.label = label;
	}
	
	public Button(PApplet p5, String label, int x, int y, int w, int h){
		super(x, y);
		this.p5 = p5;
		this.w = w;
		this.h = h;
		this.label = label;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		
		p5.rect(x, y, w, h);
		p5.text(label, x+5, y+15);
		
	}
	
	
	public boolean mouseOver(){
		boolean over = false;
		
		// referenz links oben
		if(p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y+h) {
			over = true;
		}	
		return over;
	}
}

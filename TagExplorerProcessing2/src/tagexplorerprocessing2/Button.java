package tagexplorerprocessing2;

import processing.core.PApplet;
import toxi.geom.Vec2D;

public class Button extends Vec2D{
	TagExplorerProcessing2 p5;
	protected int w;
	protected int h;
	
	public Button(TagExplorerProcessing2 p5){
		super(0, 0);
		this.p5 = p5;
		this.w = 10;	
		this.h = 10;
	}
	
	public Button(TagExplorerProcessing2 p5, int w, int h){
		super(0, 0);
		this.p5 = p5;
		this.w = w;	
		this.h = h;
	}
	
	public Button(TagExplorerProcessing2 p5, int w, int h, int x, int y){
		super(x, y);
		this.p5 = p5;
		this.w = w;
		this.h = h;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		p5.noStroke();
		p5.rect(x, y, w, h);
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

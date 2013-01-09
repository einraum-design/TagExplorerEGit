package tagexplorerprocessing2;

import processing.core.PApplet;

public class MenuPlane {
	PApplet p5;

	float minH = 30;
	
	float h = minH;
	
	public MenuPlane(PApplet p5){
		this.p5 = p5;
		
	}
	
	public void update(){
		if(mouseOver()){
			h = 80;
		} else{
			h = minH;;
		}
	}
	
	public void render(){
		p5.fill(255, 220);
		p5.noStroke();
		p5.rect(0, 0, p5.width, h);
	}
	
	public boolean mouseOver(){
		boolean over = false;
		
		// referenz links oben
		if(p5.mouseY < h) { //p5.mouseX >= x && p5.mouseX < x + w &&  && p5.mouseY < y+h
			over = true;
		}	
		return over;
	}
}

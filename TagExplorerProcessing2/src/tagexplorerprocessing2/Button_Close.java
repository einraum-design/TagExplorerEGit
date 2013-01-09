package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PImage;

public class Button_Close extends Button {
	
	PImage img;

	public Button_Close(PApplet p5, int w, int h) {
		super(p5, w, h);	
	}
	
	public Button_Close(PApplet p5, PImage img, int w, int h) {
		super(p5, w, h);
		this.img = img;
		
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
}

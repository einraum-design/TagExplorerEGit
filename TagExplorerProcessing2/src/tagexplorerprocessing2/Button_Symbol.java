package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PImage;

public class Button_Symbol extends Button {
	
	PImage img;

	public Button_Symbol(TagExplorerProcessing2 p5, int w, int h) {
		super(p5, w, h);	
	}
	
	public Button_Symbol(TagExplorerProcessing2 p5, PImage img, int x, int y) {
		super(p5, img.width, img.height, x, y);
		this.img = img;		
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
			p5.noStroke();
			p5.rect(x, y, w, h);
		} 
		p5.image(img, x, y);
	}
}

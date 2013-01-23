package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Button_App extends Button {

	String label;
	PImage img;
	
	boolean showImgLabel = false;
	boolean showImg = false;
	
	public Button_App(PApplet p5, String label, PImage img, int w, int h, int x, int y) {
		super(p5, w, h, x, y);
		
		this.label = label;
		this.img = img;
		
		if (this.w > (int) p5.textWidth(label) + 30) {
			showImgLabel = true;
		}
		else if (this.w > h) {
			showImg = true;
		}

		
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		p5.noStroke();
		p5.rect(x, y, w, h);
		
		if(showImg){
			p5.imageMode(PConstants.CENTER);
			p5.image(img, x + w/2, y + h/2, h-4, h-4);
		}
		// Img & Label
		if(showImgLabel){
			p5.textAlign(PConstants.CENTER, PConstants.CENTER);
			p5.fill(90);
			p5.text(label, x + w/2 + 4 + (h-4)/2, y+h/2);
			
			int logoX = (int) (x + w/2 - p5.textWidth(label)/2 - 4 - (h-4)/2);	
			
			p5.imageMode(PConstants.CENTER);
			p5.image(img, logoX, y + h/2, h-4, h-4);
		}
	}

	
}

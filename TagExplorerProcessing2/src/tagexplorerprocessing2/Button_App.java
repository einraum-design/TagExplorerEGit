package tagexplorerprocessing2;

import processing.core.PConstants;
import processing.core.PImage;

public class Button_App extends Button {

	String label;
	PImage img;
	Tag_App app = null;
	
	boolean showImgLabel = false;
	boolean showImg = false;
	
	int imgRand = 4;
	
	public Button_App(TagExplorerProcessing2 p5, Tag_App app, int w, int h, int x, int y) {
		super(p5, w, h, x, y);
		
		this.app = app;
		
		this.label = app.name;
		this.img = app.img;
		
		p5.textFont(p5.font, 18);
		
		if (this.w > (int) p5.textWidth(label) + h + imgRand * 2) {
			showImgLabel = true;
		}
		else if (this.w >= h) {
			showImg = true;
		}
	}
	
	public Button_App(TagExplorerProcessing2 p5, String label, PImage img, int w, int h, int x, int y) {
		super(p5, w, h, x, y);
		
		this.label = label;
		this.img = img;
		
		p5.textFont(p5.font, 18);
		
		if (this.w > (int) p5.textWidth(label) + h + imgRand * 2) {
			showImgLabel = true;
		}
		else if (this.w >= h) {
			showImg = true;
		}
	}
	
	public void render(){
		p5.noStroke();
		p5.imageMode(PConstants.CORNER);
		p5.image(p5.backgroundApp, x, y, w, h);
		
		if(mouseOver()){
			p5.stroke(p5.cBorderHover);
		} else{
			p5.stroke(p5.cBorder);
		}
		p5.noFill();
		p5.rect(x, y, w, h);
		
		
		
		if(showImg){
			p5.imageMode(PConstants.CENTER);
			p5.image(img, x + w/2, y + h/2, h-imgRand*2, h-imgRand*2);
		}
		// Img & Label
		if(showImgLabel){
			p5.textFont(p5.font, 18);
			p5.textAlign(PConstants.CENTER, PConstants.CENTER);
			p5.fill(p5.cFont);
			float textX =  x + w/2 + (h-imgRand*2)/2 + imgRand*2;
			p5.text(label, textX, y+h/2);
			
			float logoX = textX - p5.textWidth(label)/2 - (h-imgRand*2)/2 - imgRand*2;	
			
			p5.imageMode(PConstants.CENTER);
			p5.image(img, logoX, y + h/2, h-imgRand*2, h-imgRand*2);
		}
	}

	
}

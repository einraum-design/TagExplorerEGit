package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Button_TagType extends Button {
	String type = null;
	PImage img;
	
	
//	public Button_TagType(TagExplorerProcessing2 p5, String label, String type, int w, int h) {
//		super(p5, w, h);
//		this.p5 = p5;
//		if (this.w < (int) p5.textWidth(label) + 10) {
//			this.w = (int) p5.textWidth(label) + 10;
//		}
//		this.label = label;
//		this.type = type;
//	}
	
//	public Button_TagType(TagExplorerProcessing2 p5, String label, String type, int w, int h, int x, int y){
//		super(p5, w, h, x, y);
//		this.p5 = p5;
//		
//		if (this.w < (int) p5.textWidth(label) + 20) {
//			this.w = (int) p5.textWidth(label) + 20;
//		}
//		this.label = label;
//		this.type = type;
//	}
	
	public Button_TagType(TagExplorerProcessing2 p5, PImage img, String type, int w, int h, int x, int y){
		super(p5, w, h, x, y);
		this.p5 = p5;
		
//		if (this.w < (int) p5.textWidth(label) + 20) {
//			this.w = (int) p5.textWidth(label) + 20;
//		}
		this.type = type;
		this.img = img;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(p5.cDropdownHover);
			p5.stroke(p5.cBorderHover);
		} else{
			p5.fill(p5.cButtonBright);
			p5.stroke(p5.cBorder);
		}
		
		
		p5.rectMode(PConstants.CORNER);
		p5.rect((int)x, (int)y, (int)w, (int)h);
		p5.imageMode(PConstants.CENTER);
		p5.image(img, x+w/2, y+h/2);
//		p5.fill(50);
//		p5.textAlign(PConstants.CENTER, PConstants.CENTER);
//		p5.text(label, x+w/2, y+h/2);
	}
}

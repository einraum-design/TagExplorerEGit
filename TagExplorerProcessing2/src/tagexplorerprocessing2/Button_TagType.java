package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;

public class Button_TagType extends Button {
	String label;
	String type = null;
	
	
	public Button_TagType(TagExplorerProcessing2 p5, String label, String type, int w, int h) {
		super(p5, w, h);
		this.p5 = p5;
		if (this.w < (int) p5.textWidth(label) + 10) {
			this.w = (int) p5.textWidth(label) + 10;
		}
		this.label = label;
		this.type = type;
	}
	
	public Button_TagType(TagExplorerProcessing2 p5, String label, String type, int w, int h, int x, int y){
		super(p5, w, h, x, y);
		this.p5 = p5;
		
		if (this.w < (int) p5.textWidth(label) + 20) {
			this.w = (int) p5.textWidth(label) + 20;
		}
		this.label = label;
		this.type = type;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		
		p5.noStroke();
		p5.rectMode(PApplet.CORNER);
		p5.rect(x, y, w, h);
		p5.fill(50);
		p5.textAlign(PConstants.CENTER, PConstants.CENTER);
		p5.text(label, x+w/2, y+h/2);
	}
}

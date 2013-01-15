package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;

public class Button_LabelToggle extends Button_Label {

	boolean onOff = false;
	
	public Button_LabelToggle(TagExplorerProcessing2 p5, String label, boolean onOff, int w, int h, int x, int y){
		super(p5, label, w, h, x, y);
		this.onOff = onOff;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		
		if(onOff){
			p5.fill(180);
		}
		
		p5.noStroke();
		p5.rectMode(PApplet.CORNER);
		p5.rect(x, y, w, h);
		p5.fill(50);
		p5.textAlign(PConstants.CENTER, PConstants.CENTER);
		p5.text(label, x+w/2, y+h/2);
	}
	
	public void toggle(){
		onOff = !onOff;
	}
}

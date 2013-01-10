package tagexplorerprocessing2;

import processing.core.PApplet;

public class Button_DropDown extends Button {
String label;
	
	public Button_DropDown(PApplet p5, String label, int w, int h) {
		super(p5, w, h);
		this.p5 = p5;
		if (w < (int) p5.textWidth(label) + 10) {
			w = (int) p5.textWidth(label) + 10;
		}
		this.label = label;

		// TODO Auto-generated constructor stub
	}
	
	public Button_DropDown(PApplet p5, String label, int w, int h, int x, int y){
		super(p5, w, h, x, y);
		this.p5 = p5;
		if (w < (int) p5.textWidth(label) + 10) {
			w = (int) p5.textWidth(label) + 10;
		}
		this.label = label;
	}
	
	public void render(){
		if(mouseOver()){
			p5.fill(230);
		} else{
			p5.fill(200);
		}
		p5.noStroke();
		p5.rect(x, y, w, h);
		p5.fill(50);
		p5.textAlign(p5.LEFT, p5.CENTER);
		p5.text(label, x+10, y+h/2);
	}
}

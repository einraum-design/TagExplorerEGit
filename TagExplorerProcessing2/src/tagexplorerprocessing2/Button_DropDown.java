package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Button_DropDown extends Button {
	String label;

	String type;
	

	public Button_DropDown(TagExplorerProcessing2 p5, String label, String type, int w, int h) {
		super(p5, w, h);
		this.p5 = p5;
		if (this.w < (int) p5.textWidth(label) + 20) {
			this.w = (int) p5.textWidth(label) + 20;
		}
		this.label = label;
		this.type = type;
		
		p5.imageMode(PConstants.CENTER);

		// TODO Auto-generated constructor stub
	}

	public Button_DropDown(TagExplorerProcessing2 p5, String label, String type, int w, int h, int x, int y) {
		super(p5, w, h, x, y);
		this.p5 = p5;
		if (this.w < (int) p5.textWidth(label) + 20) {
			this.w = (int) p5.textWidth(label) + 20;
		}
		this.label = label;
		this.type = type;
		
		p5.imageMode(PConstants.CENTER);
	}

	public void render() {
		if (mouseOver()) {
			p5.fill(p5.cDropdownHover);
		} else {
			p5.fill(p5.cButtonBright);
		}
		p5.stroke(p5.cBorder);
		p5.rect(x, y, this.w, this.h);
		p5.fill(p5.cFont);
		p5.textAlign(p5.LEFT, p5.CENTER);
		//p5.image(typeImg, x + 16, y + this.h/2);
		p5.text(label, x + 32, y + this.h / 2);
	}
}

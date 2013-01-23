package tagexplorerprocessing2;

import processing.core.PImage;

public class Button_DropDownTag extends Button_DropDown {

	PImage typeImg;
	
	public Button_DropDownTag(TagExplorerProcessing2 p5, String label, String type, int w, int h) {
		super(p5, label, type, w, h);
		
		switch (type) {
		case "keywords":
			typeImg = p5.tagMinKeyword;
			break;
		case "locations":
			typeImg = p5.tagMinLocation;
			break;
		case "projects":
			typeImg = p5.tagMinProject;
			break;
		case "users":
			typeImg = p5.tagMinUser;
			break;
		case "events":
			typeImg = p5.tagMinEvent;
			break;
		default:
			typeImg = p5.tagMinKeyword;
		}

		p5.textFont(p5.font, 13);
	}
	
	public Button_DropDownTag(TagExplorerProcessing2 p5, String label, String type, int w, int h, int x, int y) {
		super(p5, label, type, w, h, x, y);
		
		switch (type) {
		case "keywords":
			typeImg = p5.tagMinKeyword;
			break;
		case "locations":
			typeImg = p5.tagMinLocation;
			break;
		case "projects":
			typeImg = p5.tagMinProject;
			break;
		case "users":
			typeImg = p5.tagMinUser;
			break;
		case "events":
			typeImg = p5.tagMinEvent;
			break;
		default:
			typeImg = p5.tagMinKeyword;
		}

		p5.textFont(p5.font, 13);
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
		p5.image(typeImg, x + 11, y + this.h/2);
		p5.text(label, x + 22, y + this.h / 2);
	}

}

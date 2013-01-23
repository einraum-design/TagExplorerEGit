package tagexplorerprocessing2;

import processing.core.PImage;

public class Button_DropDownGross extends Button_DropDown {
	PImage typeImg;

	public Button_DropDownGross(TagExplorerProcessing2 p5, String label, String type, int w, int h) {
		super(p5, label, type, w, h);

		switch (type) {
		case "keywords":
			typeImg = p5.minKeyword;
			break;
		case "locations":
			typeImg = p5.minLocation;
			break;
		case "projects":
			typeImg = p5.minProject;
			break;
		case "users":
			typeImg = p5.minUser;
			break;
		case "events":
			typeImg = p5.minEvent;
			break;
		default:
			typeImg = p5.minKeyword;
		}

		p5.textFont(p5.font, 16);
	}

	public Button_DropDownGross(TagExplorerProcessing2 p5, String label, String type, int w, int h, int x, int y) {
		super(p5, label, type, w, h, x, y);

		switch (type) {
		case "keywords":
			typeImg = p5.minKeyword;
			break;
		case "locations":
			typeImg = p5.minLocation;
			break;
		case "projects":
			typeImg = p5.minProject;
			break;
		case "users":
			typeImg = p5.minUser;
			break;
		case "events":
			typeImg = p5.minEvent;
			break;
		default:
			typeImg = p5.minKeyword;
		}

		p5.textFont(p5.font, 16);
	}

	public void render() {
		if (mouseOverField()) {
			p5.fill(p5.cDropdownHover);
		} else {
			p5.fill(p5.cButtonBright);
		}
		p5.stroke(p5.cBorder);
		p5.rect(x, y, this.w, this.h);
		p5.fill(p5.cFont);
		p5.textAlign(p5.LEFT, p5.CENTER);
		p5.image(typeImg, x + 16, y + this.h / 2);
		p5.text(label, x + 32, y + this.h / 2);

		// filter Icon
		p5.image(p5.minFilter, x + w - 16, y + this.h / 2);
		if(type.equals("users")){
			p5.image(p5.minMessage, x + w - 16 - 26, y + this.h / 2);
			p5.image(p5.minCall, x + w - 16 - 50, y + this.h / 2);
		}
	}

	public boolean mouseOverField() {
		boolean over = false;

		// referenz links oben
		if (p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y + h) {
			over = true;
		}
		return over;
	}

	public boolean mouseOver() {
		boolean over = false;

		// wenn user nur bei klick auf tag!
		if (type.equals("users")) {
			if (p5.mouseX >= x + w - 28 && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y + h) {
				over = true;
			}
		} else {
			over = mouseOverField();
		}
		return over;
	}

}

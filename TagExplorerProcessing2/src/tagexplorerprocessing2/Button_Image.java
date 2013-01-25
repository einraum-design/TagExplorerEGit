package tagexplorerprocessing2;

import processing.core.PConstants;
import processing.core.PImage;

public class Button_Image extends Button {
	
	PImage img;
	PImage img_h;

	public Button_Image(TagExplorerProcessing2 p5, String imageName, int x, int y) {
		super(p5);
		
		switch (imageName){
		case "news":
			img = p5.newsButton;
			img_h = p5.newsButton_h;
			break;
		}
		
		this.x = x;
		this.y = y;
		this.w = img.width;
		this.h = img.height;
		// TODO Auto-generated constructor stub
	}
	
	public void render(){
		p5.imageMode(PConstants.CORNER);
		if(mouseOver()){
			p5.image(img_h, x, y);
		} else{
			p5.image(img, x, y);
		}
	}

}

package tagexplorerprocessing2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Button_Symbol extends Button {
	
	PImage img;
	PImage img_h;

//	public Button_Symbol(TagExplorerProcessing2 p5, int w, int h) {
//		super(p5, w, h);	
//	}
	
	public Button_Symbol(TagExplorerProcessing2 p5, String imgName, int x, int y) {
		super(p5,10, 10, x, y);
		
		switch(imgName){
		case "close":
			img = p5.close;
			img_h = p5.close_h;
			w = img.width;
			h = img.height;
			break;
		case "open":
			img = p5.open;
			img_h = p5.open;
			w = img.width;
			h = img.height;
			break;
		case "user":
			img = p5.newUser;
			img_h = p5.newUser;
			w = img.width;
			h = img.height;
			break;
		default:
			img = p5.close;
			img_h = p5.close_h;
			w = img.width;
			h = img.height;
		}
		
		//this.img = img;		
	}
	
	public void render(){
		p5.imageMode(PConstants.CENTER);
		if(mouseOver()){
			p5.image(img_h, x, y);
		} else{
			p5.image(img, x, y);
		}
		
		
		
	}
}

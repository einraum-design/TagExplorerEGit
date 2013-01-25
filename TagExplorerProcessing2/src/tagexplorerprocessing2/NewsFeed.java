package tagexplorerprocessing2;

import processing.core.PConstants;
import toxi.geom.Vec2D;

public class NewsFeed extends Vec2D{
	
	TagExplorerProcessing2 p5;
	int w = 380;
	
	Button_Image newButton;
	
	public NewsFeed(TagExplorerProcessing2 p5, int x, int y){
		super(x, y);
		this.p5 = p5;
		
		newButton = new Button_Image(p5, "news", x-80 , y);
	}
	
	
	public void update(){
		
	}
	
	public void render(){
		p5.noStroke();
		p5.fill(p5.cBorder);
		p5.rect(x-w, y, w, 22);
		
		p5.fill(p5.cButtonBright);
		p5.stroke(p5.cBorder);
		
		int yShift = 22;
		for(int i = 0; i<12; i++){
			p5.rect(x-w, y + yShift, w, 60);
			yShift += 60;
		}
		
		yShift = 22;
		
		p5.textAlign(PConstants.CENTER);
		p5.textFont(p5.font, 12);
		p5.fill(p5.cFont);
		for(int i = 0; i<12; i++){
			p5.text("12. 1. 2013", x-40, y + 50 + yShift);
			yShift += 60;
		}
		
		newButton.render();
		
	}
}

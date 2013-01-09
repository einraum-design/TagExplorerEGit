package tagexplorerprocessing2;

import java.util.ArrayList;

import processing.core.PShape;
import toxi.geom.Vec2D;

public class HoverPlane extends Vec2D {
	
	TagExplorerProcessing2 p5;
	PShape infoBox;
	String fileName;
	
	int w = 0;
	int h = 70;
	
	ArrayList<Button> buttonList = new ArrayList<Button>();
	
	public HoverPlane(TagExplorerProcessing2 p5, Tag tag, int x, int y){
		super(x, y);
		this.p5 = p5;
		this.fileName = tag.name;
		
		w = (int)p5.textWidth(fileName) + 30;
		if(w<200){
			w = 200;
		}
		
		infoBox = createInfoBox();
		
		Button b = new Button_Label(p5, "open", 50, 20, x+w-15-50, y-30-20);
		buttonList.add(b);
		
//		b = new Button_Symbol(p5, p5.closeImg, x+w-15-80, y-30-20);
//		buttonList.add(b);
		
		//System.out.println(this.toString());
	}

	@Override
	public String toString() {
		return "HoverPlane [p5=" + p5 + ", infoBox=" + infoBox + ", fileName=" + fileName + ", w=" + w + ", h=" + h
				+ "]";
	}

	public void render(){
		p5.shape(infoBox, x, y);
		p5.fill(0);
		p5.textAlign(p5.LEFT, p5.BOTTOM);
		p5.text(fileName, x+15, y-h+20);
		
		for(Button b:buttonList){
			b.render();
		}
	}
	
	private PShape createInfoBox() {
		float yOffset = 20;
		float xOffset = 12;
		float rad = 5.0f;
		float kappa = (4.0f * (p5.sqrt(2.0f) - 1.0f) / 3.0f);
		PShape infoBox = p5.createShape();
		infoBox.fill(255);
		infoBox.vertex(0, 0);
		infoBox.vertex(0, -h + rad);
		infoBox.bezierVertex(0,-h + rad - rad * kappa, rad - rad*kappa, -h, rad, -h);
		infoBox.vertex(w-rad, -h);
		infoBox.bezierVertex(w-rad+rad*kappa,-h, w, -h + rad - rad*kappa, w, -h + rad);
		infoBox.vertex(w, -yOffset-rad);
		infoBox.bezierVertex(w,-yOffset-rad + rad * kappa, w - rad + rad*kappa, -yOffset, w-rad, -yOffset);
		infoBox.vertex(xOffset, -yOffset);
		infoBox.end(p5.CLOSE);
		return infoBox;
	}
	
	public boolean mouseOver(){
		boolean over = false;
		if(p5.mouseX >= x-10 && p5.mouseX < x + w && p5.mouseY < y +10 && p5.mouseY > y-h) {
			over = true;
		}	
		return over;
	}
}

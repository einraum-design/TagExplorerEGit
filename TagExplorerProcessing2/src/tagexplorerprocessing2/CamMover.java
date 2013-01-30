package tagexplorerprocessing2;

import processing.core.PConstants;
import processing.core.PShape;
import toxi.geom.Vec2D;

public class CamMover extends Vec2D {

	TagExplorerProcessing2 p5;
	PShape mover;


	float h;
	float w;


	public CamMover(TagExplorerProcessing2 p5, int x, int y) {
		super(x, y);

		this.p5 = p5;

		

		
		h = 36;
		w = h * 2;
		
		this.x = x;
		this.y = y - h; // startY    	// + p5.timeline.mapExpCam(p5.cam_eyetargetpos.z - (p5.height / 2.0f) / p5.tan(PConstants.PI * 30.0f / 180.0f), h) - hFeld;

		// MinTime Feld
		mover = p5.createShape();
		mover.fill(p5.cTagDark);
		mover.noStroke();

		mover.vertex(0 + h, 0);
		mover.vertex(0 + w, 0);
		mover.vertex(0 + w, h);
		mover.vertex(0,  h);

		mover.end();

	}

	public void render() {
		// FlŠche
		
		p5.pushMatrix();
		p5.translate(x, y);
		
		p5.shape(mover);
		
		// Icon
		p5.imageMode(PConstants.CORNER);
		p5.image(p5.cameraImg, h, 0, h, h);
		
		p5.popMatrix();
	}
	
	public void setY(float setY){
		y  = setY - h/2;
		
		
		// mapping eventuell noch logarithmisch? - mŸsste stimmen
		
		// calc cameraTarget
		p5.cam_eyetargetpos.z = p5.map(y+h, 
				p5.timeChooser.y + p5.timeChooser.h, p5.timeChooser.y, 
				(p5.height / 2.0f) / p5.tan(PConstants.PI * 30.0f / 180.0f), 
				- p5.timeline.timelineLength + (p5.height / 2.0f) / p5.tan(PConstants.PI * 30.0f / 180.0f));
		
		//System.out.println("set Y: " + p5.cam_eyetargetpos.z + " " + (p5.timeChooser.y + p5.timeChooser.h) + " " + (p5.timeChooser.y) + " " + ((p5.height / 2.0f) / p5.tan(PConstants.PI * 30.0f / 180.0f)) + " " + (p5.timeline.timelineLength + (p5.height / 2.0f) / p5.tan(PConstants.PI * 30.0f / 180.0f)));
	}

	public boolean mouseOver(float x, float y, float w, float h) {
		boolean over = false;

		// referenz links oben
		if (p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y + h) {
			over = true;
		}
		return over;
	}
	public boolean mouseOver() {
		boolean over = false;

		// referenz links oben
		if (p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y + h) {
			over = true;
		}
		return over;
	}
}

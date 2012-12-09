package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Timeline {

	PApplet p5;
	PGraphics pg;
	Timestamp oldest = null;
	
	long startTime;
	
	public Timeline(PApplet p5){
		this.p5 = p5;
		
		pg = p5.createGraphics(100, p5.height);
	}
	
	public void draw(){
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		pg.beginDraw();
		pg.pushMatrix();
		pg.translate(0, 40);
		
		pg.fill(200);
		pg.text(now.toGMTString(), 0, 0);
		
		if(oldest != null)
			pg.text(oldest.toGMTString(), 0, pg.height-80);
		
		pg.popMatrix();
		pg.endDraw();
		
//		p5.pushMatrix();
//		p5.translate(p5.width-100, 40);
//		
//		p5.fill(200);
//		p5.text(now.toGMTString(), 0, 0);
//		
//		if(oldest != null)
//			p5.text(oldest.toGMTString(), 0, p5.height-80);
//		
//		p5.popMatrix();
	}
	
	public void setWertebereich(Timestamp ts){
		oldest = ts;
		
		long currentTime = System.currentTimeMillis();
		startTime = ts.getTime();
		long delta = currentTime - startTime;
	}
}

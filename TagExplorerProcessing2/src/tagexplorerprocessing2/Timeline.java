package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Timeline {

	PApplet p5;
	PGraphics pg;
	Timestamp oldest = null;
	
	int timelineLength;
	
	long startTime;
	
	public Timeline(PApplet p5){
		this.p5 = p5;
		
		pg = p5.createGraphics(200, p5.height);
		
		timelineLength = pg.height - 80;
	}
	
	public void draw(){
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		pg.beginDraw();
		pg.background(120);
		pg.pushMatrix();
		pg.translate(0, 40);
		
		pg.fill(200);
		pg.text(now.toGMTString(), 0, 0);
		
		if(oldest != null){
			int timeLineLength = pg.height - 80;
			long delta = System.currentTimeMillis() - oldest.getTime();

			pg.text("1 hour ", 0, mapExp(60 * 60 * 1000));
			
			pg.text("1 day " , 0, mapExp(24 * 60 * 60 * 1000));
			
			pg.text("1 week ", 0, mapExp(7L * 24 * 60 * 60 * 1000));
			pg.text("2 weeks ", 0, mapExp(14L * 24 * 60 * 60 * 1000));
			
			pg.text("1 month ", 0, mapExp(30L * 24 * 60 * 60 * 1000));

			
			
			pg.text(oldest.toGMTString(), 0, p5.map(System.currentTimeMillis() - oldest.getTime(), 0, delta, 0, timeLineLength));
		}
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

	
	public float mapExp(long time){
		return mapExp(time, timelineLength);
	}
	
	public float mapExp(Timestamp ts, long maxLength){
		long time = System.currentTimeMillis() - ts.getTime();
		return mapExp(time, timelineLength);
	}
	
	public float mapExp(long time, long maxLength){
		float val = p5.map(p5.sqrt(time), 0, p5.sqrt(System.currentTimeMillis() - oldest.getTime()), 0, maxLength);	
		//System.out.println("val: " + p5.sqrt(time));
		
		return val;
	}
	
	public void setWertebereich(Timestamp ts){
		if(ts != null){
			oldest = ts;
			long currentTime = System.currentTimeMillis();
			startTime = ts.getTime();
			long delta = currentTime - startTime;
		}
		
		
	}
}

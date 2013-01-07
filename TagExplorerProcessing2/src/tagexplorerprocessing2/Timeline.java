package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Timeline {

	TagExplorerProcessing2 p5;
	PGraphics pg;
	Timestamp oldest = null;

	int timelineLength;

	long startTime;

	public Timeline(TagExplorerProcessing2 p5) {
		this.p5 = p5;

		pg = p5.createGraphics(200, p5.height);

		// timelineLength = pg.height - 80;

		timelineLength = 2000;
	}

	public void render(PGraphics renderer) {
		if (oldest != null) {
			renderer.noStroke();
			renderer.fill(255, 150);
			renderer.beginShape();
			renderer.vertex(10, 0, 0);
			renderer.vertex(10, 0, -timelineLength);
			renderer.vertex(-10, 0, -timelineLength);
			renderer.vertex(-10, 0, 0);
			renderer.endShape(p5.CLOSE);

			
			long delta = System.currentTimeMillis() - oldest.getTime();
			
			renderer.pushMatrix();
			
			
			renderTime(renderer, 60 * 60 * 1000, "1 hour ");
			
			renderTime(renderer, 24 * 60 * 60 * 1000, "1 day ");
			
			renderTime(renderer, 7L * 24 * 60 * 60 * 1000, "1 week ");
			
			renderTime(renderer, 14L * 24 * 60 * 60 * 1000, "2 weeds ");
			
			renderTime(renderer, 30L * 24 * 60 * 60 * 1000, "1 month ");
			
			renderTime(renderer, System.currentTimeMillis() - oldest.getTime(), oldest.toGMTString());
			

//			pg.text("1 day ", 0, mapExp(24 * 60 * 60 * 1000));
//
//			pg.text("1 week ", 0, mapExp(7L * 24 * 60 * 60 * 1000));
//			pg.text("2 weeks ", 0, mapExp(14L * 24 * 60 * 60 * 1000));
//
//			pg.text("1 month ", 0, mapExp(30L * 24 * 60 * 60 * 1000));
//
//			pg.text(oldest.toGMTString(), 0,
//					p5.map(System.currentTimeMillis() - oldest.getTime(), 0, delta, 0, timeLineLength));
			
			
			renderer.popMatrix();
		}
	}
	
	public void renderTime(PGraphics renderer, long time, String name){
		renderer.translate(0, 0, -mapExp(time));
		renderer.rotateX(p5.xBillboardRotation);
		renderer.rotateY(p5.yBillboardRotation);
		renderer.fill(255);
		renderer.text(name, 0, 0);
		renderer.rotateY(-p5.yBillboardRotation);
		renderer.rotateX(-p5.xBillboardRotation);
		renderer.translate(0, 0, -mapExp(60 * 60 * 1000));
	}

	public float mapExp(Timestamp ts) {
		// System.out.println("ts time: " + ts.getTime());
		long time = System.currentTimeMillis() - ts.getTime();
		// System.out.println("delta time: " + time);
		return mapExp(time);
	}

	public float mapExp(long time) {
		float maxLength = timelineLength;
		float val = p5.map(p5.sqrt(time), 0, p5.sqrt(System.currentTimeMillis() - oldest.getTime()), 0, maxLength);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

	public void setWertebereich(Timestamp ts) {
		if (ts != null) {
			oldest = ts;
			long currentTime = System.currentTimeMillis();
			startTime = ts.getTime();
			long delta = currentTime - startTime;
		}

	}
}

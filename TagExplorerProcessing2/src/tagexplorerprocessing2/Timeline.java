package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Timeline {

	TagExplorerProcessing2 p5;
	PGraphics pg;
	Timestamp oldest = null;

	int timelineLength;

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
			
			renderer.pushMatrix();
			
			renderer.translate(renderer.width/2 - 120, renderer.height/2 - 60);
			
			renderer.beginShape();
			renderer.vertex(10, 0, 0);
			renderer.vertex(10, 0, -timelineLength);
			renderer.vertex(-10, 0, -timelineLength);
			renderer.vertex(-10, 0, 0);
			renderer.endShape(p5.CLOSE);

//			renderer.pushMatrix();
			
			
			

			renderer.textAlign(p5.CENTER);

			renderTime(renderer, 60L * 60 * 1000, "1 hour");
			renderTime(renderer, 24L * 60 * 60 * 1000, "1 day");
			renderTime(renderer, 7L * 24 * 60 * 60 * 1000, "1 week");
			renderTime(renderer, 14L * 24 * 60 * 60 * 1000, "2 weeks");
			renderTime(renderer, 21L * 24 * 60 * 60 * 1000, "3 weeks");
			// renderTime(renderer, 28L * 24 * 60 * 60 * 1000, "4 weeks ");
			renderTime(renderer, 30L * 24 * 60 * 60 * 1000, "1 month");
			renderTime(renderer, 6L * 30 * 24 * 60 * 60 * 1000, "6 month");
			renderTime(renderer, 365L * 24 * 60 * 60 * 1000, "1 year");

			renderTime(renderer, System.currentTimeMillis() - oldest.getTime(), oldest.toGMTString());

//			renderer.popMatrix();
			renderer.popMatrix();
			
			renderZeitbereich(renderer, 0, 24L * 60 * 60 * 1000); // jetzt bis 1 Tag
			renderZeitbereich(renderer,  7L * 24 * 60 * 60 * 1000, 14L * 24 * 60 * 60 * 1000); // letzte woche
			renderZeitbereich(renderer,  21L * 24 * 60 * 60 * 1000, 30L * 24 * 60 * 60 * 1000); // 3. woche bis monatsende
			renderZeitbereich(renderer,  180L * 24 * 60 * 60 * 1000, 365L * 24 * 60 * 60 * 1000); // Halbjahr bis Jahresende
			renderZeitbereich(renderer,  730L * 24 * 60 * 60 * 1000, 1095L * 24 * 60 * 60 * 1000); // Halbjahr bis Jahresende
//			renderZeitbereich(renderer,  180L * 24 * 60 * 60 * 1000, 365L * 24 * 60 * 60 * 1000); // Halbjahr bis Jahresende
		}
	}
	
	public void renderZeitbereich(PGraphics renderer, long startTime, long endTime){
		renderer.fill(0, 180);
		renderer.beginShape();
		
//		System.out.println("starttime" + -mapExpMillis(startTime));
//		System.out.println("endtime: " + -mapExpMillis(endTime));
		
//		renderer.translate(0, 0, -mapExpMillis(startTime));
		
		renderer.vertex(-renderer.width/2, renderer.height/2 - 60, -mapExpMillis(startTime));
		renderer.vertex(renderer.width/2,  renderer.height/2 - 60, -mapExpMillis(startTime));
		renderer.vertex(renderer.width/2,  renderer.height/2 - 60, -mapExpMillis(endTime));
		renderer.vertex(-renderer.width/2, renderer.height/2 - 60, -mapExpMillis(endTime));
		renderer.endShape(p5.CLOSE);
		
//		renderer.translate(0, 0, mapExpMillis(startTime));
	}

	public void renderTime(PGraphics renderer, long time, String name) {
		renderer.translate(0, 0, -mapExpMillis(time));
		renderer.rotateX(p5.xBillboardRotation);
		renderer.rotateY(p5.yBillboardRotation);
		renderer.fill(0);
		renderer.text(name, 0, 0);
		renderer.rotateY(-p5.yBillboardRotation);
		renderer.rotateX(-p5.xBillboardRotation);
		renderer.translate(0, 0, mapExpMillis(time));
	}

	public float mapExp(Timestamp ts) {
		// System.out.println("ts time: " + ts.getTime());
		long time = System.currentTimeMillis() - ts.getTime();
		// System.out.println("delta time: " + time);
		return mapExpMillis(time);
	}

	public float mapExpMillis(long time) {

		if (oldest == null) {
			return 0;
		}
		float val = p5.map(p5.sqrt(time/1000), 0, p5.sqrt(System.currentTimeMillis()/1000 - oldest.getTime()/1000), 0, timelineLength);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}
	
	public float mapExpSeconds(long time) {

		if (oldest == null) {
			return 0;
		}
		float val = p5.map(p5.sqrt(time), 0, p5.sqrt(System.currentTimeMillis()/1000 - oldest.getTime()/1000), 0, timelineLength);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

	public void setWertebereich(Timestamp ts) {
		if (ts != null) {
			oldest = ts;
			timelineLength = (int) p5.sqrt((System.currentTimeMillis() - oldest.getTime()) / 10000);
			System.out.println(System.currentTimeMillis() + " - " + oldest.getTime());
			System.out.println(System.currentTimeMillis() - oldest.getTime());
			System.out.println("setWertebereich: timelineLength: " + timelineLength);
		}

	}
}

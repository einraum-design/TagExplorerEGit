package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class Timeline {
	PImage zeitbereich_hover;
	PImage zeitbereich_wand;
	TagExplorerProcessing2 p5;
	PGraphics pg;
	Timestamp oldest = null;

	int timelineLength;
	int timelineMaxLength;
	float timelineScaleFactor = 4.0f;

	public Timeline(TagExplorerProcessing2 p5) {
		this.p5 = p5;

		pg = p5.createGraphics(200, p5.height);
		zeitbereich_hover = p5.loadImage("../data/zeitbereich_hover3.png");
		zeitbereich_wand = p5.loadImage("../data/zeitbereich_wand.png");

		// timelineLength = pg.height - 80;

		
		timelineMaxLength = 8000; // fix!
		timelineLength = timelineMaxLength;
	}

	public void render(PGraphics renderer) {
		if (oldest != null) {
			renderer.noStroke();
			renderer.fill(255, 150);

			renderer.pushMatrix();

			renderer.translate(renderer.width / 2 - 120, renderer.height / 2);

			renderer.beginShape();
			renderer.vertex(10, 0, 0);
			renderer.vertex(10, 0, -timelineLength);
			renderer.vertex(-10, 0, -timelineLength);
			renderer.vertex(-10, 0, 0);
			renderer.endShape(p5.CLOSE);

			// renderer.pushMatrix();

			renderer.textAlign(p5.CENTER);
			renderer.textFont(p5.font, 18);
			

			renderTime(renderer, 60L * 60 * 1000, "1 hour");
			renderTime(renderer, 24L * 60 * 60 * 1000, "1 day");
			renderTime(renderer, 7L * 24 * 60 * 60 * 1000, "1 week");
			//renderTime(renderer, 14L * 24 * 60 * 60 * 1000, "2 weeks");
			renderTime(renderer, 21L * 24 * 60 * 60 * 1000, "3 weeks");
			// renderTime(renderer, 28L * 24 * 60 * 60 * 1000, "4 weeks ");
			renderTime(renderer, 30L * 24 * 60 * 60 * 1000, "1 month");
			renderTime(renderer, 6L * 30 * 24 * 60 * 60 * 1000, "6 month");
			renderTime(renderer, 365L * 24 * 60 * 60 * 1000, "1 year");

			renderTime(renderer, System.currentTimeMillis() - oldest.getTime(), oldest.toGMTString());

			// renderer.popMatrix();
			renderer.popMatrix();

			// renderZeitbereich(renderer, 0, 24L * 60 * 60 * 1000); // jetzt
			// bis 1 Tag
			// renderZeitbereich(renderer, 7L * 24 * 60 * 60 * 1000, 14L * 24 *
			// 60 * 60 * 1000); // letzte woche
			// renderZeitbereich(renderer, 21L * 24 * 60 * 60 * 1000, 30L * 24 *
			// 60 * 60 * 1000); // 3. woche bis monatsende
			// renderZeitbereich(renderer, 180L * 24 * 60 * 60 * 1000, 365L * 24
			// * 60 * 60 * 1000); // Halbjahr bis Jahresende
			// renderZeitbereich(renderer, 730L * 24 * 60 * 60 * 1000, 1095L *
			// 24 * 60 * 60 * 1000); // Halbjahr bis Jahresende
			// renderZeitbereich(renderer, 180L * 24 * 60 * 60 * 1000, 365L * 24
			// * 60 * 60 * 1000); // Halbjahr bis Jahresende

			if (p5.minTime != null) {
				// minTime bis jetzt
				renderZeitbereich(renderer, 0, System.currentTimeMillis() - p5.minTime.getTime()); 
				// wand at minTime
//				if(p5.mousePressed){
				renderWall(renderer, System.currentTimeMillis() - p5.minTime.getTime());
//				}
				
				
				//renderTime(renderer, System.currentTimeMillis() - p5.minTime.getTime(), "letzter Besuch");
				
				renderer.pushMatrix();
				renderer.translate(renderer.width/2, renderer.height/2, -mapExpMillis(System.currentTimeMillis() - p5.minTime.getTime()));
//				renderer.rotateX(p5.xBillboardRotation);
//				renderer.rotateY(p5.yBillboardRotation);
				renderer.fill(0, 200, 200);
				renderer.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
				renderer.textFont(p5.font, 24);
				renderer.text("letzter Besuch", 0, 0);
//				renderer.rotateY(-p5.yBillboardRotation);
//				renderer.rotateX(-p5.xBillboardRotation);
				renderer.popMatrix();
			}

		}
	}
	
	public void renderWall(PGraphics renderer, long time) {
		// renderer.fill(0, 180);
		renderer.beginShape();

		// System.out.println("starttime" + -mapExpMillis(startTime));
		// System.out.println("endtime: " + -mapExpMillis(endTime));

		renderer.texture(zeitbereich_wand);
		renderer.vertex(-renderer.width / 2, -renderer.height / 2, -mapExpMillis(time), 0, 0);
		renderer.vertex(renderer.width / 2, -renderer.height / 2, -mapExpMillis(time), zeitbereich_hover.width, 0);
		renderer.vertex(renderer.width / 2, renderer.height / 2, -mapExpMillis(time), zeitbereich_hover.width,
				zeitbereich_hover.height);
		renderer.vertex(-renderer.width / 2, renderer.height / 2, -mapExpMillis(time), 0, zeitbereich_hover.height);
		renderer.endShape(PConstants.CLOSE);
		
		

		// renderer.translate(0, 0, mapExpMillis(startTime));
	}

	public void renderZeitbereich(PGraphics renderer, long startTime, long endTime) {
		// renderer.fill(0, 180);
		renderer.beginShape();

		// System.out.println("starttime" + -mapExpMillis(startTime));
		// System.out.println("endtime: " + -mapExpMillis(endTime));

		renderer.texture(zeitbereich_hover);
		renderer.vertex(-renderer.width / 2, renderer.height / 2, -mapExpMillis(startTime), 0, 0);
		renderer.vertex(renderer.width / 2, renderer.height / 2, -mapExpMillis(startTime), zeitbereich_hover.width, 0);
		renderer.vertex(renderer.width / 2, renderer.height / 2, -mapExpMillis(endTime), zeitbereich_hover.width,
				zeitbereich_hover.height);
		renderer.vertex(-renderer.width / 2, renderer.height / 2, -mapExpMillis(endTime), 0, zeitbereich_hover.height);
		renderer.endShape(PConstants.CLOSE);

		// renderer.translate(0, 0, mapExpMillis(startTime));
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
	
	// für timeChooser
	public float mapExp(Timestamp ts, int h) {
		// System.out.println("ts time: " + ts.getTime());
		long time = System.currentTimeMillis() - ts.getTime();
		// System.out.println("delta time: " + time);
		return mapExpMillis(time, h);
	}

	public float mapExpMillis(long time) {

		if (oldest == null) {
			return 0;
		}
		float val = PApplet.map(PApplet.sqrt(time / 1000), 0,
				PApplet.sqrt(System.currentTimeMillis() / 1000 - oldest.getTime() / 1000), 0, timelineLength);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}
	
	public float mapExpCam(float aktuellCamWert, int h){
		
		if (oldest == null) {
			return 0;
		}
		
		float val = PApplet.map(aktuellCamWert, 0, timelineLength, 0, h);
		//float val = PApplet.map(aktuellCamWert, (p5.height / 2.0f) / PApplet.tan(PConstants.PI * 30.0f / 180.0f), (p5.height / 2.0f) / PApplet.tan(PConstants.PI * 30.0f / 180.0f) + timelineLength, 0, h);
		return val;
	}
	
	public float mapExpMillis(long time, int h) {

		if (oldest == null) {
			return 0;
		}
		float val = PApplet.map(PApplet.sqrt(time / 1000), 0,
				PApplet.sqrt(System.currentTimeMillis() / 1000 - oldest.getTime() / 1000), 0, h);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

	public float mapExpSeconds(long time) {

		if (oldest == null) {
			return 0;
		}
		float val = PApplet.map(PApplet.sqrt(time), 0, PApplet.sqrt(System.currentTimeMillis() / 1000 - oldest.getTime() / 1000), 0,
				timelineLength);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

	public void setWertebereich(Timestamp ts) {
		if (ts != null) {
			oldest = ts;
			timelineLength = (int) (PApplet.sqrt((System.currentTimeMillis() - oldest.getTime()) / 10000) * timelineScaleFactor);
			System.out.println(System.currentTimeMillis() + " - " + oldest.getTime());
			System.out.println(System.currentTimeMillis() - oldest.getTime());
			System.out.println("setWertebereich: timelineLength: " + timelineLength);
		}

	}
}

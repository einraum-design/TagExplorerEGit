package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import toxi.geom.Vec2D;

public class TimeChooser extends Vec2D {

	TagExplorerProcessing2 p5;
	int w = 20;
	int h;

	PShape scala;

	CamMover camMover;

	public TimeChooser(TagExplorerProcessing2 p5, int x, int y) {
		super(x, y);

		this.p5 = p5;
		this.x = x;
		this.y = y;

		this.h = p5.mainscreen.height - y - (p5.mainscreen.height - (p5.contentStartUnten + p5.mainscreen.height/2)) + p5.planeSize/2;
		System.out.println("Timechooser.h " + h + " " + (p5.mainscreen.height - y) + " " +  (p5.mainscreen.height - (p5.contentStartUnten + p5.mainscreen.height/2)));

		scala = createScala();

		camMover = new CamMover(p5, x + w, (int) (this.y + this.h), h);
	}

	public void render() {

		// render Zeitbereich
		if (p5.minTime != null) {

			// blaues füll feld zeitbereich
			if(mouseOverScala()){
				p5.fill(p5.cBorderHover, 200);
			} else{
				p5.fill(p5.cBorderHover, 120);
			}
			p5.noStroke();

			p5.beginShape();
			p5.vertex(x, y + h);
			// p5.vertex(x, y + h - p5.timeline.mapExp(p5.minTime, h));
			p5.vertex(x, y + h - mapExp(p5.minTime, h));
			// p5.vertex(x + w, y + h - p5.timeline.mapExp(p5.minTime, h));
			p5.vertex(x + w, y + h - mapExp(p5.minTime, h));
			p5.vertex(x + w, y + h);
			p5.endShape(PConstants.CLOSE);

			//
			p5.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
			p5.textFont(p5.font, 16);

			p5.fill(p5.cTagDark, 120);
			p5.noStroke();

			int fieldHeight = 20;

			float xFeld = x - p5.textWidth("letzter Besuch") - fieldHeight - 7; // 7
																				// ist
																				// textabstand
																				// zu
																				// closeButton
			// float yFeld = y + h - p5.timeline.mapExp(p5.minTime, h) -
			// fieldHeight;
			float yFeld = y + h - mapExp(p5.minTime, h) - fieldHeight;

			// MinTime Feld
			p5.beginShape();

			p5.vertex(xFeld, yFeld);
			p5.vertex(x - 15, yFeld);
			p5.vertex(x, yFeld + fieldHeight);
			p5.vertex(xFeld, yFeld + fieldHeight);

			p5.endShape(PConstants.CLOSE);

			p5.sdf.applyPattern("dd. MM. yyyy");
			// p5.text(p5.sdf.format(minTime), x - w / 2 + rand, y - 40);
			p5.fill(p5.cBorderHover);

			// p5.text(p5.sdf.format(p5.minTime), x - 20, y + h -
			// p5.timeline.mapExp(p5.minTime, h) - 2);
			p5.text(p5.sdf.format(p5.minTime), x - 20, y + h - mapExp(p5.minTime, h) - 2);

			Button_Symbol closeButtonMinTime = new Button_Symbol(p5, "close", (int) (xFeld + fieldHeight / 2.0f),
					(int) (yFeld + fieldHeight / 2.0f));
			closeButtonMinTime.render();
			if (p5.clickNextFrame && closeButtonMinTime.mouseOver()) {
				p5.minTime = null;
				p5.clickNextFrame = false;
				p5.updateShowFiles();
				p5.updateTags();
			}

		}

		// draw CameraMove
		if (p5.setZTimeAxis) {
			camMover.render();
		}

		// draw Skala
		p5.shape(scala);

		// setzte Zeitbereich
		if (mouseOverScala() && p5.mousePressed) {

			int timelineLength = p5.timeline.timelineMaxLength;

			// float yVal = PApplet.map(p5.mouseY, y + h, y, 0,
			// p5.timeline.timelineLength);
			float yVal = PApplet.map(p5.mouseY, y + h, y, 0, timelineLength);

			// System.out.println("mouseY: " + p5.mouseY + " " + (y + h) + " " +
			// (y) + "yVal: " + yVal);

			if (p5.timeline.oldest != null) {
				// float val = PApplet.map(yVal, 0, p5.timeline.timelineLength,
				// 0, PApplet.sqrt(System.currentTimeMillis() -
				// p5.timeline.oldest.getTime()));
				float val = PApplet.map(yVal, 0, timelineLength, 0,
						PApplet.sqrt(System.currentTimeMillis() - p5.oldest_File.creation_time.getTime()));

				// System.out.println("val: " + val);

				val *= val; // quadriere
				p5.minTime = new Timestamp(System.currentTimeMillis() - (long) val);
			} else {
				p5.minTime = null;
				p5.updateShowFiles();
			}

		}
	}

	public PShape createScala() {
		// PShape group = p5.createShape(PConstants.GROUP);

		PShape lines = p5.createShape(PConstants.LINES);
		lines.stroke(255);
		// lines.strokeWeight(1);

		for (int i = 0; i * 10 < h; i++) {
			lines.vertex(x, y + i * 10); // , 0.01f
			lines.vertex(x + 20, y + i * 10);
		}

		lines.end();

		// group.addChild(who);

		return lines;
	}

	// public boolean mouseOver() {
	// boolean over = false;
	//
	// // referenz links oben
	// if (p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y
	// + h) {
	// over = true;
	// }
	// return over;
	// }

	public boolean mouseOverScala() {
		boolean over = false;

		int zusatz = 50;
		// referenz links oben
		if (p5.mouseX >= x - zusatz && p5.mouseX < x + w && p5.mouseY > y && p5.mouseY < y + h) {
			over = true;
		}
		return over;
	}

	public float mapExp(Timestamp ts, int h) {
		// System.out.println("ts time: " + ts.getTime());
		long time = System.currentTimeMillis() - ts.getTime();
		// System.out.println("delta time: " + time);
		return mapExpMillis(time, h);
	}

	public float mapExpMillis(long time, int h) {

		if (p5.oldest_File == null) {
			return 0;
		}
		float val = PApplet.map(PApplet.sqrt(time / 1000), 0,
				PApplet.sqrt(System.currentTimeMillis() / 1000 - p5.oldest_File.creation_time.getTime() / 1000), 0, h);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

}

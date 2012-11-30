package tagexplorer;

import processing.core.PApplet;
import toxi.physics.VerletParticle;

public class VerletParticleTag extends VerletParticle {
	PApplet p5;
	Tag boundTag = null;

	public VerletParticleTag(PApplet p5) {
		super(0, 0, 0);
		this.p5 = p5;
		// TODO Auto-generated constructor stub
	}

	public VerletParticleTag(PApplet p5, float x, float y, float z) {
		super(x, y, z);
		this.p5 = p5;
	}

	public VerletParticleTag(PApplet p5, float x, float y, float z, Tag boundTag) {
		super(x, y, z);
		this.boundTag = boundTag;
		this.p5 = p5;
	}

	public void draw() {
		p5.strokeWeight(5);

		if (isLocked()) {
			p5.stroke(255, 0, 0);
		} else {
			p5.stroke(0, 255, 200);
		}
		p5.point(x, y);
		if (mouseOver(30, 30)) {
			p5.textAlign(p5.LEFT);
			p5.text(getTag().name, x + 10, y);
		}
	}

	public void setBoundTag(Tag t) {
		this.boundTag = t;
	}

	public Tag getTag() {
		return boundTag;
	}

	boolean mouseOver(int x, int y, int w, int h) {
		boolean over = false;
		if (p5.mouseX > x - w / 2.0f && p5.mouseX < x + w / 2.0f
				&& p5.mouseY > y - h / 2.0f && p5.mouseY < y + h / 2.0f) {
			over = true;
		}
		return over;
	}

	boolean mouseOver(int w, int h) {
		boolean over = false;
		if (p5.mouseX > x - w / 2.0f && p5.mouseX < x + w / 2.0f
				&& p5.mouseY > y - h / 2.0f && p5.mouseY < y + h / 2.0f) {
			over = true;
		}
		return over;
	}
}

package tagexplorerprocessing2;

import g4p_controls.GButton;
import g4p_controls.GEditableTextControl;
import g4p_controls.GEvent;
import g4p_controls.GPanel;
import g4p_controls.GTextField;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import tagexplorerprocessing2.Connection.ConnectionType;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

public class TagExplorerProcessing2 extends PApplet {

	WatchDir watcher;

	PGraphics mainscreen;
	Timeline timeline;

	// Tag_User user = new Tag_User("users", 0, "noname", "no Password");
	Tag_User user = null;
	Tag_Location location = null;

	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	// ControlP5 cp5_Menu;

	ArrayList<Tag> attributes = null;
	ArrayList<Tag> files = null;
	ArrayList<Tag> showFiles = null;
	Tag_File oldest_showFile = null;
	
	Timestamp oldestTime;

	ArrayList<Filter> filters = new ArrayList<Filter>();

	// Interaction
	Tag startTag = null;

	// Camera
	Vec3D cam_eye;
	Vec3D cam_target;
	Vec3D cam_up;

	float xBillboardRotation = 0;
	float yBillboardRotation = 0;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;
	VerletPhysics filePhysics;

	// PeasyCam cam;
	boolean _3d = true;

	PShape ball;

	String[] imageExtension = { "jpg", "jpeg", "png", "gif", "psd", "tif", "tiff", "bmp", "tga" };
	String[] vectorExtension = { "ai", "drw", "eps", "ps", "svg" };
	String[] layoutExtension = { "indd", "pdf", "pxd", "pxp" };
	String[] audioExtension = { "aif", "m4a", "mid", "mp3", "mpa", "wav", "wma" };
	String[] videoExtension = { "avi", "flv", "mov", "mp4", "mpg", "swf", "wmv", "vob" };
	String[] textExtension = { "txt", "doc", "docx", "log", "pages", "rtf" };
	String[] webExtension = { "css", "htm", "html", "js", "jsp", "php", "rss", "xhtml" };
	String[] fontExtension = { "fnt", "fon", "otf", "ttf" };
	String[] messageExtension = { "msg", "eml" };

	// public void init() {
	// frame.removeNotify();
	// frame.setUndecorated(true);
	// // frame.setAlwaysOnTop(true);
	// frame.addNotify();
	// super.init();
	// }

	public void setup() {
		size(800, 600, P3D);
		// frame.setLocation(1970, 50);

		font = createFont("arial", 20);
		ball = createShape(SPHERE, 10);
		ball.noStroke();
		ball.fill(155, 100);
		ball.emissive(150, 0, 0);

		// ball.shininess(0);

		Path p = FileSystems.getDefault().getPath("/Users/manuel/Documents/Testumgebung/Test");

		// FileSystem Watcher
		try {
			watcher = new WatchDir(this, p, true);
			watcher.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// SQL HELPER
		SQL = new SQLhelper(this);

		mainscreen = createGraphics(width, height - 40, P3D);
		// mainscreen.smooth(4);

		// camera
		cam_eye = new Vec3D(mainscreen.width / 2.0f, mainscreen.height / 2.0f - 250, (mainscreen.height / 2.0f)
				/ tan(PI * 30.0f / 180.0f));
		cam_target = new Vec3D(mainscreen.width / 2.0f, mainscreen.height / 2.0f, 0);
		cam_up = new Vec3D(0, 1, 0);

		mainscreen.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x, cam_target.y, cam_target.z, cam_up.x,
				cam_up.y, cam_up.z);
		// cam = new PeasyCam(this, width / 2, height / 2, 0, 100);

		// TIMELINE
		timeline = new Timeline(this);

		// Standartuser: …ffentlich
		// user = (Tag_User) SQL.queryTagList("users").get(0);

		// ControlP5
		cp5_Promt = new ControlP5(this);
		// cp5_Menu = new ControlP5(this);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// toxi VerletPhysics
		physics = new VerletPhysics();
		// GravityBehavior g = new GravityBehavior(new Vec3D(0, 0, -0.01f));
		// physics.addBehavior(g);
		filePhysics = new VerletPhysics();

		// erst Tags, dann Files!
		updateTags();
		initFiles();
		updateShowFiles();
		updateSprings();

		// Display settings
		textFont(font, 14);

		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent mwe) {
				mouseWheel(mwe.getWheelRotation());
			}
		});
	}

	// /////////// draw ////////////////////

	public void draw() {

		calcBillboardRotation();

		// control p5
		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		// draw mainscene
		drawMainscreen(mainscreen);

		// draw timeline right
		// timeline.draw();
		// image(timeline.pg, width - 200, 0);

		fill(150);
		if (user != null) {
			text("User: " + user.name, 5, 16);
		}

		if (location != null) {
			text("Location: " + location.name, 150, 16);
		}

		text(frameRate, width - 120, 16);

		// Promt Messages
		if (p != null) {
			p.showPromt();
		}

		// reset mouseHover & interation status
		interaction = false;
		hoverPoint = null;

		physics.update();
		filePhysics.update();

	}

	float mainScreenYRotation = 0;

	private void drawMainscreen(PGraphics renderer) {

		// renderer begin:
		renderer.beginDraw();

		// turn lights on
		renderer.lights();
		renderer.directionalLight(0, 255, 0, 0, -1, 0);

		// renderer.rotateY(mainScreenYRotation);
		// renderer.translate(-50, 0, 0);

		renderer.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x, cam_target.y, cam_target.z, cam_up.x, cam_up.y,
				cam_up.z);

		renderer.pushMatrix();
		renderer.translate(renderer.width / 2, renderer.height / 2, 0);

		renderer.background(0);

		// grundplatte
		// int delta = 250;
		// renderer.noStroke();
		// renderer.fill(255, 80);
		// renderer.beginShape();
		// renderer.vertex(delta, 0, delta);
		// renderer.vertex(-delta, 0, delta);
		// renderer.vertex(-delta, 0, -delta);
		// renderer.vertex(delta, 0, -delta);
		// renderer.endShape(CLOSE);

		drawFiles(renderer);
		drawTags(renderer);
		drawSprings(renderer);

		// draw interaction
		stroke(255, 0, 0);
		if (startTag != null) {
			renderer.strokeWeight(15);
			renderer.point(startTag.x, startTag.y, startTag.z);
			if (hoverPoint != null) {
				renderer.strokeWeight(3);
				renderer.line(startTag.x, startTag.y, startTag.z, hoverPoint.x, hoverPoint.y, hoverPoint.z);
			}
		}

		timeline.render(renderer);

		renderer.popMatrix();
		renderer.endDraw();
		// renderer end

		image(renderer, 0, 0);
	}

	// ///////// display Methods ////////////////////

	public void drawFiles(PGraphics renderer) {
		if (filePhysics.particles != null) {
			for (int i = 0; i < filePhysics.particles.size(); i++) {
				Tag_File vp = (Tag_File) filePhysics.particles.get(i);

				renderer.strokeWeight(5);

				if (vp.isLocked()) {
					renderer.stroke(255, 0, 0);
				} else {
					renderer.stroke(0, 255, 200);
				}

				// renderer.point(vp.x, vp.y, vp.z);

				renderer.pushMatrix();
				renderer.translate(vp.x, vp.y, vp.z);

				renderer.shape(ball);

				renderer.popMatrix();

				if (mouseOver(renderer, vp, 30, 30)) {
					renderer.textAlign(LEFT);

					if (_3d) {
						renderer.pushMatrix();
						renderer.translate(vp.x, vp.y, vp.z);
						// float[] rota = cam.getRotations();
						// rotateX(rota[0]);
						// rotateY(rota[1]);
						// rotateZ(rota[2]);
					}

					renderer.rotateX(xBillboardRotation);
					renderer.rotateY(yBillboardRotation);
					renderer.fill(255);
					renderer.text(vp.viewName, 10, 0);
					renderer.text(vp.creation_time.toGMTString(), 10, 20);
					if (_3d) {
						renderer.popMatrix();
					}
				}

			}
		}
	}

	public void drawTags(PGraphics renderer) {
		for (int i = 0; i < physics.particles.size(); i++) {
			Tag vp = (Tag) physics.particles.get(i);
			renderer.strokeWeight(5);

			if (vp.isLocked()) {
				renderer.stroke(255, 0, 0);
			} else {
				renderer.stroke(0, 255, 200);
			}
			renderer.point(vp.x, vp.y, vp.z);

			if (mouseOver(renderer, vp, 30, 30)) {
				renderer.textAlign(LEFT);
				if (_3d) {
					renderer.pushMatrix();
					renderer.translate(vp.x, vp.y, vp.z);
					// float[] rota = cam.getRotations();
					// rotateX(rota[0]);
					// rotateY(rota[1]);
					// rotateZ(rota[2]);
				}
				renderer.rotateX(xBillboardRotation);
				renderer.rotateY(yBillboardRotation);
				renderer.fill(255);
				renderer.text(vp.name, 10, 0);
				if (_3d) {
					renderer.popMatrix();
				}
			}
		}
	}

	public void drawSprings(PGraphics renderer) {
		PShape shape = createShape(GROUP);

		for (int i = 0; i < physics.springs.size(); i++) {
			Connection sp = (Connection) physics.springs.get(i);

			PShape s = createArc(sp.a, sp.b);

			switch (sp.type) {
			case VERSION:
				s.stroke(255, 0, 0);
				break;
			case FILEBINDING:
				s.stroke(0, 255, 0);
				break;
			case TAGBINDING:
				s.stroke(0, 0, 255);
				break;
			}

			shape.addChild(s);

			// renderer.stroke(255);
			// renderer.strokeWeight(1);
			// renderer.line(sp.a.x, sp.a.y, sp.a.z, sp.b.x, sp.b.y, sp.b.z);
		}

		// filePhysics hat keine Springs
		// for (int i = 0; i < filePhysics.springs.size(); i++) {
		// Connection sp = (Connection) filePhysics.springs.get(i);
		//
		// PShape s = createArc(sp.a, sp.b);
		//
		// switch (sp.type) {
		// case VERSION:
		// s.stroke(255, 255, 0);
		// break;
		// case FILEBINDING:
		// s.stroke(0, 255, 255);
		// break;
		// case TAGBINDING:
		// s.stroke(255, 0, 255);
		// break;
		// }
		//
		// shape.addChild(s);
		//
		// }
		renderer.shape(shape);
	}

	PShape createArc(Vec3D vec1, Vec3D vec2) {
		float kappa = (4.0f * (sqrt(2.0f) - 1.0f) / 3.0f);

		float dist = vec1.distanceTo(vec2) / 2;
		Vec3D ab = (vec1.sub(vec2)).scale(0.5f);
		Vec3D middle = (vec1.add(vec2)).scale(0.5f);

		Vec3D peak = new Vec3D(middle.x, middle.y - dist, middle.z);
		Vec3D anfasser1 = new Vec3D(peak.x, peak.y, peak.z);
		anfasser1.addSelf(ab.scale(kappa));
		Vec3D anfasser2 = new Vec3D(peak.x, peak.y, peak.z);
		anfasser2.subSelf(ab.scale(kappa));

		PShape myLine = createShape();
		myLine.stroke(0, 250, 200);
		myLine.noFill();
		myLine.strokeWeight(3);

		myLine.vertex(vec1.x, vec1.y, vec1.z);
		myLine.bezierVertex(vec1.x, vec1.y - dist * kappa, vec1.z, anfasser1.x, anfasser1.y, anfasser1.z, peak.x,
				peak.y, peak.z);
		myLine.bezierVertex(anfasser2.x, anfasser2.y, anfasser2.z, vec2.x, vec2.y - dist * kappa, vec2.z, vec2.x,
				vec2.y, vec2.z);

		myLine.end();
		return (myLine);
	}

	// ///////// init Methods //////////////////////
	public void initFiles() {
		ArrayList<Tag> files = new ArrayList<Tag>();
		files = SQL.queryTagList("files");
		this.files = files;

		for (Tag t : this.files) {
			// set AttributeBindings
			updateFileTagBinding((Tag_File) t);
			// set FileBindings
			updateFileFileBinding((Tag_File) t);
			// set VersionBindings
			updateVersionBinding((Tag_File) t);
		}
		
		oldestTime = ((Tag_File) getOldestTagFile(files)).creation_time;
	}

	// ///////// update Methods ////////////////////
	public void updateShowFiles() {
		System.out.println("updateShowFiles()");

		// filter files
		if (filters.size() > 0) {
			// ArrayList<Tag> files = SQL.queryTagListFiltered("files",
			// filters.get(0));
			// showFiles = files;
			// showFiles = SQL.queryTagListFiltered("files", filters);
			showFiles = SQL.queryTagListFiltered("files", filters);
		} else {
			// alle files
			showFiles = files;
		}

		oldest_showFile = (Tag_File) getOldestTagFile(showFiles);
		oldestTime = ((Tag_File) getOldestTagFile(files)).creation_time;

		if (oldest_showFile != null) {
			timeline.setWertebereich(oldest_showFile.creation_time);
		} else {
			timeline.oldest = null;
		}

		setParticlesPosition(filePhysics, showFiles);
	}

	public void updateTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		tags = SQL.queryTagList("keywords");
		tags.addAll(SQL.queryTagList("locations"));
		tags.addAll(SQL.queryTagList("projects"));
		tags.addAll(SQL.queryTagList("users"));

		// tags.addAll(SQL.queryTagList("files"));
		// tags nicht Ÿberscheiben, sondern nur abgleichen!
		this.attributes = tags;

		physics.particles.clear();

		int count = tags.size();
		float dist;
		if (count > 1) {
			dist = ((float) height - 40) / (count - 1);
		} else {
			dist = 0;
		}

		for (int i = 0; i < tags.size(); i++) {
			dropParticles(physics, i * dist - (dist * (count - 1) / 2), 0, 50, tags.get(i));
			// dropParticles(physics, width - 150, i * dist + 20, 0,
			// tags.get(i));
		}
	}

	public void updateSprings() {

		physics.springs.clear();

		for (Tag tf : showFiles) {
			Tag_File file = (Tag_File) tf;

			// File -> Tag Springs:
			if (file.attributeBindings.size() > 0) {
				for (Tag t : file.attributeBindings) {
					dropSpring(physics, file, t, ConnectionType.TAGBINDING);
				}
			}

			// File -> File Springs:
			if (file.fileBindings.size() > 0) {
				for (Tag t : file.fileBindings) {
					dropSpring(physics, file, t, ConnectionType.FILEBINDING);
				}
			}

			// File -> File Springs:
			if (file.versionBindings.size() > 0) {
				for (Tag t : file.versionBindings) {
					dropSpring(physics, file, t, ConnectionType.VERSION);
				}
			}
		}
	}

	private void setParticlesPosition(VerletPhysics physics, ArrayList<Tag> files) {
		// drop Particles
		// set Position
		physics.particles.clear();

		int count = getSizeWithoutVersion(files);

		float dist;
		if (count > 1) {
			// dist = ((float) height - 40) / (count - 1);
			// dist = ((float) width - 40) / (count - 1);
			dist = 60;
		} else {
			dist = 0;
		}

		int shiftCount = 0;
		for (int i = 0; i < files.size(); i++) {

			// set x & y wert nach parent.
			if (((Tag_File) files.get(i)).parent_ID != 0) {
				// get parent
				Tag_File parent = (Tag_File) getTagByID(files.get(i).type, ((Tag_File) files.get(i)).parent_ID);

				dropParticles(physics, parent.x, parent.y, 0, files.get(i));

				// springs to partent anhand parent file
				// dropSpring(physics, (Tag_File) files.get(i), parent);
			}

			// ist origin File Version
			else {
				dropParticles(physics, shiftCount * dist - (dist * (count - 1) / 2), 0, 0, files.get(i)); // links/rechts
																											// von
																											// 0
				shiftCount++;
			}
		}

		// set z position creation time
		setZAccessTime();
	}

	public void dropParticles(VerletPhysics physics, float x, float y, float z, Tag t) {
		t.x = x;
		t.y = y;
		t.z = z;
		t.lock();
		physics.addParticle(t);
	}

	float LEN = 400; // 10
	float STR = 0.01f; // 0.01f

	public void dropSpring(VerletPhysics physics, VerletParticle fileParticle, VerletParticle attributeParticle,
			Connection.ConnectionType type) {
		Connection sp = new Connection(fileParticle, attributeParticle, LEN, STR, type);
		physics.addSpring(sp);
	}

	void setZAccessTime() {
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;

			println("aktuelle anzeige file:" + file.name);
			file.z = -timeline.mapExp(getNewestDate(file), 150);
		}
	}

	private Tag getTagByID(String tableName, int id) {
		Tag tag = null;
		// files:
		if (tableName == "files" && files != null) {
			for (Tag _tag : files) {
				if (_tag.id == id) {
					tag = _tag;
				}
			}
		}
		// attributes
		else if (tableName != "files" && attributes != null) {
			for (Tag _tag : attributes) {
				if (_tag.id == id && _tag.type.equals(tableName)) {
					tag = _tag;
				}
			}
		}
		return tag;
	}

	private ArrayList<Tag> getAllVersions(int originId) {
		ArrayList<Tag> files = new ArrayList<Tag>();

		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			if (file.id == originId || file.origin_ID == originId) {
				files.add(file);
			}
		}

		return files;
	}

	private int getSizeWithoutVersion(ArrayList<Tag> files) {
		int count = 0;
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			if (file.parent_ID == 0) {
				count++;
			}
		}
		return count;
	}

	private Tag getOldestTagFile(ArrayList<Tag> files) {
		Tag oldest = null;
		Timestamp comp = new Timestamp(System.currentTimeMillis());
		for (Tag file : files) {
			println("getOldestTagFile: file.name: " + file.name);
			if (((Tag_File) file).creation_time.before(comp)) {
				comp = ((Tag_File) file).creation_time;
				oldest = file;
			}
		}
		return oldest;
	}

	Timestamp getNewestDate(Tag_File file) {
		Timestamp newest = file.creation_time;
		// if (file.expiration_time != null &&
		// file.expiration_time.after(newest)) {
		// newest = file.expiration_time;
		// }

		// ////// Time /////// Changes !!!!!!!!!!!!!!

		if (file.delete_time != null && file.delete_time.after(newest)) {
			newest = file.delete_time;
		}

		System.out.println("getNewestDate: " + newest.toGMTString());
		return newest;
	}

	void updateFileTagBinding(Tag_File file) {
		file.setAttributeBindings(SQL.getBindedTagList(file));
		file.updateViewName();
	}

	void updateFileFileBinding(Tag_File file) {
		file.setFileBindings(SQL.getBindedFileList(file, ConnectionType.FILEBINDING));
		// file.updateViewName();
	}

	void updateVersionBinding(Tag_File file) {
		file.setVersionBindings(SQL.getBindedFileList(file, ConnectionType.VERSION));
		// file.updateViewName();
	}

	PShape generateShape(Tag_File file) {

		// file.creation_time;

		PShape s = createShape();

		s.fill(0, 0, 255);
		s.noStroke();
		s.vertex(0, 0);
		s.vertex(0, 50);
		s.vertex(50, 0);
		s.end();

		return s;
	}

	public float mapExp(long time) {
		float val = map(sqrt(time), 0, sqrt(System.currentTimeMillis() - oldestTime.getTime()), 0, 1);
		// System.out.println("val: " + p5.sqrt(time));
		return val;
	}

	// ///////// INPUT ///////////////////
	public void mousePressed() {
		for (Tag t : showFiles) {

			if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
				startTag = t;
			}
		}
		for (Tag t : attributes) {
			if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
				startTag = t;
			}
		}

		if (b != null) {
			println("dispose");
			// PromtNewFile pro = promts.get(0);
			b.dispose();
			b = null;

			// pro = null;
			// pro.markForDisposal();
			// promts.remove(p);
		}
	}

	public void handleTextEvents(GEditableTextControl textcontrol, GEvent event) {
		println("textEvent");
	}

	// public void mouseDragged() {
	// if (mouseOver(50, 50, 100, 100)) {
	// mainScreenYRotation += 0.01;
	// System.out.println("mainScreenYRotation: " + mainScreenYRotation);
	// }
	// }

	public void mouseReleased() {
		if (startTag != null) {
			if (startTag instanceof Tag_File) {
				// File -> Attribute
				for (Tag t : attributes) {
					if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
						Tag_File file = (Tag_File) startTag;
						SQL.bindTag(file, t);
						updateFileTagBinding(file);
						updateTags();
						updateSprings();
					}
				}

				// File -> File
				for (Tag t : files) {
					if (t.id != startTag.id
							&& mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30,
									30)) {
						Tag_File file = (Tag_File) t;

						Timestamp t1 = getNewestDate(file);
						Timestamp t2 = getNewestDate((Tag_File) startTag);

						// oldest zu erst!
						if (t1.before(t2)) {
							SQL.bindFile(file, (Tag_File) startTag, ConnectionType.FILEBINDING);
							updateFileFileBinding(file);
						} else {
							SQL.bindFile((Tag_File) startTag, file, ConnectionType.FILEBINDING);
							updateFileFileBinding((Tag_File) startTag);
						}

						// updateFileTags(file);
						// updateTags();

						updateSprings();
					}
				}
			} else {
				// Attribute -> File
				for (Tag t : files) {
					if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
						Tag_File file = (Tag_File) t;
						SQL.bindTag(file, startTag);
						updateFileTagBinding(file);
						updateTags();
						updateSprings();
					}
				}
			}
		}
		startTag = null;

		// if(b != null){
		// b = null;
		// b.markForDisposal();
		//
		// // b.dispose();
		// }
	}

	// boolean mouseOver(float x, float y, int w, int h) {
	// boolean over = false;
	// if (mouseX > x - w / 2.0f && mouseX < x + w / 2.0f
	// && mouseY > y - h / 2.0f && mouseY < y + h / 2.0f) {
	// over = true;
	// }
	// return over;
	// }

	boolean interaction = false;
	Vec3D hoverPoint = null;

	boolean mouseOver(int x, int y, int w, int h) {
		boolean over = false;
		if (mouseX > x - w / 2.0f && mouseX < x + w / 2.0f && mouseY > y - h / 2.0f && mouseY < y + h / 2.0f) {
			over = true;
		}
		return over;
	}

	boolean mouseOver(float x, float y, float z, int w, int h) {
		boolean over = false;

		float screenX = screenX(x, y, z);
		float screenY = screenY(x, y, z);

		if (mouseX > screenX - w / 2.0f && mouseX < screenX + w / 2.0f && mouseY > screenY - h / 2.0f
				&& mouseY < screenY + h / 2.0f) {
			over = true;
			interaction = true;
			hoverPoint = new Vec3D(x, y, z);
		}
		return over;
	}

	boolean mouseOver(Vec3D t, int w, int h) {
		return mouseOver(t.x, t.y, t.z, w, h);
	}

	boolean mouseOver(PGraphics renderer, float x, float y, float z, int w, int h) {
		boolean over = false;

		float screenX = renderer.screenX(x, y, z);
		float screenY = renderer.screenY(x, y, z);

		if (mouseX > screenX - w / 2.0f && mouseX < screenX + w / 2.0f && mouseY > screenY - h / 2.0f
				&& mouseY < screenY + h / 2.0f) {
			over = true;
			interaction = true;
			hoverPoint = new Vec3D(x, y, z);
		}

		return over;
	}

	boolean mouseOver(PGraphics renderer, Vec3D t, int w, int h) {
		return mouseOver(renderer, t.x, t.y, t.z, w, h);
	}

	public void keyPressed() {

		switch (key) {
		case 'O':
			selectInput("Select a file to process:", "fileSelected");
			break;
		case 'U':
			// Set User
			user = (Tag_User) SQL.queryTagList("users").get(1);
			filters.clear();
			filters.add(new Filter(user, true));
			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'I':
			// Set User
			user = (Tag_User) SQL.queryTagList("users").get(2);
			filters.clear();
			filters.add(new Filter(user, true));
			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'L':
			createPromt("locations");
			break;
		case 'K':
			createPromt("keywords");
			break;
		case 'P':
			createPromt("projects");
			break;
		case 'T':
			// Bind File - Tag
			Tag_File file = (Tag_File) showFiles.get(0);

			// beispiel tag lšschen!
			Tag tag = new Tag_Location("locations", 5, "Ort", "coordinaten");
			SQL.bindTag(file, tag);
			updateTags();
			updateSprings();
			break;
		case 'F':
			filters.clear();
			System.out.println(attributes.get(0).name + " " + attributes.get(0).type);
			System.out.println(attributes.get(1).name + " " + attributes.get(1).type);
			filters.add(new Filter(attributes.get(0), true));
			filters.add(new Filter(attributes.get(1), false));

			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'C':
			b = new GTextField(this, 0, 0, 100, 100);
			// PromtNewFile p = new PromtNewFile(this);
			// promts.add(p);
			break;
		case 'D':

			// if (promts.size() > 0) {
			// println("dispose");
			// PromtNewFile pro = promts.get(0);
			// pro.dispose();
			// pro = null;
			//
			//
			// // pro = null;
			// // pro.markForDisposal();
			// // promts.remove(p);
			// }
			break;
		}
	}

	GTextField b = null;
	ArrayList<PromtNewFile> promts = new ArrayList<PromtNewFile>();

	// ///////////// Tag handling /////////////////////

	// ////////// Tag Creation /////////////////////
	public Tag_File createNewFile(String tableName, File inputFile) {
		Tag_File file = null;
		String s = inputFile.getAbsolutePath().trim();
		if (SQL.inDataBase(tableName, s)) {
			System.out.println("Tag " + s + " is already imported in " + tableName);
		} else {
			file = (Tag_File) SQL.createDbTag(tableName, s);
		}
		return file;
	}

	public Tag_File createNewFile(String tableName, Path path) {
		Tag_File file = null;
		String s = path.toString().trim();
		if (SQL.inDataBase(tableName, s)) {
			System.out.println("Tag " + s + " is already imported in " + tableName);
		} else {
			file = (Tag_File) SQL.createDbTag(tableName, s);
		}
		return file;
	}

	public void fileSelected(File selection) {

		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			// println("Create new File: url: " + url);

			Tag_File file = createNewFile("files", selection);

			if (file != null) {
				if (user != null) {
					// System.out.println(file.toString());
					SQL.bindTag(file, user);
				}

				// update File
				updateFileTagBinding(file);
				files.add(file);
				updateShowFiles();
				updateSprings();
			}
		}

	}

	public void locationInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();
		String tableName = "locations";

		if (theText.equals("Type Location name here") || theText.equals("")) {
			p.message = "Enter Locationname";
		} else if (SQL.inDataBase(tableName, theText)) {
			p.message = "Location " + theText + " already exists";
		} else {
			SQL.createDbTag(tableName, theText);
			// update Tags & Springs
			updateTags();
			updateSprings();

			removeController();

			// update
			updateTags();
			updateSprings();
		}
	}

	public void projectInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();
		String tableName = "projects";

		if (theText.equals("Type Projects name here") || theText.equals("")) {
			p.message = "Enter Locationname";
		} else if (SQL.inDataBase(tableName, theText)) {
			p.message = "Project " + theText + " already exists";
		} else {
			SQL.createDbTag(tableName, theText);

			// update
			updateTags();
			updateSprings();

			removeController();
		}
	}

	public void keywordInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();

		String tableName = "keywords";

		if (theText.equals("Keyword") || theText.equals("")) {
			p.message = "Enter Keyword";
		} else if (SQL.inDataBase(tableName, theText)) {
			p.message = "Keyword " + theText + " already exists";
		} else {
			SQL.createDbTag(tableName, theText);

			// update
			updateTags();
			updateSprings();

			removeController();
		}
	}

	public void handleButtonEvents(GButton button, GEvent event) {
		println("buttonEvent: " + event.toString());

		for (PromtNewFile p : promts) {

			if (button == p.btnCancel) {

				return;
			}
		}
	}

	public void handlePanelEvents(GPanel panel, GEvent event) {
		println("panelEvent: " + event.toString());
		for (PromtNewFile p : promts) {

			if (panel == p) {
				println("hello p");
			}
		}
	}

	// ///////////// Promt Location ////////////
	Promt p = null;
	Boolean bSaveActive = false;
	Boolean bCancelActive = false;

	public void createPromt(String type) {
		removeController();

		if (type.equals("locations")) {
			p = new Promt(this, cp5_Promt, "locationInput");
			println("locationinput created");
		} else if (type.equals("keywords")) {
			p = new Promt(this, cp5_Promt, "keywordInput");
			println("keywordinput created");
		} else if (type.equals("projects")) {
			p = new Promt(this, cp5_Promt, "projectInput");
			println("keywordinput created");
		}
	}

	// nur ein Textfield erlaubt, sonst unterscheidung beim submit!
	public void save(float value) {
		System.out.println("trigger save!");
		if (bSaveActive) {
			List l = cp5_Promt.getAll();
			for (Object o : l) {
				// System.out.println(o.toString());
				if (o instanceof controlP5.Textfield) {
					Textfield t = (Textfield) o;

					// if locationInput
					// if (t.getLabel().equals("LOCATIONINPUT")) {
					t.submit();
					System.out.println("submitted");
					break;
				}
			}
		} else {
			// System.out.println("setActive: save");
			bSaveActive = true;
		}
		// System.out.println("end save");
	}

	boolean removeController = false;

	public void cancel(float value) {
		System.out.println("trigger cancel!");
		if (bCancelActive) {
			// remove all controller
			removeController = true;
		} else {
			bCancelActive = true;
		}
		// System.out.println("end cancel");
	}

	public void removeController() {
		List l = cp5_Promt.getAll();
		for (Object ob : l) {
			((Controller) ob).remove();
		}
		bSaveActive = false;
		bCancelActive = false;
		p = null;
		System.out.println("removed Controller");
	}

	void calcBillboardRotation() {

		Vec3D cam = cam_eye.sub(cam_target);

		Vec3D cam_yz = new Vec3D(0, cam.y, cam.z).normalize();
		Vec3D cam_xz = new Vec3D(cam.x, 0, cam.z).normalize();

		if (cam.y >= 0) {
			xBillboardRotation = -cam_yz.angleBetween(new Vec3D(0, 0, 1));
		} else {
			xBillboardRotation = cam_yz.angleBetween(new Vec3D(0, 0, 1));
		}
		if (cam.x >= 0) {
			yBillboardRotation = cam_xz.angleBetween(new Vec3D(0, 0, 1));
		} else {
			yBillboardRotation = -cam_xz.angleBetween(new Vec3D(0, 0, 1));
		}

		if (cam.z < 0) {
			xBillboardRotation += PI;
			yBillboardRotation *= -1;
		}

		// println("x:rotation: " + xBillboardRotation);
		// println("y:rotation: " + yBillboardRotation);
	}

	void mouseWheel(int delta) {
		// println("mousewheel: " + delta);
		cam_eye.z += delta;
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorerprocessing2.TagExplorerProcessing2.class.getName() });
	}
}

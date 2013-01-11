package tagexplorerprocessing2;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

import tagexplorerprocessing2.Connection.ConnectionType;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

public class TagExplorerProcessing2 extends PApplet {

	WatchDir watcher;

	PGraphics mainscreen;
	// PGraphics textu;
	PGraphics pg;

	Timeline timeline;

	// Tag_User user = new Tag_User("users", 0, "noname", "no Password");
	Tag_User user = null;
	Tag_Location location = null;

	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	ControlP5 cp5_Menu;

	ArrayList<Tag> attributes = null;
	ArrayList<Tag> files = null;
	ArrayList<Tag> showFiles = null;
	ArrayList<Tag> availableTags = new ArrayList<Tag>();

	Tag_File oldest_showFile = null;

	// Timestamp oldestTime;

	ArrayList<Filter> filterList = new ArrayList<Filter>();

	// Interaction
	Tag startTag = null;
	boolean mouseActive = true;
	Timestamp lastClick;

	// Camera
	Vec3D cam_eye;
	Vec3D cam_target;
	Vec3D cam_up;

	float xBillboardRotation = 0;
	float yBillboardRotation = 0;
	float zBillboardRotation = 0;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;
	VerletPhysics filePhysics;

	// PShape ball;

	String[] imageExtension = { "jpg", "jpeg", "png", "gif", "psd", "tif", "tiff", "bmp", "tga" };
	String[] vectorExtension = { "ai", "drw", "eps", "ps", "svg" };
	String[] layoutExtension = { "indd", "pdf", "pxd", "pxp" };
	String[] audioExtension = { "aif", "m4a", "mid", "mp3", "mpa", "wav", "wma" };
	String[] videoExtension = { "avi", "flv", "mov", "mp4", "mpg", "swf", "wmv", "vob" };
	String[] textExtension = { "txt", "doc", "docx", "log", "pages", "rtf" };
	String[] webExtension = { "css", "htm", "html", "js", "jsp", "php", "rss", "xhtml" };
	String[] fontExtension = { "fnt", "fon", "otf", "ttf" };
	String[] messageExtension = { "msg", "eml" };

	HoverPlane hoverPlane = null;
	MenuPlane menuPlane;

	PShader transition;
	PShader transition2;
	PShader plifShader;
	PImage env_plif;

	// Images
	PImage closeImg;
	PImage openImg;
	
	
	
	Tag_Comparator_Id comp_id;

	// PImage

	// public void init() {
	// frame.removeNotify();
	// frame.setUndecorated(true);
	// // frame.setAlwaysOnTop(true);
	// frame.addNotify();
	// super.init();
	// }

	public void setup() {
		size(1800, 600, P3D);
		// frame.setLocation(1970, 50);
		smooth(4);

		font = createFont("arial", 40);
		textFont(font, 14);

		loadImages();

		// ControlP5
		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);

		menuPlane = new MenuPlane(this);

		mainscreen = createGraphics(width, height - 40, P3D);
		pg = createGraphics(100, 100, P2D);
		// mainscreen.smooth(4);

		// camera
		cam_eye = new Vec3D(mainscreen.width / 2.0f, mainscreen.height / 2.0f - 250, (mainscreen.height / 2.0f)
				/ tan(PI * 30.0f / 180.0f));
		cam_target = new Vec3D(mainscreen.width / 2.0f, mainscreen.height / 2.0f, 0);
		cam_up = new Vec3D(0, 1, 0);

		mainscreen.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x, cam_target.y, cam_target.z, cam_up.x,
				cam_up.y, cam_up.z);

		transition = loadShader("../shader/transition.glsl");
		transition.set("res", (float) (mainscreen.width), (float) (mainscreen.height));
		transition.set("color", (1.0f), (1.0f), (1.0f));

		transition2 = loadShader("../shader/transition.glsl");
		transition2.set("res", 100.0f, 100.0f);
		transition2.set("color", (1.0f), (0.1f), (0.1f));

		plifShader = loadShader("../shader/plifFrag.glsl", "../shader/plifVert.glsl");
		env_plif = loadImage("../data/env_plif.jpg");

		// ball = createShape(SPHERE, 10);
		// ball.noStroke();
		// ball.fill(155, 100);
		// ball.emissive(150, 0, 0);

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

		// TIMELINE
		timeline = new Timeline(this);
		
		
		// Comparator
		comp_id = new Tag_Comparator_Id();

		// Standartuser: Öffentlich
		// user = (Tag_User) SQL.queryTagList("users").get(0);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// toxi VerletPhysics
		physics = new VerletPhysics();
		// GravityBehavior g = new GravityBehavior(new Vec3D(0, 0, -0.01f));
		// physics.addBehavior(g);
		filePhysics = new VerletPhysics();

		// erst Tags, dann Files!
		attributes = initTagsFromDB();
		initFilesFromDB();
		updateShowFiles();
		updateTags();
		updateSprings();

		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent mwe) {
				mouseWheel(mwe.getWheelRotation());
			}
		});

		lastClick = new Timestamp(System.currentTimeMillis());
	}

	// /////////// draw ////////////////////

	public void draw() {
		// set Mouse active nach jeweils 600 millis;
		if (System.currentTimeMillis() > lastClick.getTime() + 600) {
			mouseActive = true;
		} else {
			mouseActive = false;

		}
		// println("mouseActive: " + mouseActive);

		calcBillboardRotation();
		transition.set("shaderTime", (millis() / 1000.0f));
		transition2.set("shaderTime", (millis() / 1000.0f));

		plifShader.set("shininess", 5.1f);
		// plifShader.set("BaseColor", .4f, .4f, 1.0f);
		plifShader.set("EnvMap", 0);
		plifShader.set("MixRatio", 0.2f);
		plifShader.set("SpecularVal", 0.2f);
		// plifShader.set("Xunitvec", 1, 0, 0);
		// plifShader.set("Yunitvec", 0, 1, 0);

		pg.beginDraw();
		pg.fill(25, 0, 0);
		pg.rect(0, 0, pg.width, pg.height);
		pg.shader(transition2);
		pg.endDraw();

		// control p5
		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		// draw mainscene
		drawMainscreen(mainscreen);

		// MenuPlane
		menuPlane.render();
		menuPlane.update();

		// Hover Plane Dateiinfos
		if (hoverPlane != null) {

			if (hoverPlane.mouseOver()) {
				hoverPlane.render();
				// println("render hoverplane");
			} else {
				// delete Textfield
				try{
				cp5_Menu.get(Textfield.class, hoverPlane.inputFieldName).remove();
				} catch (Exception e){
					e.printStackTrace();
				}
				hoverPlane = null;
			}
		}

		// logging
		fill(0);
		text(frameRate, width - 120, 16);
		// macht hintergrund für 3D Objekte weiß!
		fill(255);

		// Promt Messages
		if (p != null) {
			p.showPromt();
		}

		if (typeChooser != null) {
			typeChooser.render();
		}

		// reset mouseHover & interation status
		interaction = false;
		hoverPoint = null;

		physics.update();
		filePhysics.update();
	}

	private void drawMainscreen(PGraphics renderer) {
		// renderer begin:
		renderer.beginDraw();
		renderer.smooth(4);

		renderer.background(255);

		renderer.hint(DISABLE_DEPTH_MASK);
		renderer.fill(255);
		renderer.shader(transition);

		// renderer.pushMatrix();
		// renderer.translate(0, 0, -2100);
		renderer.rectMode(CENTER);
		renderer.rect(renderer.width / 2, renderer.height / 2, renderer.width * 20, renderer.height * 20);
		// renderer.popMatrix();
		renderer.resetShader();
		renderer.hint(ENABLE_DEPTH_MASK);

		// turn lights on
		renderer.lights();
		// renderer.directionalLight(0, 255, 0, 0, -1, 0);

		// renderer.rotateY(mainScreenYRotation);
		// renderer.translate(-50, 0, 0);

		renderer.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x, cam_target.y, cam_target.z, cam_up.x, cam_up.y,
				cam_up.z);

		renderer.pushMatrix();
		renderer.translate(renderer.width / 2, renderer.height / 2, 0);

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

		PShape s = createShape(GROUP);

		if (filePhysics.particles != null) {
			// shader(plifShader);
			for (int i = 0; i < filePhysics.particles.size(); i++) {
				Tag_File file = (Tag_File) filePhysics.particles.get(i);

				s.addChild(file.shape);
				// renderer.shape(file.shape);

				// balls statt 3D-shapes
				// renderer.pushMatrix();
				// renderer.translate(vp.x, vp.y, vp.z);
				// renderer.shape(ball);
				// renderer.popMatrix();

				if (mouseOver(renderer, file, 30, 30)) {
					if (hoverPlane == null) {
						hoverPlane = new HoverPlane(this, file, (int) mainscreen.screenX(file.x, file.y, file.z),
								(int) mainscreen.screenY(file.x, file.y, file.z));
					}
				}
			}

			renderer.shape(s);
			// resetShader();
		}
	}

	public void drawTags(PGraphics renderer) {
		for (int i = 0; i < physics.particles.size(); i++) {
			Tag tag = (Tag) physics.particles.get(i);
			renderer.strokeWeight(5);

			if (tag.isLocked()) {
				renderer.stroke(255, 0, 0);
			} else {
				renderer.stroke(0, 255, 200);
			}
			renderer.point(tag.x, tag.y, tag.z);

			if (mouseOver(renderer, tag, 30, 30)) {
				if (hoverPlane == null) {
					hoverPlane = new HoverPlane(this, tag, (int) mainscreen.screenX(tag.x, tag.y, tag.z),
							(int) mainscreen.screenY(tag.x, tag.y, tag.z));
					// println("Create hoverPlane");
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

			// shape(s);
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
	public void initFilesFromDB() {
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

		oldest_showFile = ((Tag_File) getOldestTagFile(this.files));
		// return files;
	}

	public ArrayList<Tag> initTagsFromDB() {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		tags = SQL.queryTagList("keywords");
		tags.addAll(SQL.queryTagList("locations"));
		tags.addAll(SQL.queryTagList("projects"));
		tags.addAll(SQL.queryTagList("users"));

		// tags.addAll(SQL.queryTagList("files"));
		// tags nicht überscheiben, sondern nur abgleichen!
		return tags;
	}

	// ///////// update Methods ////////////////////
	public void updateShowFiles() {
		System.out.println("updateShowFiles()");

		// count matches mit FilterList
		updateMatches(files);

		// filter files
		if (filterList.size() > 0) {
			// DB Abfrage
			// showFiles = SQL.queryTagListFiltered("files", filterList);

			// alternativ Java Abfrage:
			showFiles = getFullMatches(files);
		} else {
			// alle files
			showFiles = files;
		}

		oldest_showFile = (Tag_File) getOldestTagFile(showFiles);

		// set Timeline Wertebereich
		if (oldest_showFile != null) {
			timeline.setWertebereich(oldest_showFile.creation_time);
		} else {
			timeline.oldest = null;
		}

		setParticlesPosition(filePhysics, showFiles);
	}

	private ArrayList<Tag> getFullMatches(ArrayList<Tag> files) {
		ArrayList<Tag> fullMatches = new ArrayList<Tag>();
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			if (file.matches == filterList.size()) {
				fullMatches.add(file);
			}
		}
		return fullMatches;
	}

	// zählt matches mit Filer ++ oder bei inOut false --
	public void updateMatches(ArrayList<Tag> files) {
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			// set matches = 0
			file.matches = 0;

			// count matches mit filterList
			for (Filter f : filterList) {
				if (f.inOut) {
					if (file.attributeBindings.contains(f.tag)) {
						file.matches++;
					}
				} else {
					if (file.attributeBindings.contains(f.tag)) {
						file.matches--;
					}
				}
			}
		}
	}

	private ArrayList<Tag> getTagcountAndTags(ArrayList<Tag> showFiles) {
		ArrayList<Tag> aktuelleTags = new ArrayList<Tag>();

		for (Tag tag : attributes) {
			if (tag.bindCount > 0) {
				aktuelleTags.add(tag);
			}
		}
		return aktuelleTags;
	}

	// setzt bindCount & available Tags & setzt Patikelpositionen neu
	public void updateTags() {
		// ArrayList<Tag> tags = new ArrayList<Tag>();
		//
		// tags = SQL.queryTagList("keywords");
		// tags.addAll(SQL.queryTagList("locations"));
		// tags.addAll(SQL.queryTagList("projects"));
		// tags.addAll(SQL.queryTagList("users"));
		//
		// // tags.addAll(SQL.queryTagList("files"));
		// // tags nicht überscheiben, sondern nur abgleichen!
		// this.attributes = tags;

		attributes = initTagsFromDB();

		// reset bindCount
		for (Tag tag : attributes) {
			tag.bindCount = 0;
		}

		// aktuelle FileBindings
		if (showFiles != null) {
			for (Tag f : showFiles) {
				Tag_File file = (Tag_File) f;
				for (Tag tag : file.attributeBindings) {
					tag.bindCount++;
				}
			}
		}

		// get mit showFiles verknüpfte Tags & Häufigkeit
		this.availableTags = getTagcountAndTags(showFiles);

		for (Tag tag : availableTags) {
			System.out.println(tag.name + " " + tag.bindCount);
		}

		physics.particles.clear();

		int count = attributes.size();
		float dist;
		if (count > 1) {
			dist = ((float) height - 40) / (count - 1);
		} else {
			dist = 0;
		}

		for (int i = 0; i < attributes.size(); i++) {
			// dropParticles(physics, i * dist - (dist * (count - 1) / 2), 0,
			// 50, tags.get(i));

			dropParticles(physics, i * dist - (dist * (count - 1) / 2), -500, -250, attributes.get(i));

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

				// System.out.println("dropParticles: file: " +
				// files.get(i).name);

				// dropParticles(physics, parent.x, parent.y, 0, files.get(i));
				dropParticles(physics, parent.x, parent.y - 40, 0, files.get(i));

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

		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			file.setShape(generateShape(file));
		}

		// set z position creation time
		setZNewestTime();
	}

	public void dropParticles(VerletPhysics physics, float x, float y, float z, Tag t) {
		t.x = x;
		t.y = y;
		t.z = z;
		t.lock();

		// System.out.println(t.name + " " + x + " " + y + " " + z + " " + t.x +
		// " " + t.y + " " + t.z);
		physics.addParticle(t);
	}

	float LEN = 400; // 10
	float STR = 0.01f; // 0.01f

	public void dropSpring(VerletPhysics physics, VerletParticle fileParticle, VerletParticle attributeParticle,
			Connection.ConnectionType type) {
		Connection sp = new Connection(fileParticle, attributeParticle, LEN, STR, type);
		physics.addSpring(sp);
	}

	void setZNewestTime() {
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;

			// println("aktuelle anzeige file:" + file.name);
			file.z = -timeline.mapExp(getNewestDate(file));
		}
	}

	public Tag getTagByID(String tableName, int id) {
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
	
	public Tag getTagByName(String name){
		Tag tag = null;
		
		String tagName = name.trim().toLowerCase();
		// no files!

		// attributes
		if (attributes != null) {
			for (Tag _tag : attributes) {
				if (_tag.name.toLowerCase().equals(tagName)) {
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
			// println("getOldestTagFile: file.name: " + file.name);
			if (((Tag_File) file).creation_time.before(comp)) {
				comp = ((Tag_File) file).creation_time;
				oldest = file;
			}
		}
		return oldest;
	}

	
	// noch nicht fertig!
	private ArrayList<Tag> getNewestTagFile(ArrayList<Tag> files) {

		ArrayList<Tag> newestVersions = (ArrayList<Tag>) files.clone();
		
		Collections.sort(newestVersions, comp_id);
		
		
		

		// Tag oldest = null;
		// Timestamp comp = new Timestamp(System.currentTimeMillis());
		// for (Tag file : files) {
		// // println("getOldestTagFile: file.name: " + file.name);
		// if (((Tag_File) file).creation_time.before(comp)) {
		// comp = ((Tag_File) file).creation_time;
		// oldest = file;
		// }
		// }
		// return oldest;

		return newestVersions;
	}

	Timestamp getNewestDate(Tag_File file) {
		Timestamp newest = file.creation_time;

		// deleteTime ist neueste
		if (file.delete_time != null && file.delete_time.after(newest)) {
			newest = file.delete_time;
			return newest;
		}

		// accesses:
		for (Access c : file.getAccesses()) {
			if (c.date.after(newest)) {
				newest = c.date;
			}
		}

		// System.out.println("getNewestDate: " + newest.toGMTString());
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

	public PShape generateShape(Tag_File file) {
		PShape s = createShape(GROUP);
		// file.creation_time;
		PShape start = makeRect(file, file.creation_time);
		s.addChild(start);

		PShape next = null;
		for (Access c : file.getAccesses()) {
			next = makeRect(file, c.date);
			s.addChild(next);
			s.addChild(generateConnection(start, next));
			start = next;
		}

		if (file.delete_time != null) {
			next = makeRect(file, file.delete_time);
			s.addChild(next);
			println("generateShape(): deleted file: " + file.name);

			// PShape[] shapes = s.getChildren();
			s.addChild(generateConnection(start, next));

		}
		return s;
	}

	public PShape makeRect(Tag file, Timestamp ts) {
		float z = -timeline.mapExp(System.currentTimeMillis() - ts.getTime());

		// image(pg, 0, 500, 100, 100);

		PShape s = createShape();
		s.fill(0, 0, 255);
		s.noStroke();
		s.texture(pg);
		// s.texture(env_plif);
		s.vertex(file.x + 10, file.y + 10, z, 0, 0);
		s.vertex(file.x + 10, file.y - 10, z, 0, pg.height);
		s.vertex(file.x - 10, file.y - 10, z, pg.width, pg.height);
		s.vertex(file.x - 10, file.y + 10, z, pg.width, 0);
		s.end(CLOSE);

		return s;
	}

	// public PShape generateConnection(PShape s1, PShape s2) {
	//
	// int count = s1.getVertexCount();
	//
	// PShape s = createShape(TRIANGLE_STRIP);
	//
	// s.fill(0, 255, 255);
	// s.noStroke();
	// for (int i = 0; i < count; i++) {
	// println(s1.getVertexX(i) + " ," + s1.getVertexY(i) + " ," +
	// s1.getVertexZ(i));
	// s.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i));
	// println(s2.getVertexX(i) + " ," + s2.getVertexY(i) + " ," +
	// s2.getVertexZ(i));
	// s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i));
	// }
	// s.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0));
	// s.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0));
	// s.end();
	//
	// return s;
	// }
	public PShape generateConnection(PShape s1, PShape s2) {

		int count = s1.getVertexCount();

		PShape s = createShape(GROUP);

		s.fill(0, 255, 255);
		s.noStroke();

		PShape r = createShape();
		r.fill(0, 0, 255);
		r.noStroke();
		r.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0));
		r.vertex(s1.getVertexX(0 + 1), s1.getVertexY(0 + 1), s1.getVertexZ(0 + 1));
		r.vertex(s2.getVertexX(0 + 1), s2.getVertexY(0 + 1), s2.getVertexZ(0 + 1));
		r.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0));
		r.end(CLOSE);
		s.addChild(r);

		r = createShape();
		r.fill(0, 0, 255);
		r.noStroke();
		r.vertex(s1.getVertexX(1), s1.getVertexY(1), s1.getVertexZ(1));
		r.vertex(s1.getVertexX(1 + 1), s1.getVertexY(1 + 1), s1.getVertexZ(1 + 1));
		r.vertex(s2.getVertexX(1 + 1), s2.getVertexY(1 + 1), s2.getVertexZ(1 + 1));
		r.vertex(s2.getVertexX(1), s2.getVertexY(1), s2.getVertexZ(1));
		r.end(CLOSE);
		s.addChild(r);

		r = createShape();
		r.fill(0, 0, 255);
		r.noStroke();
		r.vertex(s1.getVertexX(2), s1.getVertexY(2), s1.getVertexZ(2));
		r.vertex(s1.getVertexX(2 + 1), s1.getVertexY(2 + 1), s1.getVertexZ(2 + 1));
		r.vertex(s2.getVertexX(2 + 1), s2.getVertexY(2 + 1), s2.getVertexZ(2 + 1));
		r.vertex(s2.getVertexX(2), s2.getVertexY(2), s2.getVertexZ(2));
		r.end(CLOSE);
		s.addChild(r);

		r = createShape();
		r.fill(0, 0, 255);
		r.noStroke();
		r.vertex(s1.getVertexX(3), s1.getVertexY(3), s1.getVertexZ(3));
		r.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0));
		r.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0));
		r.vertex(s2.getVertexX(3), s2.getVertexY(3), s2.getVertexZ(3));
		r.end(CLOSE);

		s.addChild(r);

		return s;
	}

	// verwende timeline.mapExpe(long time);
	// public float mapExp(long time) {
	//
	// float val = map(sqrt(System.currentTimeMillis() - time), 0,
	// sqrt(System.currentTimeMillis() - timeline.oldest.getTime()), 0, 2000);
	// // 150
	// // hand
	// // gesetzt!
	//
	// return val;
	// }

	// ///////// INPUT ///////////////////
	public void mousePressed() {

		// lastClick = new Timestamp(System.currentTimeMillis());

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

		// if (b != null) {
		// println("dispose");
		// // PromtNewFile pro = promts.get(0);
		// b.dispose();
		// b = null;
		//
		// // pro = null;
		// // pro.markForDisposal();
		// // promts.remove(p);
		// }
	}

	// public void handleTextEvents(GEditableTextControl textcontrol, GEvent
	// event) {
	// println("textEvent");
	// }

	// public void mouseDragged() {
	// if (mouseOver(50, 50, 100, 100)) {
	// mainScreenYRotation += 0.01;
	// System.out.println("mainScreenYRotation: " + mainScreenYRotation);
	// }
	// }

	public void mouseReleased() {
		lastClick = new Timestamp(System.currentTimeMillis());

		if (startTag != null) {
			if (startTag instanceof Tag_File) {
				// File -> Attribute
				for (Tag t : attributes) {
					if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
						Tag_File file = (Tag_File) startTag;
						file.attributeBindings.add(startTag);
						SQL.bindTag(file, t);
						// updateFileTagBinding(file);
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
		case 'U':
			// Set User
			user = (Tag_User) SQL.queryTagList("users").get(1);
			filterList.clear();
			filterList.add(new Filter(user, true));
			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'I':
			// Set User
			user = (Tag_User) SQL.queryTagList("users").get(2);
			filterList.clear();
			filterList.add(new Filter(user, true));
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

			// beispiel tag löschen!
			Tag tag = new Tag_Location("locations", 5, "Ort", "coordinaten");
			SQL.bindTag(file, tag);
			updateTags();
			updateSprings();
			break;
		case 'F':
			filterList.clear();
			System.out.println(attributes.get(0).name + " " + attributes.get(0).type);
			System.out.println(attributes.get(1).name + " " + attributes.get(1).type);
			filterList.add(new Filter(attributes.get(0), true));
			filterList.add(new Filter(attributes.get(1), false));

			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'C':
			// b = new GTextField(this, 0, 0, 100, 100);
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
		case 'a':
			cam_eye.x -= 20;
			// println("cam_eye.x = " + cam_eye.x);
			break;
		case 'd':
			cam_eye.x += 20;
			// println("cam_eye.x = " + cam_eye.x);
			break;
		case 'w':
			cam_eye.y -= 20;
			// println("cam_eye.y = " + cam_eye.y);
			break;
		case 's':
			cam_eye.y += 20;
			// println("cam_eye.y = " + cam_eye.y);
			break;
		}
	}

	// GTextField b = null;
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
			System.out.println("createNewFile(): Tag " + s + " is already imported in " + tableName);
		} else {
			file = (Tag_File) SQL.createDbTag(tableName, s);
		}
		return file;
	}

	// public void fileSelected(File selection) {
	//
	// if (selection == null) {
	// println("Window was closed or the user hit cancel.");
	// } else {
	// // println("Create new File: url: " + url);
	//
	// Tag_File file = createNewFile("files", selection);
	//
	// if (file != null) {
	// if (user != null) {
	// // System.out.println(file.toString());
	// SQL.bindTag(file, user);
	// }
	//
	// // update File
	// updateFileTagBinding(file);
	// files.add(file);
	// updateShowFiles();
	// updateSprings();
	// }
	// }
	//
	// }

	// nicht mehr gebraucht alte Promts
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
//			updateTags();
//			updateSprings();
		}
	}

	// nicht mehr gebraucht alte Promts
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

	// nicht mehr gebraucht alte Promts
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
	
	
	public Tag createNewTag(String theText, String type) {
		// System.out.println("function locationInput");
		Tag tag = null;
		theText = theText.trim();
		
		if (SQL.inDataBase(type, theText)) {
			System.out.println(type + ": " + theText + " already exists");
		} else {
			tag = SQL.createDbTag(type, theText);

			// update
			updateTags();
			updateSprings();

			removeController();
		}
		return tag;
	}
	
	

	// Menuplane Textinput Feld nicht mehr gebraucht
	public void tagInput(String tagName) {
		println("Textfield tagInput content: " + tagName);
//		if (!tagName.trim().equals("")) {
//			createChooserButtons("tagInput", tagName);
//		}
	}

	// Hoverplane Textinput Feld nicht mehr gebraucht
	public void tagInputHoverPlane(String tagName) {
		println("Textfield tagInputHoverPlane content: " + tagName);
//		if (!tagName.trim().equals("")) {
//			createChooserButtons("tagInputHoverPlane", tagName);
//		}
	}

	// public void handleButtonEvents(GButton button, GEvent event) {
	// println("buttonEvent: " + event.toString());
	//
	// for (PromtNewFile p : promts) {
	//
	// if (button == p.btnCancel) {
	//
	// return;
	// }
	// }
	// }
	//
	// public void handlePanelEvents(GPanel panel, GEvent event) {
	// println("panelEvent: " + event.toString());
	// for (PromtNewFile p : promts) {
	//
	// if (panel == p) {
	// println("hello p");
	// }
	// }
	// }

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

	Promt_TypeChooser typeChooser = null;

	public void createChooserButtons(String inputFieldName, String value) {
		// removeController();
		typeChooser = new Promt_TypeChooser(this, cp5_Menu, inputFieldName, value);
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
		Vec3D cam_xy = new Vec3D(cam.x, cam.y, 0).normalize();

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

		// zBillboardRotation = cam_xy.angleBetween(new Vec3D(0, -1, 0));
		// println(zBillboardRotation);

		// zBillboardRotation = 0;

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

	void loadImages() {
		closeImg = loadImage("../data/close.png");
		openImg = loadImage("../data/open.png");
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorerprocessing2.TagExplorerProcessing2.class.getName() });
	}
}

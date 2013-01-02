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
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

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
	Tag_File oldest_File = null;

	ArrayList<Filter> filters = new ArrayList<Filter>();

	// Interaction
	Tag startTag = null;

	// Camera
	Vec3D cam_eye;
	Vec3D cam_target;
	Vec3D cam_up;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;
	VerletPhysics filePhysics;

	// PeasyCam cam;
	boolean _3d = true;

	public void setup() {
		size(800, 400, P3D);
		
		font = createFont("arial", 20);

		Path p = FileSystems.getDefault().getPath(
				"/Users/manuel/Documents/Testumgebung/Test");
		
		// FileSystem Watcher
		try {
			watcher = new WatchDir(this, p, true);
			watcher.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// SQL HELPER
		SQL = new SQLhelper(this);

		mainscreen = createGraphics(width - 200, height, P3D);
		// mainscreen.smooth(4);
		
		// camera
		cam_eye = new Vec3D(mainscreen.width / 2.0f, mainscreen.height / 2.0f,
				(mainscreen.height / 2.0f) / tan(PI * 30.0f / 180.0f));
		cam_target = new Vec3D(mainscreen.width / 2.0f,
				mainscreen.height / 2.0f, 0);
		cam_up = new Vec3D(0, 1, 0);
		mainscreen.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x,
				cam_target.y, cam_target.z, cam_up.x, cam_up.y, cam_up.z);
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

		// test SQL Join
		// SQL.msql.query("SELECT files.* FROM files INNER JOIN tag_binding ON (files.ID = tag_binding.file_ID) WHERE tag_binding.type = 'users' AND tag_binding.tag_ID = '1' ");
		// while(SQL.msql.next()){
		// System.out.println(SQL.msql.getString("name"));
		// }

		// UNION!
		// SQL.msql.query("SELECT files.* FROM files INNER JOIN tag_binding ON (files.ID = tag_binding.file_ID) WHERE tag_binding.type = 'users' AND tag_binding.tag_ID = '1' ");
		// while(SQL.msql.next()){
		// //System.out.println(SQL.msql.getString("name"));
		// }

		// Writing to the depth buffer is disabled to avoid rendering
		// artifacts due to the fact that the particles are semi-transparent
		// but not z-sorted.
		// hint(DISABLE_DEPTH_MASK);
		// hint(mainscreen.ENABLE_ACCURATE_2D);
		// hint(mainscreen.DISABLE_DEPTH_TEST);
	}

	// /////////// draw ////////////////////

	public void draw() {
		
		// control p5
		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		drawMainscreen(mainscreen);

		fill(150);
		if (user != null) {
			text("User: " + user.name, 5, 16);
		}

		if (location != null) {
			text("Location: " + location.name, 150, 16);
		}

		timeline.draw();
		image(timeline.pg, width - 200, 0);

		// Promt Messages
		if (p != null) {
			p.showPromt();
		}

		// if(interaction || startTag != null){
		// cam.setActive(false);
		// } else{
		// cam.setActive(true);
		// }

		// reset mouseHover & interation status
		interaction = false;
		hoverPoint = null;

		physics.update();
		filePhysics.update();

	}

	// mouse to 3d
	// originally created by david huebner (aka myT) (2005|05|01)
	// edited by markavian (2005|06|07) - Two new method variables offsetX and
	// offsetY added (would normally be mouseX and mouseY)

	// Vec3D getScreenGroundPlaneIntersection(float eyeX, float eyeY, float
	// eyeZ,
	// float centerX, float centerY, float centerZ,
	// float upX, float upY, float upZ,
	// float offsetX, float offsetY) {
	// // generate the required vectors
	// Vec3D eye = new Vec3D(eyeX, eyeY, eyeZ);
	// Vec3D center = new Vec3D(centerX, centerY, centerZ);
	// Vec3D look = (center.subSelf(eye)).normalize();
	// Vec3D up = new Vec3D(upX, upY, upZ).normalize();
	// Vec3D left = up.crossSelf(look.normalize());
	//
	// // calculate the distance between the mouseplane and the eye
	// float distanceEyeMousePlane = (height / 2) / tan(PI / 6);
	//
	// // calculate the vector, that points from the eye
	// // to the clicked point, the mouse is on
	// Vec3D mousePoint = look.scaleSelf(distanceEyeMousePlane);
	// mousePoint = mousePoint.add(left.scaleSelf((float) ((offsetX) * -1)));
	// mousePoint = mousePoint.add(up.scaleSelf((float) (offsetY)));
	//
	// Vec3D intersection = new Vec3D();
	// if (mousePoint.z != 0) { // avoid zero division
	// // calculate the value, the vector that points to the mouse
	// // must be multiplied with to reach the XY-plane
	// float multiplier = -eye.z / mousePoint.z;
	// // do not calculate intersections behind the camera
	// if (multiplier > 0) {
	// // add the multiplied mouse point vector
	// intersection = eye.add(mousePoint.scaleSelf(multiplier));
	// }
	// }
	// return intersection;
	// }
	//
	// void updateUp() {
	// if(look != null){
	// Vec3D helper = new Vec3D();
	// //if z > length of xy
	// if ( look.x[2] > (sqrt ( camLook.x[0] * camLook.x[0] + camLook.x[1] *
	// camLook.x[1] ) ) ) {
	// camUp.x[0] = 0;
	// camUp.x[1] = 1;
	// camUp.x[2] = 0;
	// helper = new Vec3D(camLook);
	// helper.x[1] = 0;
	// }
	// else {
	// camUp.x[0] = 0;
	// camUp.x[1] = 0;
	// camUp.x[2] = 1;
	// helper = new Vec3D(camLook);
	// helper.x[2] = 0;
	// }
	// helper.normalize()();
	// helper = (helper.crossProduct(camUp)).normalize();
	// camUp = (camLook.crossProduct(helper)).normalize();
	//
	// // Calculate the roll if there is one
	// //if (roll != 0.0) {
	// //camUp = camUp.multiply(cos(roll));
	// // camUp = camUp.add(helper.multiply(sin(roll)));
	// //}
	// }
	// }

	float mainScreenYRotation = 0;

	private void drawMainscreen(PGraphics renderer) {
		renderer.beginDraw();
		renderer.pushMatrix();

		renderer.translate(renderer.width / 2, renderer.height / 2);

		// renderer.rotateY(mainScreenYRotation);
		// renderer.translate(-50, 0, 0);

		renderer.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x,
				cam_target.y, cam_target.z, cam_up.x, cam_up.y, cam_up.z);

		// float dist = cam_eye.z;
		// renderer.camera(dist * sin(mainScreenYRotation), dist *
		// cos(mainScreenYRotation), cam_target.x, 0, cam_target.y,
		// cam_target.z, cam_up.x, cam_up.y, cam_up.z);

		renderer.background(0);

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
				renderer.line(startTag.x, startTag.y, startTag.z, hoverPoint.x,
						hoverPoint.y, hoverPoint.z);
			}
		}
		// renderer.fill(100);
		// renderer.noStroke();
		// renderer.alpha(10);
		// // renderer.rect(0, 0, renderer.width, renderer.height);
		// renderer.pushMatrix();
		// // renderer.rect(0, 0, renderer.width, renderer.height);
		// renderer.translate(0, 0, -80);
		// renderer.rect(0, 0, renderer.width, renderer.height);
		// renderer.popMatrix();

		renderer.popMatrix();
		renderer.endDraw();
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

				renderer.text(vp.name, 10, 0);
				if (_3d) {
					renderer.popMatrix();
				}
			}
		}
	}

	public void drawSprings(PGraphics renderer) {
		for (int i = 0; i < physics.springs.size(); i++) {
			VerletSpring sp = physics.springs.get(i);
			renderer.stroke(255);
			renderer.strokeWeight(1);
			renderer.line(sp.a.x, sp.a.y, sp.a.z, sp.b.x, sp.b.y, sp.b.z);
		}

		for (int i = 0; i < filePhysics.springs.size(); i++) {
			VerletSpring sp = filePhysics.springs.get(i);
			renderer.stroke(0, 255, 0);
			renderer.strokeWeight(1);
			renderer.line(sp.a.x, sp.a.y, sp.a.z, sp.b.x, sp.b.y, sp.b.z);
		}
	}

	// ///////// init Methods //////////////////////
	public void initFiles() {
		ArrayList<Tag> files = new ArrayList<Tag>();
		files = SQL.queryTagList("files");
		this.files = files;

		// set Attributes
		for (Tag t : this.files) {
			updateFileTags((Tag_File) t);
		}
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

		oldest_File = (Tag_File) getOldestTagFile(showFiles);

		if (oldest_File != null) {
			timeline.setWertebereich(oldest_File.creation_time);
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
			dropParticles(physics, width - 150, i * dist + 20, 0, tags.get(i));
		}
	}

	public void updateSprings() {

		physics.springs.clear();

		for (Tag tf : showFiles) {
			Tag_File file = (Tag_File) tf;
			if (file.attributes.size() > 0) {
				for (Tag t : file.attributes) {
					dropSpring(physics, file, t);
				}
			}
		}
	}

	private void setParticlesPosition(VerletPhysics physics,
			ArrayList<Tag> files) {
		// drop Particles
		// set Position
		physics.particles.clear();

		int count = getSizeWithoutVersion(files);
		// int count = showFiles.size();

		float dist;
		if (count > 1) {
			dist = ((float) height - 40) / (count - 1);
		} else {
			dist = 0;
		}

		for (int i = 0; i < files.size(); i++) {

			// set z wert nach versionsnummer.
			if (((Tag_File) files.get(i)).parent_ID != 0) {
				// get parent
				Tag_File parent = (Tag_File) getTagByID(files.get(i).type,
						((Tag_File) files.get(i)).parent_ID);

				dropParticles(physics, parent.x, parent.y, 0, files.get(i));

				dropSpring(physics, (Tag_File) files.get(i), parent);
			}

			// ist origin File Version
			else {
				dropParticles(physics, 250, i * dist, 0, files.get(i));
			}
		}

		// set z position creation time
		setZAccessTime();
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
			if (((Tag_File) file).creation_time.before(comp)) {
				comp = ((Tag_File) file).creation_time;
				oldest = file;
			}
		}
		return oldest;
	}

	void updateFileTags(Tag_File fileTag) {
		fileTag.setAttributes(SQL.getBindedTagList(fileTag));
		fileTag.updateViewName();
	}

	void setZAccessTime() {
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;
			file.z = -timeline.mapExp(file.creation_time, 150);
		}
	}

	public void dropParticles(VerletPhysics physics, float x, float y, float z,
			Tag t) {
		t.x = x;
		t.y = y;
		t.z = z;
		// if (t.z == 0) {
		t.lock();
		// }
		physics.addParticle(t);
		// println(t.x + " " + t.y + " " + t.z);
	}

	float LEN = 400; // 10
	float STR = 0.01f; // 0.01f

	public void dropSpring(VerletPhysics physics, VerletParticle fileParticle,
			VerletParticle attributeParticle) {
		VerletSpring sp = new VerletSpring(fileParticle, attributeParticle,
				LEN, STR);
		physics.addSpring(sp);
	}

	// ///////// INPUT ///////////////////
	public void mousePressed() {
		for (Tag t : showFiles) {
			if (mouseOver(mainscreen, t, 10, 10)) {
				startTag = t;
			}
		}
		for (Tag t : attributes) {
			if (mouseOver(mainscreen, t, 10, 10)) {
				startTag = t;
			}
		}
		
		if (b != null) {
			println("dispose");
			//PromtNewFile pro = promts.get(0);
			b.dispose();
			b = null;
			

		//	pro = null;
//			pro.markForDisposal();
			// promts.remove(p);
		}
	}
	
	public void handleTextEvents(GEditableTextControl textcontrol, GEvent event) { 
		println("textEvent");
	}


	public void mouseDragged() {
		if (mouseOver(50, 50, 100, 100)) {
			mainScreenYRotation += 0.01;
			System.out.println("mainScreenYRotation: " + mainScreenYRotation);
		}
	}

	public void mouseReleased() {
		if (startTag != null) {
			if (startTag instanceof Tag_File) {
				for (Tag t : attributes) {
					if (mouseOver(mainscreen, t, 10, 10)) {
						Tag_File file = (Tag_File) startTag;
						SQL.bindTag(file, t);
						updateFileTags(file);
						updateTags();
						updateSprings();
					}
				}
			} else {
				for (Tag t : files) {
					if (mouseOver(mainscreen, t, 10, 10)) {
						Tag_File file = (Tag_File) t;
						SQL.bindTag(file, startTag);
						updateFileTags(file);
						updateTags();
						updateSprings();
					}
				}
			}
		}
		startTag = null;
		
//		if(b != null){
//			b = null;
//			b.markForDisposal();
//			
////			b.dispose();
//		}
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
		if (mouseX > x - w / 2.0f && mouseX < x + w / 2.0f
				&& mouseY > y - h / 2.0f && mouseY < y + h / 2.0f) {
			over = true;
		}
		return over;
	}

	boolean mouseOver(float x, float y, float z, int w, int h) {
		boolean over = false;

		float screenX = screenX(x, y, z);
		float screenY = screenY(x, y, z);

		if (mouseX > screenX - w / 2.0f && mouseX < screenX + w / 2.0f
				&& mouseY > screenY - h / 2.0f && mouseY < screenY + h / 2.0f) {
			over = true;
			interaction = true;
			hoverPoint = new Vec3D(x, y, z);
		}
		return over;
	}

	boolean mouseOver(Tag t, int w, int h) {
		return mouseOver(t.x, t.y, t.z, w, h);
	}

	boolean mouseOver(PGraphics renderer, float x, float y, float z, int w,
			int h) {
		boolean over = false;

		float screenX = renderer.screenX(x, y, z);
		float screenY = renderer.screenY(x, y, z);

		if (mouseX > screenX - w / 2.0f && mouseX < screenX + w / 2.0f
				&& mouseY > screenY - h / 2.0f && mouseY < screenY + h / 2.0f) {
			over = true;
			interaction = true;
			hoverPoint = new Vec3D(x, y, z);
		}
		return over;
	}

	boolean mouseOver(PGraphics renderer, Tag t, int w, int h) {
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
			System.out.println(attributes.get(0).name + " "
					+ attributes.get(0).type);
			System.out.println(attributes.get(1).name + " "
					+ attributes.get(1).type);
			filters.add(new Filter(attributes.get(0), true));
			filters.add(new Filter(attributes.get(1), false));

			updateShowFiles();
			updateTags();
			updateSprings();
			break;
		case 'C':
			b = new GTextField(this, 0, 0, 100, 100);
//			PromtNewFile p = new PromtNewFile(this);
//			promts.add(p);
			break;
		case 'D':
			
			
//			if (promts.size() > 0) {
//				println("dispose");
//				PromtNewFile pro = promts.get(0);
//				pro.dispose();
//				pro = null;
//				
//
//			//	pro = null;
////				pro.markForDisposal();
//				// promts.remove(p);
//			}
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
			System.out.println("Tag " + s + " is already imported in "
					+ tableName);
		} else {
			file = (Tag_File) SQL.createDbTag(tableName, s);
		}
		return file;
	}

	public Tag_File createNewFile(String tableName, Path path) {
		Tag_File file = null;
		String s = path.toString().trim();
		if (SQL.inDataBase(tableName, s)) {
			System.out.println("Tag " + s + " is already imported in "
					+ tableName);
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
				updateFileTags(file);
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

	void mouseWheel(int delta) {
		// println("mousewheel: " + delta);
		cam_eye.z += delta;
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorerprocessing2.TagExplorerProcessing2.class
				.getName() });
	}
}

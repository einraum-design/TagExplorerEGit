package tagexplorerprocessing2;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;

import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

public class TagExplorerProcessing2 extends PApplet {
	
	WatchDir watcher;

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

	ArrayList<Filter> filters = new ArrayList<Filter>();

	// Interaction
	Tag startTag = null;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;
	VerletPhysics filePhysics;

	public void setup() {
		size(800, 400, P3D);
		smooth(4);
		
		Path p = FileSystems.getDefault().getPath("/Users/manuel/Documents/Testumgebung/Test");
        try {
			watcher = new WatchDir(this, p, true);
			watcher.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		font = createFont("arial", 20);

		SQL = new SQLhelper(this);

		// Standartuser: …ffentlich
		// user = (Tag_User) SQL.queryTagList("users").get(0);

		// ControlP5
		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// User registration
		// user = (Tag_User) SQL.queryTagList("users").get(0);

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

	}

	// /////////// draw ////////////////////

	public void draw() {
		background(0);

		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		fill(150);
		if (user != null) {
			text("User: " + user.name, 5, 16);
		}

		if (location != null) {
			text("Location: " + location.name, 150, 16);
		}

		drawFiles();
		drawTags();
		drawSprings();

		// draw interaction
		stroke(255, 0, 0);
		if (startTag != null) {
			line(startTag.x, startTag.y, mouseX, mouseY);
		}

		// Promt Messages
		if (p != null) {
			p.showMessages();
		}

		physics.update();
		filePhysics.update();
	}

	// ///////// display Methods ////////////////////
	public void drawFiles() {
		if (filePhysics.particles != null) {
			for (int i = 0; i < filePhysics.particles.size(); i++) {
				Tag_File vp = (Tag_File) filePhysics.particles.get(i);
				
				strokeWeight(5);

				if (vp.isLocked()) {
					stroke(255, 0, 0);
				} else {
					stroke(0, 255, 200);
				}
				point(vp.x, vp.y);
				if (mouseOver(vp, 30, 30)) {
					textAlign(LEFT);
					text(vp.viewName, vp.x + 10, vp.y);
				}

			}
			// for (int i = 0; i < showFiles.size(); i++) {
			// text(((Tag_File) showFiles.get(i)).viewName, 10, 40 + i * 16);
			// }
		}
	}

	public void drawTags() {
		for (int i = 0; i < physics.particles.size(); i++) {
			Tag vp = (Tag) physics.particles.get(i);
			strokeWeight(5);

			if (vp.isLocked()) {
				stroke(255, 0, 0);
			} else {
				stroke(0, 255, 200);
			}
			point(vp.x, vp.y);
			if (mouseOver(vp, 30, 30)) {
				textAlign(LEFT);
				text(vp.name, vp.x + 10, vp.y);
			}
		}
	}

	public void drawSprings() {
		for (int i = 0; i < physics.springs.size(); i++) {
			VerletSpring sp = physics.springs.get(i);

			stroke(255);
			strokeWeight(1);
			line(sp.a.x, sp.a.y, sp.a.z, sp.b.x, sp.b.y, sp.b.z);
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
		if (filters.size() > 0) {
			ArrayList<Tag> files = SQL.queryTagListFiltered("files",
					filters.get(0));
			showFiles = files;
			// showFiles = SQL.queryTagListFiltered("files", filters);
		} else {
			// alle files
			showFiles = files;
		}

		// drop Particles
		filePhysics.particles.clear();
		int count = showFiles.size();

		float dist;
		if (count > 1) {
			dist = ((float) height - 40) / (count - 1);
		} else {
			dist = 0;
		}

		for (int i = 0; i < showFiles.size(); i++) {
			dropParticles(filePhysics, 250, i * dist + 20, 0, showFiles.get(i));
		}
	}
	
	
	void updateFileTags(Tag_File fileTag){
		fileTag.setAttributes(SQL.getBindedTagList(fileTag));
		fileTag.updateViewName();
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
					dropSpring(file, t);
				}
			}
		}
	}

	public void dropParticles(VerletPhysics physics, float x, float y, float z,
			Tag t) {
		t.x = x;
		t.y = y;
		t.z = z;
		if (t.z == 0) {
			t.lock();
		}
		physics.addParticle(t);
		// println(t.x + " " + t.y + " " + t.z);
	}

	float LEN = 400; // 10
	float STR = 0.01f; // 0.01f

	public void dropSpring(VerletParticle fileParticle,
			VerletParticle attributeParticle) {
		VerletSpring sp = new VerletSpring(fileParticle, attributeParticle,
				LEN, STR);
		physics.addSpring(sp);
	}

	// ///////// INPUT ///////////////////
	public void mousePressed() {
		for (Tag t : showFiles) {
			if (mouseOver(t, 10, 10)) {
				startTag = t;
			}
		}
		for (Tag t : attributes) {
			if (mouseOver(t, 10, 10)) {
				startTag = t;
			}
		}
	}

	public void mouseReleased() {
		if (startTag != null) {
			if (startTag instanceof Tag_File) {
				for (Tag t : attributes) {
					if (mouseOver(t, 10, 10)) {
						Tag_File file = (Tag_File) startTag;
						SQL.bindTag(file, t);
						updateFileTags(file);
						updateTags();
						updateSprings();
					}
				}
			} else {
				for (Tag t : files) {
					if (mouseOver(t, 10, 10)) {
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
	}

	boolean mouseOver(float x, float y, int w, int h) {
		boolean over = false;
		if (mouseX > x - w / 2.0f && mouseX < x + w / 2.0f
				&& mouseY > y - h / 2.0f && mouseY < y + h / 2.0f) {
			over = true;
		}
		return over;
	}

	boolean mouseOver(Tag t, int w, int h) {
		boolean over = false;
		if (mouseX > t.x - w / 2.0f && mouseX < t.x + w / 2.0f
				&& mouseY > t.y - h / 2.0f && mouseY < t.y + h / 2.0f) {
			over = true;
		}
		return over;
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
		}
	}

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

			if (file != null){
				if(user != null) {
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
		// System.out.println("trigger save!");
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
		// System.out.println("trigger cancel!");
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

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorerprocessing2.TagExplorerProcessing2.class
				.getName() });
	}
}

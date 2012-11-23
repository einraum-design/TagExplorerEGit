package tagexplorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;

public class TagExplorer extends PApplet {

	//Tag_User user = new Tag_User("users", 0, "noname", "no Password");
	Tag_User user = null;
	Tag_Location location = null;
	
	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	ControlP5 cp5_Menu;

	ArrayList<Tag> tags = new ArrayList<Tag>();
	ArrayList<Tag> showFiles = null;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;

	public void setup() {
		size(800, 400);
		font = createFont("arial", 20);

		SQL = new SQLhelper(this);

		// ControlP5
		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// User registration
		// user = (Tag_User) SQL.queryTagList("users").get(0);
		showFiles = SQL.queryTagList("files");

		// toxi VerletPhysics
		physics = new VerletPhysics();
		GravityBehavior g = new GravityBehavior(new Vec3D(0, 0, -0.01f));
		physics.addBehavior(g);

		// Display settings
		textFont(font, 14);
	}

	// /////////// draw ////////////////////

	public void draw() {
		background(0);

		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		showFiles();

		fill(150);
		if(user != null){
			text("User: " + user.name, 5, 16);
		}
		
		if(location != null){
			text("Location: " + location.name, 150, 16);
		}
		
		
		// Promt Messages
		if (p != null) {
			p.showMessages();
		}
		// List l = cp5.getAll();
		// text("pc5 List.size()" + l.size(), 5, 30);
		// text("Location: " + user.getName(), 5, 16);

		physics.update();
	}

	// ///////// display Files ////////////////////
	public void showFiles() {
		if (showFiles != null) {
			for (int i = 0; i < showFiles.size(); i++) {
				text(showFiles.get(i).name, 10, 40 + i * 16);
			}
		}
	}

	// ///////// INPUT ///////////////////
	public void keyPressed() {

		switch (key) {
		case 'O':
			String url = selectInput("Select a file to process:");
			if (url != null) {
				println("Create new File: url: " + url);
				Tag_File file = createNewFile("files", url);
				
				if(file != null){
					System.out.println(file.toString());
					if(user != null){
						//Tag_File file = (Tag_File) showFiles.get(0);
						SQL.bindTag(file, user);
					}
				}
				
				
				
			}
			
			break;
		case 'U':
			// Set User
			user = (Tag_User) SQL.queryTagList("users").get(0);
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
			Tag tag = new Tag_Location("locations", 5, "Ort", "coordinaten");
			SQL.bindTag(file, tag);
			break;

		}
	}

	// ///////////// Tag handling /////////////////////
	


	// ////////// Tag Creation /////////////////////
	public Tag_File createNewFile(String tableName, String s) {
		Tag_File file = null;
		s = s.trim();
		if (SQL.inDataBase(tableName, s)) {
			System.out.println("Tag " + s + " is already imported in "
					+ tableName);
		} else {
			file = (Tag_File) SQL.createDbTag(tableName, s);
		}
		return file;
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
			removeController();
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

	// public enum Tables
	// {
	// FILES, LOCATIONS, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	// }

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorer.TagExplorer.class.getName() });
	}

}

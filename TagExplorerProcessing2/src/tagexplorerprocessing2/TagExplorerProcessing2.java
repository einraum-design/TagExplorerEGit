package tagexplorerprocessing2;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeSet;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

import tagexplorerprocessing2.Connection.ConnectionType;
import tagexplorerprocessing2.Tag_File.FileType;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import treemap.MapLayout;
import treemap.SquarifiedLayout;
import treemap.Treemap;

// Anleitung jogl installation: http://www.3dcoding.de/2009/05/installation-von-eclipse-mit-jogl-unterstutzung/

public class TagExplorerProcessing2 extends PApplet {

	WatchDir watcher;

	PGraphics mainscreen;

	Treemap map = null;
	FileMap mapData = null;
	MapLayout layoutAlgorithm = new SquarifiedLayout();

	// PGraphics textu;
	PGraphics pg;

	Timeline timeline;
	TimeChooser timeChooser;

	// Tag_User user = new Tag_User("users", 0, "noname", "no Password");
	Tag_User mainUser = null;
	int aktuellerUserId = 0;
	Tag_Location location = null;

	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	ControlP5 cp5_Menu;
	ControlP5 cp5_Test;

	ArrayList<Tag> attributes = null;
	ArrayList<Tag> files = null;
	ArrayList<Tag> showFiles = null;
	ArrayList<Tag> availableTags = new ArrayList<Tag>();
	ArrayList<Tag> availableUsers = new ArrayList<Tag>();

	ArrayList<Tag> filterTagTypeList = new ArrayList<Tag>();

	ArrayList<Tag> applications = new ArrayList<Tag>();
	ArrayList<Tag> showApplications = new ArrayList<Tag>();
	ArrayList<Button_App> appButtons = new ArrayList<Button_App>();

	Tag_File oldest_showFile = null;
	Tag_File oldest_File = null;

	Timestamp minTime = null;
	Timestamp maxTime = null;

	SimpleDateFormat sdf;

	boolean showButtons = true;

	boolean showVersions = false;
	boolean drawAccessShapes = false;

	boolean drawTags = false;

	boolean draw2DShape = true;
	boolean drawTreemap = false;

	boolean showTimeline = false;

	boolean setZTimeAxis = false;
	boolean position1D = false;
	boolean position2D = true;
	// boolean position2Ddown = false;

	boolean enableVersionBinding = false;
	boolean enableTagBinding = false;
	boolean enableFileBinding = false;

	boolean setMinTime = false;

	ArrayList<Button_LabelToggle> testButton = new ArrayList<Button_LabelToggle>();

	// boolean

	// Timestamp oldestTime;

	ArrayList<Filter> filterList = new ArrayList<Filter>();

	// Interaction
	Tag startTag = null;
	boolean mouseActive = true;

	boolean startClickNextFrame = false;
	boolean clickNextFrame = false;

	Timestamp lastClick;

	// Camera
	// Vec3D cam_eye;
	// Vec3D cam_target;
	// Vec3D cam_up;
	// Vec3D cam_eye_target;

	Vec3D cam_eye2Dpos;
	// Vec3D cam_eye3Dpos;
	Vec3D cam_eyeaktuellpos;
	Vec3D cam_eyetargetpos;

	Vec3D cam_target;

	float xBillboardRotation = 0;
	float yBillboardRotation = 0;
	float zBillboardRotation = 0;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;
	VerletPhysics filePhysics;
	VerletPhysics appPhysics;

	PShape fileShape;
	PShape backgroundLines;
	PShape scala;
	
	ArrayList<ScalaWert> scalaWerte = new ArrayList<ScalaWert>();

	int contentBorders = 120;

	int contentStartOben;
	int contentStartUnten;

	String[] imageExtension = { "jpg", "jpeg", "png", "gif", "psd", "tif", "tiff", "bmp", "tga" };
	String[] vectorExtension = { "ai", "drw", "eps", "ps", "svg", "c4d" };
	String[] layoutExtension = { "indd", "pdf", "pxd", "pxp" };
	String[] audioExtension = { "aif", "m4a", "mid", "mp3", "mpa", "wav", "wma" };
	String[] videoExtension = { "avi", "flv", "mov", "mp4", "mpg", "swf", "wmv", "vob" };
	String[] textExtension = { "txt", "doc", "docx", "log", "pages", "rtf" };
	String[] webExtension = { "css", "htm", "html", "js", "jsp", "php", "rss", "xhtml" };
	String[] fontExtension = { "fnt", "fon", "otf", "ttf" };
	String[] messageExtension = { "msg", "eml" };

	HoverPlane hoverPlane = null;
	UserChooserPlane userChooser = null;

	MenuPlane menuPlane;

	NewsFeed newsFeed;

	PShader transition;
	PShader transition2;
	PShader plifShader;
	PImage env_plif;

	// Images
	PImage close;
	PImage close_h;
	PImage open;

	PImage cameraImg;

	PImage texture_connection;

	PImage texture_RECT;
	PImage texture_IMAGE;
	PImage texture_MESSAGE;
	PImage texture_TEXT;
	PImage texture_WEB;
	PImage texture_FONT;
	PImage texture_VIDEO;
	PImage texture_AUDIO;
	PImage texture_LAYOUT;
	PImage texture_VECTOR;
	PImage texture_CODE;

	PImage mapImg;
	PImage userImg;
	PImage controlImg;

	PImage newsButton;
	PImage newsButton_h;

	PImage backgroundTransition;

	PImage schatten;

	PImage appsButton;
	PImage backgroundApp;

	// Label miniaturen
	PImage minCall;
	PImage minEvent;
	PImage minFilter;
	PImage minKeyword;
	PImage minLocation;
	PImage minMessage;
	PImage minUser;
	PImage minDocument;
	PImage minProject;

	// Tag Miniaturen
	PImage tagMinProject;
	PImage tagMinUser;
	PImage tagMinEvent;
	PImage tagMinKeyword;
	PImage tagMinLocation;

	// new Tags
	PImage newLocation;
	PImage newUser;
	PImage newKeyword;
	PImage newProject;
	PImage newEvent;

	// news Types
	PImage newsCall;
	PImage newsAppointment;
	PImage newsFile;
	PImage newsMessage;

	// colors
	int cBorderHover;
	int cBorder;
	int cFont;
	int cBorderBright;

	int cButtonBright;
	int cDropdownHover;

	int cPopUp;
	int cTagDark;

	Tag_Comparator_Id comp_id;
	Tag_Comparator_Time comp_time;
	App_Comparator_Count comp_appcount;

	// PImage

	// public void init() {
	// frame.removeNotify();
	// frame.setUndecorated(true);
	// // frame.setAlwaysOnTop(true);
	// frame.addNotify();
	// super.init();
	// }

	public void setup() {
		size(1400, 1050, P3D);

		// size(1024, 768, P3D);
		// frame.setLocation(1970, 50);
		// frame.setLocation(0,0);
		smooth(4);

		// addMouseWheelListener(new MouseWheelListener() {
		// public void mouseWheelMoved(MouseWheelEvent mwe) {
		// mouseWheel(mwe.getWheelRotation());
		// }
		// });

		font = createFont("arial", 40);
		textFont(font, 14);

		loadImages();

		cBorderHover = color(82, 219, 209);
		cBorder = color(132, 132, 132);
		cBorderBright = color(220);

		cFont = color(132, 132, 132);

		cButtonBright = color(241);
		cDropdownHover = color(230);

		cPopUp = color(111, 126, 132);
		cTagDark = color(72, 86, 92);

		// Date Formatter
		sdf = new SimpleDateFormat();
		sdf.setTimeZone(new SimpleTimeZone(1, "GMT"));
		// sdf.applyPattern("dd MMM yyyy HH:mm:ss");

		// Fill the tables for arcs
		sinLUT = new float[SINCOS_LENGTH];
		cosLUT = new float[SINCOS_LENGTH];
		for (int i = 0; i < SINCOS_LENGTH; i++) {
			sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
			cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
		}

		// ControlP5
		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);
		cp5_Test = new ControlP5(this);

		// cp5_Test.addToggle("showVersions", false, width - 120, 20, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("drawAccessShapes", false, width - 120, 60, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("draw2DShape", false, width - 120, 90, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("setZTimeAxis", false, width - 120, 130, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("position1D", false, width - 120, 170, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("position2D", true, width - 120, 200, 10,
		// 10).getCaptionLabel().setColor(color(0));
		//
		// cp5_Test.addToggle("last", false, width - 120, 240, 10,
		// 10).getCaptionLabel().setColor(color(0));
		//
		// cp5_Test.addToggle("enableVersionBinding", false, width - 120, 280,
		// 10, 10).getCaptionLabel()
		// .setColor(color(0));
		// cp5_Test.addToggle("enableTagBinding", false, width - 120, 310, 10,
		// 10).getCaptionLabel().setColor(color(0));
		// cp5_Test.addToggle("enableFileBinding", false, width - 120, 340, 10,
		// 10).getCaptionLabel().setColor(color(0));

		testButton.add(new Button_LabelToggle(this, "showVersions", false, 10, 20, width - 120, 20));
		testButton.add(new Button_LabelToggle(this, "drawAccessShapes", false, 10, 20, width - 120, 50));
		testButton.add(new Button_LabelToggle(this, "draw2DShape", false, 10, 20, width - 120, 80));
		testButton.add(new Button_LabelToggle(this, "drawTreemap", false, 10, 20, width - 120, 110));
		testButton.add(new Button_LabelToggle(this, "setZTimeAxis", false, 10, 20, width - 120, 150));
		testButton.add(new Button_LabelToggle(this, "position1D", false, 10, 20, width - 120, 190));
		testButton.add(new Button_LabelToggle(this, "position2D", false, 10, 20, width - 120, 210));
		// testButton.add(new Button_LabelToggle(this, "position2Ddown", false,
		// 10, 20, width - 40, 210));

		testButton.add(new Button_LabelToggle(this, "last", false, 10, 20, width - 20, 230));
		testButton.add(new Button_LabelToggle(this, "enableVersionBinding", false, 10, 20, width - 120, 270));
		testButton.add(new Button_LabelToggle(this, "enableTagBinding", false, 10, 20, width - 120, 300));
		testButton.add(new Button_LabelToggle(this, "enableFileBinding", false, 10, 20, width - 120, 330));
		testButton.add(new Button_LabelToggle(this, "setMinTime", false, 10, 20, width - 120, 370));
		testButton.add(new Button_LabelToggle(this, "drawTags", false, 10, 20, width - 120, 400));

		// FrameRate
		cp5_Menu.addFrameRate().setInterval(10).setPosition(width - 100, 10).setColor(color(50)); // .setFont(font)

		menuPlane = new MenuPlane(this);

		newsFeed = new NewsFeed(this, 0, height / 5);

		mainscreen = createGraphics(width, height, P3D); // height-40
		pg = createGraphics(100, 100, P2D);
		// mainscreen.smooth(4);

		// camera
		// cam_eye = new Vec3D(mainscreen.width / 2.0f, mainscreen.height /
		// 2.0f, (mainscreen.height / 2.0f)
		// / tan(PI * 30.0f / 180.0f));
		//
		// cam_eye_target = new Vec3D(mainscreen.width / 2.0f, mainscreen.height
		// / 2.0f, (mainscreen.height / 2.0f)
		// / tan(PI * 30.0f / 180.0f));
		// cam_target = new Vec3D(mainscreen.width / 2.0f, mainscreen.height /
		// 2.0f, 0);
		// cam_up = new Vec3D(0, 1, 0);
		//
		// mainscreen.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x,
		// cam_target.y, cam_target.z, cam_up.x,
		// cam_up.y, cam_up.z);

		cam_eye2Dpos = new Vec3D(width / 2.0f, height / 2.0f, (height / 2.0f) / tan(PI * 30.0f / 180.0f));
		// cam_eye3Dpos = new Vec3D(width / 2.0f, height * 2, (height / 2.0f) /
		// tan(PI * 30.0f / 180.0f));

		cam_eyeaktuellpos = cam_eye2Dpos.copy();
		cam_eyetargetpos = cam_eyeaktuellpos;

		cam_target = new Vec3D(width / 2.0f, height / 2.0f, cam_eyeaktuellpos.z - (height / 2.0f)
				/ tan(PI * 30.0f / 180.0f));

		mainscreen.camera(cam_eyeaktuellpos.x, cam_eyeaktuellpos.y, cam_eyeaktuellpos.z, cam_target.x, cam_target.y,
				cam_target.z, 0, 1, 0);

		// transition = loadShader("../shader/transition.glsl");
		// transition.set("res", (float) (mainscreen.width), (float)
		// (mainscreen.height));
		// transition.set("color", (1.0f), (1.0f), (1.0f));
		//
		// transition2 = loadShader("../shader/transition.glsl");
		// transition2.set("res", 100.0f, 100.0f);
		// transition2.set("color", (1.0f), (0.1f), (0.1f));
		//
		// plifShader = loadShader("../shader/plifFrag.glsl",
		// "../shader/plifVert.glsl");
		// env_plif = loadImage("../data/env_plif.jpg");

		// fileShape = createShape(SPHERE, 10);
		fileShape = createShape(BOX, 20);
		fileShape.noStroke();
		fileShape.fill(cBorderHover);
		// fileShape.emissive(0, 0, 50);

		// ball.shininess(0);

		// am start
		// int contentBorders = 120;

		contentStartOben = mainscreen.height / 2 - (int) menuPlane.h - contentBorders / 2;
		contentStartUnten = mainscreen.height / 2 - contentBorders / 2;

		Path p = FileSystems.getDefault().getPath("/Users/manuel/Documents/Testumgebung/UserOrdner");
		// Path p =
		// FileSystems.getDefault().getPath("/Users/manuel/Documents/Testumgebung/Test");

		// FileSystem Watcher
		try {
			watcher = new WatchDir(this, p, true);
			watcher.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// SQL HELPER

		// SQL = new SQLhelper(this, "root", "root", "files2_db",
		// "localhost:8889");
		// SQL = new SQLhelper(this, "root", "root", "files_db",
		// "localhost:8889");
		SQL = new SQLhelper(this);

		// TIMELINE
		timeline = new Timeline(this);

		// TimeChooser
		timeChooser = new TimeChooser(this, width - 80, height / 5);

		// Comparator
		comp_id = new Tag_Comparator_Id();
		comp_time = new Tag_Comparator_Time();
		comp_appcount = new App_Comparator_Count();

		// Standartuser: Öffentlich
		// aktuellerUserId = 2;
		// mainUser = (Tag_User) SQL.queryTagList("users").get(0);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// toxi VerletPhysics
		physics = new VerletPhysics();
		// GravityBehavior g = new GravityBehavior(new Vec3D(0, 0, -0.01f));
		// physics.addBehavior(g);
		filePhysics = new VerletPhysics();
		appPhysics = new VerletPhysics();

		// erst Tags, dann Files!
		attributes = initTagsFromDB();
		initFilterTagTypeList(); // add these to Filterauswahl/
		// availbleFilterList (oder so ähnlich)

		initFilesFromDB();

		oldest_File = (Tag_File) getOldestTagFile(files);

		updateShowFiles();
		updateTags();
		updateSprings();

		initApplications();

		// sets Default main user
		mainUser = (Tag_User) getTagByID("users", 2);
		filterList.add(new Filter(mainUser, true));
		println("Default main User in setup: " + mainUser.name);

		lastClick = new Timestamp(System.currentTimeMillis());

		// Background Lines
		backgroundLines = generateBackgroundLines();
		scala = generateScala();
	}

	// void mouseWheel(int delta) {
	// println("mousewheel: " + delta);
	// cam_eye.z += delta;
	// }

	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	// /////////// draw ////////////////////
	boolean first = true;

	// boolean updateTextures = false;

	public void draw() {

		if (first) {
			// init file.textur
			initTextures();
			first = false;

		}

		if (startClickNextFrame) {
			clickNextFrame = true;
			startClickNextFrame = false;
		}
		
		

		// if (updateTextures) {
		//
		// // erzeuge TreemapTexturen
		// if (drawTreemap) {
		// initTextures(showFiles);
		// println("tremapTextures");
		// }
		// // erzeuge StandartTexturen;
		// else{
		// initTextures();
		// println("normal textures");
		// }
		// updateTextures = false;
		//
		// }

		if (!position1D) {
			cam_eyetargetpos.x = cam_eye2Dpos.x;
		}

		// update interpolate cam position
		cam_eyeaktuellpos = interpolateVec(cam_eyeaktuellpos, cam_eyetargetpos);

		// target.z wird mitverschoben
		cam_target.z = cam_eyeaktuellpos.z - (height / 2.0f) / tan(PI * 30.0f / 180.0f);
		cam_target.x = cam_eyeaktuellpos.x;

		// bei neu hinzugefügten Files textur generieren
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;
			if (file.textur == null) {

				// position setzten

				// textur erzeugen
				file.setTextur(generateTexture(file));

				// shape erzeugen
				setShape(file);
			}
		}

		background(255);

		// lights();

		// imageMode(CORNER);
		// image(backgroundTransition, 0,0);

		// PShape back = createShape(PConstants.RECT, -100, 0, width + 200,
		// height);
		PShape back = createShape();

		back.texture(backgroundTransition);

		back.vertex(0, 0, 0, 0);
		back.vertex(width, 0, backgroundTransition.width, 0);
		back.vertex(width, height, backgroundTransition.width, backgroundTransition.height);
		back.vertex(0, height, 0, backgroundTransition.height);

		// back.vertex(-100, 0, 0, 0);
		// back.vertex(width + 200, 0, backgroundTransition.width, 0);
		// back.vertex(width + 200, height, backgroundTransition.width,
		// backgroundTransition.height);
		// back.vertex(-100, height, 0, backgroundTransition.height);
		// back.fill(255);
		back.end();

		back.translate(0, (cam_eyeaktuellpos.y - height / 2.0f) * 2);
		back.scale(1, map(cam_eyeaktuellpos.y, height / 2, 0, 1, 2));
		shape(back);

		// set Mouse active nach jeweils 600 millis;
		if (System.currentTimeMillis() > lastClick.getTime() + 800) { // 1200
			mouseActive = true;
		} else {
			mouseActive = false;
		}

		// println("mouseActive: " + mouseActive);

		// calcBillboardRotation();
		// transition.set("shaderTime", (millis() / 1000.0f));
		// transition2.set("shaderTime", (millis() / 1000.0f));

		// plifShader.set("shininess", 5.1f);
		// plifShader.set("EnvMap", 0);
		// plifShader.set("MixRatio", 0.2f);
		// plifShader.set("SpecularVal", 0.2f);

		// modelTextur
		// drawPGTexture();

		// control p5 Promt
		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		// remove UserChooser wenn user gewählt
		if (userChooser != null && userChooser.ok) {
			println("remove UserChooser + TextInputFild");
			cp5_Menu.get(Textfield.class, userChooser.inputFieldName).remove();
			userChooser = null;
		}

		// überprüfe ob mainUser in filterList
		if (mainUser != null) {
			boolean userInFilterList = false;
			for (Filter f : filterList) {
				if (f.tag instanceof Tag_User && f.tag.id == mainUser.id) {
					userInFilterList = true;
				}
			}
			if (!userInFilterList) {
				mainUser = null;
				// updateApplications();
			}
		}

		// draw mainscene

		if (mainUser != null) {

			// if (minTime == null) {

			drawMainscreen(mainscreen);

			// } else {
			// // drawMainscreen(mainscreen); // alte mit blur
			// // drawMainscreen(mainscreen); // aktuelle
			// }
		} else {
			// erstelle und draw userChooser
			drawUserChooser();
		}

		// drawApplications
		for (Button_App b : appButtons) {
			b.render();
			// if (mouseActive && b.mouseOver() && mousePressed) {
			if (clickNextFrame && b.mouseOver()) {
				if (b.app != null && b.app.url != null) {
					println("open: " + b.app.url);
					open(b.app.url);
				}
				lastClick = new Timestamp(System.currentTimeMillis());
				mouseActive = false;
				clickNextFrame = false;
			}
		}

		// draw Map rects
		// if (map != null) {
		// map.draw();
		// }

		// MenuPlane
		menuPlane.render();
		menuPlane.update();

		// TimeChoose
		timeChooser.render();

		// NewsFeed
		if (mainUser != null) {
			newsFeed.render();
		}

		// Hover Plane Dateiinfos
		if (hoverPlane != null) {
			if (hoverPlane.mouseOver()) {
				hoverPlane.render();
				// println("render hoverplane");
			} else {
				// delete Textfield
				if (hoverPlane.openButton != null) {
					cp5_Menu.get(Textfield.class, hoverPlane.inputFieldName).remove();
				}
				hoverPlane = null;
			}
		}

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
		
		
		setButtonStates();

		if (showButtons) {
			for (Button_LabelToggle b : testButton) {
				b.render();

				// if (mouseActive && b.mouseOver() && mousePressed) {
				if (clickNextFrame && b.mouseOver()) {

					switch (b.label) {
					case "showVersions":
						b.toggle();
						showVersions(b.onOff);
						break;
					case "drawAccessShapes":
						b.toggle();
						drawAccessShapes(b.onOff);
						break;
					case "draw2DShape":
						b.toggle();
						draw2DShape(b.onOff);
						break;
					case "drawTreemap":
						b.toggle();
						drawTreemap(b.onOff);
						break;
					// case "showTimeline":
					// b.toggle();
					// showTimeline(b.onOff);
					// break;
					case "setZTimeAxis":
						b.toggle();
						setZTimeAxis(b.onOff);
						break;
					case "position1D":
						b.toggle();
						position1D(b.onOff);
						break;
					case "position2D":
						b.toggle();
						position2D(b.onOff);
						break;
					// case "position2Ddown":
					// b.toggle();
					// position2Ddown(true);
					// break;
					case "enableVersionBinding":
						b.toggle();
						enableVersionBinding(b.onOff);
						break;
					case "enableTagBinding":
						b.toggle();
						enableTagBinding(b.onOff);
						break;
					case "enableFileBinding":
						b.toggle();
						enableFileBinding(b.onOff);
						break;
					case "setMinTime":
						b.toggle();
						setMinTime(b.onOff);
						break;
					case "drawTags":
						b.toggle();
						drawTags(b.onOff);
						break;
					}

					lastClick = new Timestamp(System.currentTimeMillis());
					mouseActive = false;
					clickNextFrame = false;
				}
			}
		}

		physics.update();
		filePhysics.update();
		appPhysics.update();

		// if(startClickNextFrame){
		// startClickNextFrame = false;
		// }
		clickNextFrame = false;
	}

	public void drawPGTexture() {
		pg.beginDraw();
		pg.shader(transition2);
		pg.fill(25, 0, 0);
		pg.rect(0, 0, pg.width, pg.height);
		pg.resetShader();
		pg.endDraw();
	}

	public void drawUserChooser() {
		if (userChooser == null) {
			minTime = null;
			filterList.clear();
			userChooser = new UserChooserPlane(this, width / 2, height / 5);
		}
		userChooser.render();
	}

	private void drawMainscreen(PGraphics renderer) {
		// renderer begin:
		renderer.beginDraw();
		renderer.smooth(4);

		renderer.background(150, 0);

		// pulse shader
		// renderer.hint(DISABLE_DEPTH_MASK);
		// renderer.fill(255);
		// renderer.shader(transition);
		//
		// renderer.rectMode(CENTER);
		// renderer.rect(renderer.width / 2, renderer.height / 2, renderer.width
		// * 20, renderer.height * 20);
		// renderer.resetShader();
		// renderer.hint(ENABLE_DEPTH_MASK);

		// renderer.directionalLight(0, 255, 0, 0, -1, 0);

		// renderer.rotateY(mainScreenYRotation);
		// renderer.translate(-50, 0, 0);
		// renderer.camera();

		// cam_eye.x = interpolate(cam_eye.x, cam_eye_target.x);
		// cam_eye.y = interpolate(cam_eye.y, cam_eye_target.y);
		// cam_eye.z = interpolate(cam_eye.z, cam_eye_target.z);
		//
		// renderer.camera(cam_eye.x, cam_eye.y, cam_eye.z, cam_target.x,
		// cam_target.y, cam_target.z, cam_up.x, cam_up.y,
		// cam_up.z);
		float fov = PI / 3.0f;
		float cameraZ = (mainscreen.height / 2.0f) / tan(fov / 2.0f);

		renderer.perspective(fov, (float) (renderer.width) / (float) (renderer.height), cameraZ / 10.0f,
				cameraZ * 25.0f);
		// renderer.perspective(fov,
		// (float)(renderer.width)/(float)(renderer.height), cameraZ/10.0f,
		// cameraZ*10.0f);
		renderer.camera(cam_eyeaktuellpos.x, cam_eyeaktuellpos.y, cam_eyeaktuellpos.z, cam_target.x, cam_target.y,
				cam_target.z, 0, 1, 0);

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

		renderer.shape(backgroundLines);
		renderer.shape(scala);
		
		renderer.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
		renderer.textFont(font, 24);
		renderer.fill(0);
		
		for(ScalaWert sw : scalaWerte){
			
			renderer.pushMatrix();
			renderer.translate(-renderer.width / 2, renderer.height / 2, sw.z);
			renderer.translate(-50, -50, 0);
			renderer.rotateZ(PI/4.0f);
			
			renderer.text(sw.zeit, 0, 0);
			renderer.popMatrix();
			
		}
		

		if (showTimeline) {
			timeline.render(renderer);
		}

		drawFiles(renderer);

		// tags werden immer erzeugt aber neu bei drawTags = true gezeichnet
		if (drawTags) {
			drawTags(renderer);
		}

		// springs werden mit enableTagBinding .... erst erzeugt
		// drawSprings(renderer);
		drawSpringsLINES(renderer);

		// draw interaction -renderer.height/2
		stroke(255, 0, 0);
		if (startTag != null) {
			renderer.strokeWeight(15);
			renderer.point(startTag.x, startTag.y, startTag.z);
			if (hoverPoint != null) {
				renderer.strokeWeight(3);
				renderer.line(startTag.x, startTag.y, startTag.z, hoverPoint.x, hoverPoint.y, hoverPoint.z);
			}
		}

		renderer.popMatrix();
		renderer.endDraw();
		// renderer end

		imageMode(PConstants.CORNER);
		image(renderer, 0, 0);
	}

	// ///////// display Methods ////////////////////

	public void drawFiles(PGraphics renderer) {

		// turn lights on
		// renderer.lights();

		PShape s = createShape(GROUP);

		if (filePhysics.particles != null) {

			// println("filePhysics.particles.size(): " +
			// filePhysics.particles.size());

			// renderer.shader(plifShader);
			boolean drawShape = false;
			renderer.fill(0);
			for (int i = 0; i < filePhysics.particles.size(); i++) {
				Tag_File file = (Tag_File) filePhysics.particles.get(i);

				// versionsShape
				if (file.shape != null) {
					drawShape = true;
					s.addChild(file.shape);

					if (draw2DShape && (position1D || file.y > renderer.height / 2 - 70)) {
						PShape shadow = createShape();
						shadow.fill(0);
						shadow.noStroke();
						shadow.texture(schatten);

						shadow.vertex(file.x - 70, file.y + 59, file.z + 12, 0, 0);
						shadow.vertex(file.x - 70, file.y + 59, file.z - 12, 0, schatten.height);
						shadow.vertex(file.x + 70, file.y + 59, file.z - 12, schatten.width, schatten.height);
						shadow.vertex(file.x + 70, file.y + 59, file.z + 12, schatten.width, 0);

						shadow.end();
						s.addChild(shadow);
					}

					// debug
					// if (mousePressed) {
					// println((Tag_File) filePhysics.particles.get(i));
					// }

					// if (draw2DShape) {
					// renderer.pushMatrix();
					// renderer.translate(file.x, file.y, file.z);
					// file.renderFileName(this, renderer);
					// renderer.popMatrix();
					// }
				}
				// balls statt 3D-shapes
				else {
					renderer.pushMatrix();
					renderer.translate(file.x, file.y, file.z);
					renderer.shape(fileShape);
					// Plane mit Text
					file.renderFileName(this, renderer);
					renderer.popMatrix();

				}

				// erstelle Hoverplane
				if (!mousePressed && !cp5_Menu.get(Textfield.class, menuPlane.inputFieldName).isFocus()
						&& mouseOver(renderer, file, 30, 30)) {
					if (hoverPlane == null) {
						hoverPlane = new HoverPlane(this, file, (int) mainscreen.screenX(file.x, file.y, file.z),
								(int) mainscreen.screenY(file.x, file.y, file.z));
					}
				}
			}
			if (drawShape) {
				renderer.shape(s);
			}
			// renderer.resetShader();
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

			if (!mousePressed && !cp5_Menu.get(Textfield.class, menuPlane.inputFieldName).isFocus()
					&& mouseOver(renderer, tag, 30, 30)) {
				if (hoverPlane == null) {
					// tag.lock();
					hoverPlane = new HoverPlane(this, tag, (int) mainscreen.screenX(tag.x, tag.y, tag.z),
							(int) mainscreen.screenY(tag.x, tag.y, tag.z));
					// println("Create hoverPlane");
				}
			}
		}
	}

	public PShape generateTypeSprings(ConnectionType type) {
		PShape lines = createShape(LINES);
		lines.stroke(0);
		lines.strokeWeight(1);

		for (int i = 0; i < physics.springs.size(); i++) {
			Connection sp = (Connection) physics.springs.get(i);

			if (sp.type == type) {

				Vec3D vec1 = new Vec3D(sp.a.x, sp.a.y, sp.a.z);
				Vec3D vec2 = new Vec3D(sp.b.x, sp.b.y, sp.b.z);

				float dist = vec1.distanceTo(vec2) / 2;
				Vec3D middle = (vec1.add(vec2)).scale(0.5f);
				Vec3D peak = new Vec3D(middle.x, middle.y - dist, middle.z);

				lines.vertex(sp.a.x, sp.a.y, sp.a.z);

				lines.vertex(peak.x, peak.y, peak.z);
				lines.vertex(peak.x, peak.y, peak.z);

				lines.vertex(sp.b.x, sp.b.y, sp.b.z);
			}
		}

		lines.end();
		return lines;
	}

	public void drawSpringsLINES(PGraphics renderer) {

		PShape shape = createShape(GROUP);

		// if (enableVersionBinding) {
		PShape shapeVersions = generateTypeSprings(ConnectionType.VERSION);
		shapeVersions.stroke(255, 0, 0);
		shape.addChild(shapeVersions);
		// }

		// if (enableFileBinding) {
		PShape shapeFilebindings = generateTypeSprings(ConnectionType.FILEBINDING);
		shapeFilebindings.stroke(0, 255, 0);
		shape.addChild(shapeFilebindings);
		// }

		// if (enableTagBinding) {
		PShape shapeTagbindings = generateTypeSprings(ConnectionType.TAGBINDING);
		shapeTagbindings.stroke(0, 0, 255);
		shape.addChild(shapeTagbindings);
		// }

		renderer.shape(shape);
	}

	public void drawSpringsARCS(PGraphics renderer) {
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
		}

		renderer.shape(shape);
	}

	public void drawSprings(PGraphics renderer) {
		// PShape shape = createShape(GROUP);

		for (int i = 0; i < physics.springs.size(); i++) {
			Connection sp = (Connection) physics.springs.get(i);

			// PShape s = createArc(sp.a, sp.b);

			switch (sp.type) {
			case VERSION:
				// s.stroke(255, 0, 0);
				renderer.stroke(255, 0, 0);
				break;
			case FILEBINDING:
				// s.stroke(0, 255, 0);
				renderer.stroke(0, 255, 0);
				break;
			case TAGBINDING:
				// s.stroke(0, 0, 255);
				renderer.stroke(0, 0, 255);
				break;
			}

			// shape(s);
			// shape.addChild(s);
			renderer.strokeWeight(1);
			renderer.line(sp.a.x, sp.a.y, sp.a.z, sp.b.x, sp.b.y, sp.b.z);
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

		// oldest_showFile = ((Tag_File) getOldestTagFile(this.files));
		// return files;
	}

	private void initFilterTagTypeList() {
		for (Tag_File.FileType typ : Tag_File.FileType.values()) {
			filterTagTypeList.add(new Tag_FileType(typ));
		}
	}

	public void initTextures() {
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;

			// file.setTextur(generateTexture((Tag_File) file);
			file.setTextur(generateTexture((Tag_File) file, 100, 100));
		}
	}

	public void initTextures(ArrayList<Tag> showFiles) {
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;

			// file.setTextur(generateTexture((Tag_File) file);
			file.setTextur(generateTexture((Tag_File) file, file.treemapW, file.treemapH));
		}
	}

	public void initApplications() {

		applications = SQL.queryTagList("applications");
		for (Tag t : applications) {
			Tag_App app = (Tag_App) t;
			app.setImage(loadImage(VersionBuilder.versionsVerzeichnis + "applications/" + app.imgName));

			// update App-Tag Bindings
			updateAppTagBindings(app);
		}

		// setPosition und filter
		updateApplications();
	}

	public ArrayList<Tag> initTagsFromDB() {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		tags = SQL.queryTagList("keywords");
		tags.addAll(SQL.queryTagList("locations"));
		tags.addAll(SQL.queryTagList("projects"));
		tags.addAll(SQL.queryTagList("users"));

		availableUsers.clear();
		availableUsers.addAll(SQL.queryTagList("users"));

		// tags.addAll(SQL.queryTagList("files"));
		// tags nicht überscheiben, sondern nur abgleichen!
		return tags;
	}

	// ///////// update Methods ////////////////////
	public void updateShowFiles() {
		System.out.println("updateShowFiles()");

		// count matches mit FilterList
		updateMatches(files);

		// Filter files
		if (filterList.size() > 0) {
			// debug
			// for (Filter f : filterList) {
			// println("aktuelle Filter: " + f.tag.name);
			// }

			// DB Abfrage
			// showFiles = SQL.queryTagListFiltered("files", filterList);

			// alternativ Java Abfrage:
			showFiles = getFullMatches(files);
		} else {
			// alle files
			showFiles = files;
		}

		// ArrayList<Tag> showFilesCopy = (ArrayList<Tag>) showFiles.clone();
		ArrayList<Tag> newest = getNewestTagFileVersions(showFiles);

		// nur neueste werden showFiles
		if (!showVersions) {
			// showFiles = getNewestTagFileVersions(showFiles);
			showFiles = newest;
		}

		// Filter nach minTime
		if (minTime != null) {
			println("	updateShowFiles(): showFiles.size():" + showFiles.size());
			// Filter nach verwendungszeit später als:
			ArrayList<Tag> results = new ArrayList<Tag>();

			// ohne Versions

			for (Tag tag : newest) {
				Tag_File file = (Tag_File) tag;
				if (getNewestDate(file).after(minTime)) {
					results.add(file);
					// println(file.name + " " +
					// getNewestDate(file).toGMTString());
				}
			}
			if (!showVersions) {
				showFiles = results;
				println("	updateShowFiles(): showFiles.size():" + showFiles.size());
			} else {

				// mit Versionen
				ArrayList<Tag> resultsAndVersions = new ArrayList<Tag>();
				for (Tag tag : results) {
					Tag_File file = (Tag_File) tag;
					if (file.origin_ID != 0) {
						resultsAndVersions.addAll(getAllVersions(file.origin_ID));
					} else {
						resultsAndVersions.add(file);
					}
				}
				showFiles = resultsAndVersions;
				println("	updateShowFiles(): showFiles.size():" + showFiles.size());
			}

		}

		// create Map Position für Treemap
		if (drawTreemap && showFiles.size() > 0) {
			if (!showVersions) {
				println("	updateShowFiles(): createTreeMap");
				createTreeMap(showFiles);
			} else {
				createTreeMap(getNewestTagFileVersions(showFiles));
			}
		}

		oldest_showFile = (Tag_File) getOldestTagFile(showFiles);
		// System.out.println("oldest_showFile: " +
		// oldest_showFile.creation_time.toGMTString());

		// set Timeline Wertebereich
		if (oldest_showFile != null) {
			timeline.setWertebereich(oldest_showFile.creation_time);
		} else {
			timeline.oldest = null;
			println("timeline.oldest = null!");
		}

		// println("before setParticelPositions : setZTimeAxis = " +
		// setZTimeAxis);

		// schicke 8 aktuellsten meldungen an NewsFeed

		// funktioniert noch nicht
		// ArrayList<Tag> sortedFiles = getFilesSortedLastAccess(showFiles);
		// ArrayList<Tag> filesToNews = new ArrayList<Tag>();
		// if(sortedFiles.size() > 10){
		// for(int i = 0; i<10; i++){
		// filesToNews.add(sortedFiles.get(i));
		// }
		// } else{
		// filesToNews = sortedFiles;
		// }
		//
		// println("filesToNews.size()" + filesToNews.size());
		// newsFeed.setNewNews(filesToNews);

		// set Partikel Position
		setParticlePositions(showFiles);

		// setzt shape oder null
		 setShape(); // wird direkt bei drawTreemap(), draw2DShape() und drawAccessShape() aufgerufen

		// println("setZTimeAxis = " + setZTimeAxis);
		setZTimeAxis(setZTimeAxis); // macht auch updateSprings

	}

	public void setParticlePositions(ArrayList<Tag> showFiles) {
		// hard treemap!
		if (drawTreemap) {
			
			setParticlesPositionTreeMap(filePhysics, showFiles);
		} else if (position1D) {
			
			setParticlesPosition1D(filePhysics, showFiles);
		} else if (position2D) {
			System.out.println("setParticlesPosition2D() 2D");
			setParticlesPosition2D(filePhysics, showFiles);
		} else {
			System.out.println("DEFAULT: setParticlesPosition2D() 2D");
			setParticlesPosition2D(filePhysics, showFiles);
		}
	}

	void updateApplications() {
		appPhysics.particles.clear();

		ArrayList<Filter> appFilterList = getUserLocationFilters(filterList);
		updateAppMatches(applications, appFilterList);

		// filter files
		if (appFilterList.size() > 0) {
			// DB Abfrage
			// showFiles = SQL.queryTagListFiltered("files", filterList);

			// alternativ Java Abfrage:
			showApplications = getUserLocationMatches(applications, appFilterList);
		} else {
			// alle apps
			// showApplications = applications;
			// keine apps
			showApplications.clear();
		}

		// sort showApplications nach count
		Collections.sort(showApplications, comp_appcount);

		// app Buttons löschen
		appButtons.clear();

		// calc maße und erzeuge Buttons

		int allAccesses = 0;
		for (Tag tag : showApplications) {
			Tag_App app = (Tag_App) tag;
			allAccesses += app.count;
		}

		int seitenAbstand = 120;
		int h = 36;

		int xShift = 0;
		int wApps = 0;

		for (Tag tag : showApplications) {
			Tag_App app = (Tag_App) tag;

			int w = (int) map(app.count, 0, allAccesses, 0, width - seitenAbstand * 2 - h);
			// System.out.println("w: " + app.name + " " + w);

			if (w >= h) {
				appButtons.add(new Button_App(this, app, w, h, xShift + seitenAbstand, height - h - 10));
				xShift += w;
			} else {
				wApps += w; // Button zu kleine, vergrößere Apps Button um Beite
							// auszugleichen
			}
		}
		if (showApplications.size() == 0) {
			xShift = width - seitenAbstand * 2 - h;
		}
		appButtons.add(new Button_App(this, "show all", appsButton, h + wApps, h, xShift + seitenAbstand, height - h
				- 10));

		// particle visualisierung

		// float dist;
		// if (showApplications.size() > 1) {
		// dist = 120;
		// } else {
		// dist = 0;
		// }
		//
		// int shiftCount = 0;
		//
		// for (int i = 0; i < showApplications.size(); i++) {
		// dropParticle(appPhysics, shiftCount * dist - ((dist *
		// (showApplications.size() - 1)) / 2.0f), 0,
		// showApplications.get(i), true); // links/rechts
		// shiftCount++;
		// }

	}

	private ArrayList<Filter> getUserLocationFilters(ArrayList<Filter> filters) {
		ArrayList<Filter> userLocationFilters = new ArrayList<Filter>();

		for (Filter f : filters) {
			// if (f.tag.type.equals("users") || f.tag.type.equals("locations"))
			// {

			// nur mainUser als User Filter!
			if (mainUser != null && f.tag.type.equals("users") && f.tag.id == mainUser.id) {
				userLocationFilters.add(f);
			}

			if (f.tag.type.equals("locations")) {
				userLocationFilters.add(f);
			}
		}

		println("getUserLocationFilters.size(): " + userLocationFilters.size());
		return userLocationFilters;
	}

	private ArrayList<Tag> getFullMatches(ArrayList<Tag> files) {
		ArrayList<Tag> fullMatches = new ArrayList<Tag>();
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			if (file.matches == filterList.size()) {
				fullMatches.add(file);
				// println("getFullMatches add: " + file.name);
			}
		}
		return fullMatches;
	}

	private ArrayList<Tag> getUserLocationMatches(ArrayList<Tag> apps, ArrayList<Filter> filters) {
		ArrayList<Tag> fullMatches = new ArrayList<Tag>();

		for (Tag t : apps) {
			Tag_App app = (Tag_App) t;
			if (app.matches == filters.size()) {
				fullMatches.add(app);
			}
		}
		return fullMatches;
	}

	// zählt matches mit Filter ++ oder bei inOut false --
	public void updateMatches(ArrayList<Tag> files) {
		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			// set matches = 0
			file.matches = 0;

			// count matches mit filterList
			for (Filter f : filterList) {

				// FiltType Filter
				if (f.tag instanceof Tag_FileType) {
					if (f.inOut) {
						if (file.fileType == ((Tag_FileType) f.tag).fileType) {
							file.matches++;
						}
					} else {
						if (file.fileType == ((Tag_FileType) f.tag).fileType) {
							file.matches--;
						}
					}
				}

				// normaler Tag Filter
				else {

					if (f.inOut) {
						for (Tag tt : file.attributeBindings) {
							if (tt.type.equals(f.tag.type) && tt.id == f.tag.id) {
								file.matches++;
							}
						}

						// contains funktioniert nicht
						// if (file.attributeBindings.contains(f.tag)) {
						// file.matches++;
						// println("updateMatches(): " + file.name + " id " +
						// file.id + " contains Tag: " + f.tag.name);
						// }
					} else {
						for (Tag tt : file.attributeBindings) {
							if (tt.type.equals(f.tag.type) && tt.id == f.tag.id) {
								file.matches--;
							}
						}
					}
				}
			}
		}
	}

	public void updateAppMatches(ArrayList<Tag> apps, ArrayList<Filter> filters) {
		for (Tag t : apps) {
			Tag_App app = (Tag_App) t;
			// set matches = 0
			app.matches = 0;

			// count matches mit filterList
			for (Filter f : filters) {

				if (f.inOut) {
					if (app.attributeBindings.contains(f.tag)) {
						app.matches++;
					}
				} else {
					if (app.attributeBindings.contains(f.tag)) {
						app.matches--;
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

		attributes = initTagsFromDB();

		// reset bindCount attributes
		for (Tag tag : attributes) {
			tag.bindCount = 0;
		}

		// reset bindCount fileTagTypeList
		for (Tag tag : filterTagTypeList) {
			tag.bindCount = 0;
		}

		// aktuelle FileBindings
		if (showFiles != null) {
			for (Tag f : showFiles) {
				Tag_File file = (Tag_File) f;

				for (Tag tag : file.attributeBindings) {
					// zählt files, die mit Attribut verbunden snd
					tag.bindCount++;
				}

				// zählt fileTypen hoch
				for (Tag ftype : filterTagTypeList) {
					if (file.fileType == ((Tag_FileType) ftype).fileType) {
						ftype.bindCount++;
					}
				}

			}

			// for (Tag ftype : filterTagTypeList) {
			// System.out.println(ftype.name + " " + ftype.bindCount);
			// }

		}

		// add filterTagTypeList to all attributes
		attributes.addAll(filterTagTypeList);

		// get mit showFiles verknüpfte Tags & Häufigkeit
		this.availableTags = getTagcountAndTags(showFiles);

		// for (Tag tag : availableTags) {
		// System.out.println(tag.name + " " + tag.bindCount);
		// }

		physics.particles.clear();

		int count = this.availableTags.size();
		float dist;
		if (count > 1) {
			dist = ((float) height) / (count - 1);
		} else {
			dist = 0;
		}

		// dropParticles
		for (int i = 0; i < this.availableTags.size(); i++) {
			dropParticle(physics, i * dist - (dist * (count - 1) / 2), -mainscreen.height / 4, 20,
					this.availableTags.get(i), true);
		}

		// draw alle Tags
		// physics.particles.clear();
		//
		// int count = attributes.size();
		// float dist;
		// if (count > 1) {
		// dist = ((float) height) / (count - 1);
		// } else {
		// dist = 0;
		// }
		//
		// // dropParticles
		// for (int i = 0; i < attributes.size(); i++) {
		// dropParticle(physics, i * dist - (dist * (count - 1) / 2), 0, 20,
		// attributes.get(i), true);
		// }
	}

	public void updateSprings() {

		physics.springs.clear();

		for (Tag tf : showFiles) {
			Tag_File file = (Tag_File) tf;

			// File -> Tag Springs:
			if (enableTagBinding && file.attributeBindings.size() > 0) {
				for (Tag t : file.attributeBindings) {
					dropSpring(physics, file, t, ConnectionType.TAGBINDING);
				}
			}

			// File -> File Springs:
			if (enableFileBinding && file.fileBindings.size() > 0) {
				for (Tag t : file.fileBindings) {
					// wenn file in showFiles ist!
					if (showFiles.contains(t)) {
						dropSpring(physics, file, t, ConnectionType.FILEBINDING);
					}
				}
			}

			// File -> File Springs:
			if (enableVersionBinding && file.versionBindings.size() > 0) {
				for (Tag t : file.versionBindings) {
					dropSpring(physics, file, t, ConnectionType.VERSION);
				}
			}
		}
	}

	private void setParticlesPosition2D(VerletPhysics physics, ArrayList<Tag> _files) {
		physics.particles.clear();

		// int count = getSizeWithoutVersion(files);

		// int dist = 120; // stattdessen contentBorders

		// bei maxRows yStarOben und yStartUntern noch Programm höhe abziehen

		int perRow = (int) ((mainscreen.width - 2 * contentBorders) / contentBorders);
		int maxRows = (int) ((mainscreen.height - menuPlane.h) / contentBorders);

		int max1Seite = perRow * maxRows;

		int xShiftCount = 0;

		int yShiftCount = 0;

		// int yStartOben = mainscreen.height / 2 - (int) menuPlane.h - dist /
		// 2;
		// int yStartUnten = mainscreen.height / 2 - dist / 2;

		// sortiere nach letztem genutzten Zeitpunkt nur neuesten
		// FilesVersionen! - es könne älter Versionen neuere Accesses haben!
		ArrayList<Tag> sortedFiles = getFilesSortedLastAccess(_files);
		println("setParticlesPosition2D(): files.size:" + _files.size());
		println("setParticlesPosition2D(): sortedFiles.size:" + sortedFiles.size());

		// fülle von unten rechts beginnend mit wenigst interessantem
		if (getSizeWithoutVersion(_files) < max1Seite) {

			for (int i = sortedFiles.size() - 1; i >= 0; i--) {
				// println("sortedFiles name: " + sortedFiles.get(i).name);
				// positoniere alle neuesten Dateiversionen
				if (!setZTimeAxis) {
					dropParticle(physics, (contentBorders * (perRow - 1) / 2) - xShiftCount * contentBorders,
							contentStartUnten - yShiftCount * contentBorders, sortedFiles.get(i), true); // links/rechts
				} else {
					// setzte auf yStartUnten
					dropParticle(physics, (contentBorders * (perRow - 1) / 2) - xShiftCount * contentBorders,
							contentStartUnten, sortedFiles.get(i), true); // links/rechts
				}
				xShiftCount++;
				if (xShiftCount >= perRow) {
					yShiftCount++;
					xShiftCount = 0;

				}
			}

			// wenn showVersions alle Versionen an neuester Datei ansetzen
			if (showVersions) {

				for (int i = 0; i < _files.size(); i++) {

					// wenn file nicht in sortedFiles ist:
					if (!inSortedFiles(_files.get(i).id, sortedFiles)) {

						Tag_File position;

						// origin

						if (((Tag_File) _files.get(i)).origin_ID == 0) {
							// suche file mit dessen Id zur positionierung
							position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).id, sortedFiles);

						} // andere Verson
						else {
							position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).origin_ID, sortedFiles);

						}
						dropParticle(physics, position.x, position.y, _files.get(i), true);
					}
				}
			}

		}
		// fülle von oben voll
		else {
			// println("sortedFiles.size:" + sortedFiles.size());

			// neuest Versionen
			for (int i = 0; i < sortedFiles.size(); i++) {

				// println("sortedFiles name: " + sortedFiles.get(i).name);

				if (!setZTimeAxis) {
					// jede Datei aus sortedFiles muss positoniert werden
					dropParticle(physics, xShiftCount * contentBorders - (contentBorders * (perRow - 1) / 2),
							yShiftCount * contentBorders - contentStartOben, sortedFiles.get(i), true); // links/rechts
				} else {
					// setzte auf yStartUnten
					dropParticle(physics, xShiftCount * contentBorders - (contentBorders * (perRow - 1) / 2),
							contentStartUnten, sortedFiles.get(i), true); // links/rechts
				}
				xShiftCount++;
				if (xShiftCount >= perRow) {
					yShiftCount++;
					xShiftCount = 0;
				}
			}

			// wenn showVersions alle Versionen an neuester Datei ansetzen
			if (showVersions) {

				for (int i = 0; i < _files.size(); i++) {
					if (!inSortedFiles(_files.get(i).id, sortedFiles)) {
						Tag_File position;

						// origin
						if (((Tag_File) _files.get(i)).origin_ID == 0) {
							// suche file mit dessen Id zur positionierung
							position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).id, sortedFiles);

						} // andere Verson
						else {
							position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).origin_ID, sortedFiles);

						}
						dropParticle(physics, position.x, position.y, _files.get(i), true);
					}
				}
			}
		}
	}

	private void setParticlesPositionTreeMap(VerletPhysics physics, ArrayList<Tag> _files) {
		System.out.println("setParticlesPositionTreeMap()");
		
		physics.particles.clear();

		for (Tag tag : _files) {
			Tag_File file = (Tag_File) tag;

			MapItem mItem = getMapItemById(file.id);
			// versionen werden aussortiert!
			if (mItem != null) {
				// println(mItem.x + mItem.w/2 + " " + (mItem.y + mItem.h/2));
				file.treemapH = (int) mItem.h;
				file.treemapW = (int) mItem.w;
				dropParticle(physics, mItem.x + mItem.w / 2 - mainscreen.width / 2, mItem.y + mItem.h / 2
						- mainscreen.height / 2, file, true);
			}

		}

	}

	private void setParticlesPosition1D(VerletPhysics physics, ArrayList<Tag> _files) {
		System.out.println("setParticlesPosition() 1D");
		// drop Particles
		// set Position
		physics.particles.clear();

//		int count = 0;
//		if (showVersions) {
//			count = getSizeWithoutVersion(_files);
//		} else {
//			count = _files.size();
//		}
		// println("count: " + count);

		// float dist;
		// if (count > 1) {
		// // dist = ((float) height - 40) / (count - 1);
		// // dist = ((float) width - 40) / (count - 1);
		// dist = 120;
		// } else {
		// dist = 0;
		// }

		int shiftCount = 0;
		// int yStartUnten = (int) (mainscreen.height / 2 - contentBorders / 2);

		ArrayList<Tag> sortedFiles = getFilesSortedLastAccess(_files);
//		println("	setParticlesPosition1D(): _files.size:" + _files.size());
//		println("	setParticlesPosition1D(): sortedFiles.size:" + sortedFiles.size());

		// neuest Versionen
		for (int i = 0; i < sortedFiles.size(); i++) {
			// jede Datei aus sortedFiles muss positoniert werden

			dropParticle(physics, -mainscreen.width / 2 + contentBorders + shiftCount * contentBorders,
					contentStartUnten, sortedFiles.get(i), true);

			// dropParticle(physics, shiftCount * dist - ((dist * (count - 1)) /
			// 2.0f), yStartUnten, sortedFiles.get(i), true); // links/rechts

			shiftCount++;
		}

		// wenn showVersions alle Versionen an neuester Datei ansetzen
		if (showVersions) {

			for (int i = 0; i < _files.size(); i++) {
				if (!inSortedFiles(_files.get(i).id, sortedFiles)) {
					Tag_File position;

					// origin
					if (((Tag_File) _files.get(i)).origin_ID == 0) {
						// suche file mit dessen Id zur positionierung
						position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).id, sortedFiles);

					} // andere Verson
					else {
						position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).origin_ID, sortedFiles);

					}
					dropParticle(physics, position.x, position.y, _files.get(i), true);
				}
			}
		}
	}

	private void setParticlesPosition1D_(VerletPhysics physics, ArrayList<Tag> _files) {
		// drop Particles
		// set Position
		physics.particles.clear();

		int count = 0;
		if (showVersions) {
			count = getSizeWithoutVersion(_files);
		} else {
			count = _files.size();
		}
		// println("count: " + count);

		// float dist;
		// if (count > 1) {
		// // dist = ((float) height - 40) / (count - 1);
		// // dist = ((float) width - 40) / (count - 1);
		// dist = 120;
		// } else {
		// dist = 0;
		// }

		int shiftCount = 0;
		// int yStartUnten = (int) (mainscreen.height / 2 - contentBorders / 2);

		ArrayList<Tag> sortedFiles = getFilesSortedLastAccess(_files);
		println("setParticlesPosition1D(): _files.size:" + _files.size());
		println("setParticlesPosition1D(): sortedFiles.size:" + sortedFiles.size());

		// neuest Versionen
		for (int i = 0; i < sortedFiles.size(); i++) {
			// jede Datei aus sortedFiles muss positoniert werden
			dropParticle(physics, shiftCount * contentBorders - ((contentBorders * (count - 1)) / 2.0f),
					contentStartUnten, sortedFiles.get(i), true); // links/rechts

			shiftCount++;
		}

		// wenn showVersions alle Versionen an neuester Datei ansetzen
		if (showVersions) {

			for (int i = 0; i < _files.size(); i++) {
				if (!inSortedFiles(_files.get(i).id, sortedFiles)) {
					Tag_File position;

					// origin
					if (((Tag_File) _files.get(i)).origin_ID == 0) {
						// suche file mit dessen Id zur positionierung
						position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).id, sortedFiles);

					} // andere Verson
					else {
						position = (Tag_File) getTagByIDandType(((Tag_File) _files.get(i)).origin_ID, sortedFiles);

					}
					dropParticle(physics, position.x, position.y, _files.get(i), true);
				}
			}
		}
	}

	public void setShape() {
		// nachdem positionen festgelegt sind

		if (drawAccessShapes) {
			for (Tag t : files) {
				Tag_File file = (Tag_File) t;
				file.setShape(generateAccessShape(file));
			}
			System.out.println("file.setShape(generateShape(file)) -> VersionShapee");
		} else if (draw2DShape) {// || shape.getFamily() == PConstants.GROUP){

			for (Tag t : files) {
				Tag_File file = (Tag_File) t;
				file.setShape(generate2DShape(file));
			}
			System.out.println("file.setShape(generateShape(file)) : Created Tag Planes");
		} else if (drawTreemap) {// || shape.getFamily() == PConstants.GROUP){

			for (Tag t : files) {
				Tag_File file = (Tag_File) t;
				file.setShape(generateTreemapShape(file));
			}
			System.out.println("file.setShape(generateTreemapShape(file)) : Created Tag Planes");
		} else {
			for (Tag t : files) {
				Tag_File file = (Tag_File) t;
				file.shape = null;
			}
		}
	}

	public void setShape(Tag_File file) {
		// nachdem positionen festgelegt sind
		if (drawAccessShapes) {
			System.out.println("file.setShape(generateShape(file)) -> VersionShapee");
			file.setShape(generateAccessShape(file));

		} else if (draw2DShape) {// || shape.getFamily() == PConstants.GROUP){
			System.out.println("file.setShape(generateShape(file)) : Created Tag Plane");
			file.setShape(generate2DShape(file));

		} else if (drawTreemap) {
			System.out.println("file.setShape(generateTreemapShape(file)) : Created Tag Plane");
			file.setShape(generateTreemapShape(file));

		} else {
			file.shape = null;
		}
	}

	// ohne z
	public void dropParticle(VerletPhysics physics, float x, float y, Tag t) {
		t.x = x;
		t.y = y;

		// t.lock();

		physics.addParticle(t);
	}

	// ohne z
	public void dropParticle(VerletPhysics physics, float x, float y, Tag t, boolean lock) {
		t.x = x;
		t.y = y;

		if (lock) {
			t.lock();
		} else {
			t.unlock();
		}

		physics.addParticle(t);
	}

	public void dropParticle(VerletPhysics physics, float x, float y, float z, Tag t) {
		t.x = x;
		t.y = y;
		t.z = z;

		// t.lock();

		physics.addParticle(t);
	}

	public void dropParticle(VerletPhysics physics, float x, float y, float z, Tag t, boolean lock) {
		t.x = x;
		t.y = y;
		t.z = z;

		if (lock) {
			t.lock();
		}

		physics.addParticle(t);
	}

	float LEN = 400; // 10
	float STR = 0.01f; // 0.01f

	public void dropSpring(VerletPhysics physics, VerletParticle fileParticle, VerletParticle attributeParticle,
			Connection.ConnectionType type) {
		Connection sp = new Connection(fileParticle, attributeParticle, LEN, STR, type);
		physics.addSpring(sp);
	}

	public void createTreeMap(ArrayList<Tag> mapFiles) {

		// Treemap
		mapData = new FileMap(this);

		for (Tag tag : mapFiles) {
			Tag_File file = (Tag_File) tag;
			int count = file.attributeBindings.size() + file.versionBindings.size() + file.fileBindings.size()
					+ file.getAccesses().size();
			mapData.addItem(file.id, (int) sq(count));
		}

		// ------ treemap data is ready ------
		mapData.finishAdd();

		map = new Treemap(mapData, 120, height / 5, width - 240, height - 1 * height / 5 - 60);

		// map = new Treemap(mapData, 120 - mainscreen.width/2, height / 5 -
		// mainscreen.height/2, width - 240 - mainscreen.width/2, height - 2 *
		// height / 5 - mainscreen.height/2);
	}

	void setZNewestTime() {
		for (Tag t : showFiles) {
			Tag_File file = (Tag_File) t;

			// println("aktuelle anzeige file:" + file.name);
			file.z = (float) -timeline.mapExp(getNewestDate(file));

			// shape2D
			if (draw2DShape) {
				// setzt shape z-wert
				if (file.shape != null) {
					file.shape.resetMatrix();
					file.shape.translate(file.x, file.y, file.z);
				}
			}

			if (drawTreemap) {
				if (file.shape != null) {
					// file.shape.resetMatrix();
					file.shape.translate(0, 0, file.z);
				}
			}
		}
	}

	void resetZshowFiles() {
		for (Tag t : showFiles) {
			// setzt shape wieder auf z = 0;
			if (draw2DShape) {
				if (((Tag_File) t).shape != null) {
					((Tag_File) t).shape.resetMatrix();
					((Tag_File) t).shape.translate(t.x, t.y, 0);
				}
			}
			if (drawTreemap) {
				((Tag_File) t).shape.translate(0, 0, -t.z);
			}
			t.z = 0;
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
				// println(tableName + " " + _tag.id + " " + _tag.type);
				if (_tag.id == id && _tag.type.equals(tableName)) {
					tag = _tag;
				}
			}
		}
		// debug
		// if(tag == null){
		// println("getTagByID() attributes.size(): " + attributes.size());
		// println("getTagByID() tag ist null!");
		// }

		return tag;
	}

	public boolean inSortedFiles(int id, ArrayList<Tag> sortedFiles) {
		for (Tag tag : sortedFiles) {
			if (tag.id == id) {
				return true;
			}
		}
		return false;
	}

	public Tag getTagByIDandType(int originId, ArrayList<Tag> sortedFiles) {
		Tag tag = null;

		for (Tag _tag : sortedFiles) {
			Tag_File file = (Tag_File) _tag;
			if (file.origin_ID == originId || file.id == originId) {
				tag = _tag;
			}
		}

		return tag;
	}

	public Tag getTagFileByID(ArrayList<Tag> files, int id) {
		Tag tag = null;
		for (Tag _tag : files) {
			if (_tag.id == id) {
				tag = _tag;
			}
		}
		return tag;
	}

	public Tag getTagByName(String name) {
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
		ArrayList<Tag> results = new ArrayList<Tag>();

		for (Tag t : files) {
			Tag_File file = (Tag_File) t;
			if (file.id == originId || file.origin_ID == originId || file.parent_ID == originId) {
				results.add(file);
				// println(file.name + " " + file.id);
			}
		}

		return results;
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

	private ArrayList<Tag> getNewestTagFileVersions(ArrayList<Tag> files) {
		ArrayList<Tag> newestVersions = new ArrayList<Tag>();
		ArrayList<Tag> allFiles = (ArrayList<Tag>) files.clone();

		TreeSet<Integer> removeIDs = new TreeSet<Integer>();

		for (Tag t : allFiles) {
			Tag_File file = (Tag_File) t;

			// parents werden als alt markiert
			if (file.parent_ID != 0) {
				removeIDs.add(file.parent_ID);
			}
		}

		for (Tag file : files) {
			if (!removeIDs.contains(file.id)) {
				newestVersions.add(file);
			}

		}
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

	public ArrayList<Tag> getFilesSortedLastAccess(ArrayList<Tag> files) {

		ArrayList<Tag> sortedFiles = new ArrayList<Tag>();

		// nur die neuesten FileVersions // es können neuere Accesses bei alten
		// versionen bestehen!
		sortedFiles = getNewestTagFileVersions(files);

		Collections.sort(sortedFiles, comp_time);

		return sortedFiles;
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

	void updateAppTagBindings(Tag_App app) {
		app.setAttributeBindings(SQL.getBindedTagList(app));
	}

	public PGraphics generateTexture(Tag_File file, int texturW, int texturH) {

		// int texW = w*4; // 4fache Auflösung
		// int texH = h*4; // 4fache Auflösung

		PGraphics textu = createGraphics(texturW * 4, texturH * 4, P2D);
		textu.beginDraw();

		int cTint = color(255, 255, 255);

		// background macht Ränder von Textur!
		textu.background(cTint, 0);

		textu.imageMode(PConstants.CENTER);

		// Hintergrund Textur
		textu.image(texture_RECT, textu.width / 2, textu.height / 2, textu.width, textu.height);

		// Icon

		if (textu.width <= textu.height) {
			int smaller = (int) (textu.width * 0.15f);
			textu.image(getFileTypeImage(file.fileType), textu.width / 2, textu.height / 2 - smaller * 2 / 3,
					textu.width - smaller * 2, textu.width - smaller * 2);
		} else {
			int smaller = (int) (textu.height * 0.15f);
			textu.image(getFileTypeImage(file.fileType), textu.width / 2, textu.height / 2 - smaller * 2 / 3,
					textu.height - smaller * 2, textu.height - smaller * 2);
		}

		// mindestgröße, damit text gezeigt wird
		if (textu.width >= 400 && textu.height >= 400) {
			textu.fill(cFont);
			textu.rectMode(PConstants.CENTER);
			textu.textAlign(PConstants.CENTER, PConstants.CENTER);

			if (textu.width <= textu.height) {
				textu.textFont(font, (int) (textu.width * 0.15f));
				// text länger als einzeilig
				if (textWidth(file.name) * 4 > textu.width - textu.width * 0.2f) {
					// verkleinere text
					textu.textFont(font, (int) (textu.width * 0.12f));
					textu.text(file.name, textu.width / 2, textu.height - (int) (textu.width * 0.2f), textu.width
							- (textu.width * 0.2f), (textu.height * 0.4f));
				}
				// einzeilig
				else {
					textu.text(file.name, textu.width / 2, textu.height - (int) (textu.height * 0.2f));
				}
			}
			// breiter als hoch
			else {

				textu.textFont(font, (int) (textu.height * 0.15f));

				// text länger als einzeilig
				if (textWidth(file.name) > textu.width - textu.width * 0.2f) {
					// verkleinere text
					textu.textFont(font, (int) (textu.width * 0.12f));
					textu.text(file.name, textu.width / 2, textu.height - (int) (textu.width * 0.2f), textu.width
							- (textu.width * 0.2f), (textu.height * 0.4f));
				}
				// einzeilig
				else {
					textu.text(file.name, textu.width / 2, textu.height - (int) (textu.height * 0.1f));
				}
			}
		}

		// draw Arcs
		// textu.fill(255, 0, 0);
		// textu.noStroke();
		//
		// if (file.attributeBindings.size() > 0) {
		// int parts = file.attributeBindings.size();
		//
		// // println("arc drawn?");
		// for (int i = 0; i < file.attributeBindings.size(); i++) {
		// arc(textu, textu.width / 2, textu.height / 2,
		// (360 - (1.0f / (parts * 4)) * 360) + i * (360.0f / parts), (360 +
		// (1.0f / (parts * 4)) * 360)
		// + i * (360.0f / parts), 30, 5);
		// }
		// }

		textu.endDraw();

		return textu;

	}

	public PGraphics generateTexture(Tag_File file) {

		return generateTexture(file, 100, 100);
	}

	public PImage getFileTypeImage(FileType fileType) {
		// Icon
		PImage icon;
		switch (fileType) {
		case IMAGE:
			// textu.image(texture_IMAGE, 0, 0);
			icon = texture_IMAGE;
			break;
		case MESSAGE:
			icon = texture_MESSAGE;
			break;
		case TEXT:
			icon = texture_TEXT;
			break;
		case WEB:
			icon = texture_WEB;
			break;
		case FONT:
			icon = texture_FONT;
			break;
		case VIDEO:
			icon = texture_VIDEO;
			break;
		case AUDIO:
			icon = texture_AUDIO;
			break;
		case LAYOUT:
			icon = texture_LAYOUT;
			break;
		case VECTOR:
			icon = texture_VECTOR;
			break;
		default:
			icon = texture_TEXT;
			break;
		}
		return icon;
	}

	// Draw solid arc
	float sinLUT[];
	float cosLUT[];
	float SINCOS_PRECISION = 1.0f;
	int SINCOS_LENGTH = (int) ((360.0f / SINCOS_PRECISION));

	// public void arc(float x, float y, float deg, float rad, float w) {
	// int a = (int) (deg / SINCOS_PRECISION);
	// beginShape(QUAD_STRIP);
	// for (int i = 0; i < a; i++) {
	// vertex(cosLUT[i % SINCOS_LENGTH] * (rad) + x, sinLUT[i % SINCOS_LENGTH] *
	// (rad) + y);
	// vertex(cosLUT[i % SINCOS_LENGTH] * (rad + w) + x, sinLUT[i %
	// SINCOS_LENGTH] * (rad + w) + y);
	// }
	// endShape();
	// }

	public void arc(PGraphics textu, float x, float y, float startDeg, float endDeg, float rad, float w) {
		int a = (int) (startDeg / SINCOS_PRECISION);
		int b = (int) (endDeg / SINCOS_PRECISION);
		textu.beginShape(QUAD_STRIP);

		for (int i = a; i < b; i++) {
			textu.vertex(sinLUT[i % SINCOS_LENGTH] * (rad) + x, -cosLUT[i % SINCOS_LENGTH] * (rad) + y);
			textu.vertex(sinLUT[i % SINCOS_LENGTH] * (rad + w) + x, -cosLUT[i % SINCOS_LENGTH] * (rad + w) + y);
		}
		textu.endShape();
	}

	// public PShape gernerateShapeTRIANGLES(Tag_File file){
	// PShape shape = createShape(GROUP);
	//
	// return shape;
	// }

	public Timestamp createTimestamp(int year, int month, int day) {

		String dateString = nf(day, 2) + "/" + nf(month, 2) + "/" + nf(year, 4);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		try {
			date = dateFormat.parse(dateString);
			long time = date.getTime();
			return new Timestamp(time);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Timestamp createTimestamp(int year, int month, int day, int hour) {

		String dateString = nf(day, 2) + "/" + nf(month, 2) + "/" + nf(year, 4) + "/" + nf(hour, 2);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/HH");
		Date date;
		try {
			date = dateFormat.parse(dateString);
			long time = date.getTime();
			return new Timestamp(time);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	

	public PShape generateScala() {
		PShape scala = createShape(LINES);

		scala.stroke(0);
		scala.strokeWeight(3);

		int laenge = 50;
//		int lineDist = 50;

		// Calendar rightNow = Calendar.getInstance();

		long startTimestamp = System.currentTimeMillis();

		// Calender Instance
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTimestamp);

//		int startH = cal.get(Calendar.HOUR_OF_DAY);
//		int startD = cal.get(Calendar.DAY_OF_MONTH);
//		int startM = cal.get(Calendar.MONTH) + 1;
//		int startY = cal.get(Calendar.YEAR);

		// Timestamp heuteStunde = createTimestamp(cal.get(Calendar.YEAR),
		// cal.get(Calendar.MONTH) + 1,
		// cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY));
		// Timestamp heuteTag = createTimestamp(cal.get(Calendar.YEAR),
		// cal.get(Calendar.MONTH) + 1,
		// cal.get(Calendar.DAY_OF_MONTH));

		// speicher Text und z position in Array

//		for (int i = 0; i < 6; i++) {
		for (int i = 0; i <= cal.get(Calendar.HOUR_OF_DAY); i++) {

			int startH = cal.get(Calendar.HOUR_OF_DAY) - i;
			int startD = cal.get(Calendar.DAY_OF_MONTH);
			int startM = cal.get(Calendar.MONTH) + 1;
			int startY = cal.get(Calendar.YEAR);
			
			if (startH <= 0) {
				startH = 24 + cal.get(Calendar.HOUR_OF_DAY) - i;
				startD = cal.get(Calendar.DAY_OF_MONTH) - 1;
				if (startD <= 0) {
					startD = 31;
					startM = cal.get(Calendar.MONTH) + 1 - 1;
					if (startM <= 0) {
						startM = 12;
						startY = cal.get(Calendar.YEAR) - 1;
					}
				}
			}

			Timestamp stunde = createTimestamp(startY, startM, startD, startH);

			float zVal = -timeline.mapExp(stunde);

			scala.vertex(-mainscreen.width / 2, mainscreen.height / 2, zVal);
			scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 - laenge, zVal);
			
			scalaWerte.add(new ScalaWert("" + startD + ". " + startM + ". " + startY + "  " + nf(startH,2) + ":00", zVal));
		}

		for (int i = 1; i < 14; i++) {

			int startD = cal.get(Calendar.DAY_OF_MONTH) - i;
			int startM = cal.get(Calendar.MONTH) + 1;
			int startY = cal.get(Calendar.YEAR);
			
			if (startD <= 0) {
				startD = 31 + cal.get(Calendar.DAY_OF_MONTH) - i;
				startM = cal.get(Calendar.MONTH) + 1 - 1;
				if (startM <= 0) {
					startM = 12;
					startY = cal.get(Calendar.YEAR) - 1;
				}
			}

			Timestamp tag = createTimestamp(startY, startM, startD);
			
			float zVal = -timeline.mapExp(tag);
			scala.vertex(-mainscreen.width / 2, mainscreen.height / 2, zVal);
			scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 - laenge, zVal);
			
			scalaWerte.add(new ScalaWert("" + startD + ". " + startM + ". " + startY, zVal));
		}

		for (int i = 1; i < 12; i++) {

			int startD = cal.get(Calendar.DAY_OF_MONTH);
			int startM = cal.get(Calendar.MONTH) + 1 - i;
			int startY = cal.get(Calendar.YEAR);

			if (startM <= 0) {
				startM = 12 + cal.get(Calendar.MONTH) + 1 - i;
				startY = cal.get(Calendar.YEAR) - 1;
			}

			Timestamp monat = createTimestamp(startY, startM, 1);

			float zVal = -timeline.mapExp(monat);

			scala.vertex(-mainscreen.width / 2, mainscreen.height / 2, zVal);
			scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 - laenge, zVal);
			
			scalaWerte.add(new ScalaWert("" + startD + ". " + startM + ". " + startY, zVal));
		}
		
		for (int i = 1; i < 10; i++) {
			int startD = cal.get(Calendar.DAY_OF_MONTH);
			int startM = cal.get(Calendar.MONTH) + 1;
			int startY = cal.get(Calendar.YEAR) - i;

			Timestamp year = createTimestamp(startY, 1, 1);

			float zVal = -timeline.mapExp(year);

			scala.vertex(-mainscreen.width / 2, mainscreen.height / 2, zVal);
			scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 - laenge, zVal);
			
			scalaWerte.add(new ScalaWert("" + startY, zVal)); // + ". " + startM + ". " + startY
		}

		// println("heuteStunde: " + heuteStunde.getTime());
		// println("heuteTag: " + heuteTag.getTime());
		//
		// println("tag  " + timeline.mapExp(heuteStunde));
		//
		// println("heute" + timeline.mapExp(heuteTag));
		//
		// scala.vertex(-mainscreen.width / 2, mainscreen.height / 2,
		// -timeline.mapExp(heuteStunde));
		// scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 -
		// laenge, timeline.mapExp(heuteStunde));
		//
		// scala.vertex(-mainscreen.width / 2, mainscreen.height / 2,
		// -timeline.mapExp(heuteTag));
		// scala.vertex(-mainscreen.width / 2 - laenge, mainscreen.height / 2 -
		// laenge, timeline.mapExp(heuteTag));

		println(cal.get(Calendar.YEAR));
		println(cal.get(Calendar.MONTH));
		println(cal.get(Calendar.DAY_OF_MONTH));
		println("jetzt: " + cal.getTimeInMillis());

		// for (int i = 0; i < 200; i++) {
		// scala.vertex(-mainscreen.width/2, mainscreen.height/2, -i *
		// lineDist);
		// scala.vertex(-mainscreen.width/2 - laenge, mainscreen.height/2 -
		// laenge, -i * lineDist);
		// }

		scala.end();

		return scala;
	}

	public PShape generateBackgroundLines() {
		PShape backgroundLines = createShape(QUADS);

		backgroundLines.fill(0, 130);
		backgroundLines.noStroke();

		int lineDist = 120;
		float lineW = 3f;

		for (int i = -30; i < 200; i++) {

			backgroundLines.vertex(-mainscreen.width / 2 - lineW + i * lineDist, mainscreen.height / 2 + 1, 800);
			backgroundLines.vertex(-mainscreen.width / 2 - lineW + i * lineDist, mainscreen.height / 2 + 1, -40000);
			backgroundLines.vertex(-mainscreen.width / 2 + lineW + i * lineDist, mainscreen.height / 2 + 1, -40000);
			backgroundLines.vertex(-mainscreen.width / 2 + lineW + i * lineDist, mainscreen.height / 2 + 1, 800);

			// s.vertex(-50, -50, 0, 0, 0);
			// s.vertex(+50, -50, 0, file.textur.width, 0);
			// s.vertex(+50, +50, 0, file.textur.width, file.textur.height);
			// s.vertex(-50, +50, 0, 0, file.textur.height);
		}

		backgroundLines.end();

		return backgroundLines;
	}

	public PShape generate2DShape(Tag_File file) {
		PShape s = createShape();
		s.noStroke();

		if (file.textur == null) {
			// wenn shape aus anderem Thread WatchDir erzeugt wird -> Java
			// Exeption!
			return null;
		}
		// PGraphics tex = generateTexture((Tag_File) file);
		s.texture(file.textur);

		// s.vertex(- 50, + 50, 0, tex.width, 0);
		// s.vertex( + 50, + 50, 0, 0, 0);
		// s.vertex( + 50, - 50, 0, 0, tex.height);
		// s.vertex( - 50, - 50, 0, tex.width, tex.height);

		s.vertex(-50, -50, 0, 0, 0);
		s.vertex(+50, -50, 0, file.textur.width, 0);
		s.vertex(+50, +50, 0, file.textur.width, file.textur.height);
		s.vertex(-50, +50, 0, 0, file.textur.height);

		s.end(CLOSE);
		return s;
	}

	public PShape generateTreemapShape(Tag_File file) {

		// im nächsten draw() werden Treemap textures erstellt

		if (file.textur == null) {
			// wenn shape aus anderem Thread WatchDir erzeugt wird -> Java
			// Exeption!
			return null;
		}
		// PGraphics tex = generateTexture((Tag_File) file);
		// file.textur = null;
		// file.setTextur(generateTexture(file, file.treemapW, file.treemapH));

		// erzeuge Textur in Form des Treemmap Items

		PShape s = createShape();
		s.noStroke();

		s.texture(file.textur);

		int abstand = 3;

		// get information von File
		// MapItem mItem = getMapItemById(file.id);
		if (file.treemapH != 0) {
			s.vertex(file.x - file.treemapW / 2 + abstand, file.y - file.treemapH / 2 + abstand, 0, 0, 0);
			s.vertex(file.x + file.treemapW / 2 - abstand, file.y - file.treemapH / 2 + abstand, 0, file.textur.width,
					0);
			s.vertex(file.x + file.treemapW / 2 - abstand, file.y + file.treemapH / 2 - abstand, 0, file.textur.width,
					file.textur.height);
			s.vertex(file.x - file.treemapW / 2 + abstand, file.y + file.treemapH / 2 - abstand, 0, 0,
					file.textur.height);

		}

		// get positon and information by treemapItem
		// MapItem mItem = getMapItemById(file.id);
		// if (mItem != null) {
		// s.vertex(mItem.x - mainscreen.width / 2 + abstand, mItem.y -
		// mainscreen.height / 2 + abstand, 0, 0, 0);
		// s.vertex(mItem.x + mItem.w - mainscreen.width / 2 - abstand, mItem.y
		// - mainscreen.height / 2 + abstand, 0,
		// file.textur.width, 0);
		// s.vertex(mItem.x + mItem.w - mainscreen.width / 2 - abstand, mItem.y
		// + mItem.h - mainscreen.height / 2
		// - abstand, 0, file.textur.width, file.textur.height);
		// s.vertex(mItem.x - mainscreen.width / 2 + abstand, mItem.y + mItem.h
		// - mainscreen.height / 2 - abstand, 0,
		// 0, file.textur.height);
		//
		// }

		s.end(CLOSE);
		return s;
	}

	public MapItem getMapItemById(int id) {

		for (MapItem i : mapData.mapItems) {
			if (i.id == id) {
				// println("getMapItemById(): " + id);
				return i;
			}
		}

		return null;
	}

	// Access shape
	public PShape generateAccessShape(Tag_File file) {
		PShape s = createShape(GROUP);
		// file.creation_time;

		if (file.textur == null) {
			// wenn shape aus anderem Thread WatchDir erzeugt wird -> Java
			// Exeption!
			println("textur ist null! : " + file.name);
			return null;
		}

		PShape start = makeRect(file, file.creation_time);
		s.addChild(start);

		PShape next = null;
		for (Access c : file.getAccesses()) {
			next = makeRect(file, c.date);
			s.addChild(next);
			s.addChild(generateConnectionQUADS(start, next));
			start = next;
		}

		if (file.delete_time != null) {
			next = makeRect(file, file.delete_time);
			s.addChild(next);
			println("generateShape(): deleted file: " + file.name);

			// PShape[] shapes = s.getChildren();
			s.addChild(generateConnectionQUADS(start, next));

		}
		return s;
	}

	public PShape makeRect(Tag file, Timestamp ts) {
		float z = (float) -timeline.mapExpMillis(System.currentTimeMillis() - ts.getTime());

		PShape s = createShape();
		s.noStroke();
		s.fill(0, 200, 100);

		// PGraphics tex = generateTexture((Tag_File) file);
		// s.texture(((Tag_File) file).textur);
		// s.texture(pg);
		// s.texture(env_plif);

		// debug
		// println("file.name: " + file.name);

		float rad = 20;
		s.vertex(file.x - rad, file.y - rad, z, 0, 0);
		s.vertex(file.x + rad, file.y - rad, z, ((Tag_File) file).textur.width, 0);
		s.vertex(file.x + rad, file.y + rad, z, ((Tag_File) file).textur.width, ((Tag_File) file).textur.height);
		s.vertex(file.x - rad, file.y + rad, z, 0, ((Tag_File) file).textur.height);

		s.end(CLOSE);

		// println("file.name after Rect: " + file.name);

		return s;
	}

	public PShape makeOcto(Tag file, Timestamp ts) {
		float z = (float) -timeline.mapExpMillis(System.currentTimeMillis() - ts.getTime());

		PShape s = createShape();
		s.noStroke();

		// PGraphics tex = generateTexture((Tag_File) file);
		// s.texture(((Tag_File) file).textur);
		s.texture(pg);
		// s.texture(env_plif);

		// debug
		println("file.name: " + file.name);

		float rad = 20;
		// s.vertex(file.x - rad, file.y - rad, z, 0, 0);
		// s.vertex(file.x + rad, file.y - rad, z, ((Tag_File)
		// file).textur.width, 0);
		// s.vertex(file.x + rad, file.y + rad, z, ((Tag_File)
		// file).textur.width, ((Tag_File) file).textur.height);
		// s.vertex(file.x - rad, file.y + rad, z, 0, ((Tag_File)
		// file).textur.height);

		s.vertex(file.x - rad, file.y - rad / 2, z, 0, ((Tag_File) file).textur.height / 4);
		s.vertex(file.x - rad / 2, file.y - rad, z, ((Tag_File) file).textur.width / 4, 0);
		s.vertex(file.x + rad / 2, file.y - rad, z, ((Tag_File) file).textur.width * 3 / 4, 0);
		s.vertex(file.x + rad, file.y - rad / 2, z, 0, ((Tag_File) file).textur.height / 4);

		s.vertex(file.x + rad, file.y + rad / 2, z, 0, ((Tag_File) file).textur.height * 3 / 4);
		s.vertex(file.x + rad / 2, file.y + rad, z, ((Tag_File) file).textur.width * 3 / 4, 1);
		s.vertex(file.x - rad / 2, file.y + rad, z, ((Tag_File) file).textur.width / 4, 1);
		s.vertex(file.x - rad, file.y + rad / 2, z, 0, ((Tag_File) file).textur.height * 3 / 4);

		s.end(CLOSE);

		println("file.name after Rect: " + file.name);

		return s;
	}

	public PShape generateConnectionTRIANGLES(PShape s1, PShape s2) {

		PShape s = createShape(TRIANGLES);

		// println("s1.getVertexCount(): " + s1.getVertexCount());
		s.fill(0, 255, 0);

		// Stroke für Connections wieder Ausblenden!
		s.stroke(0);

		for (int i = 0; i < s1.getVertexCount(); i++) {
			// int i = 0;

			if (i < s1.getVertexCount() - 1) {
				s.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i));
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i));
				s.vertex(s1.getVertexX(i + 1), s1.getVertexY(i + 1), s1.getVertexZ(i + 1));

				s.vertex(s1.getVertexX(i + 1), s1.getVertexY(i + 1), s1.getVertexZ(i + 1));
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i));
				s.vertex(s2.getVertexX(i + 1), s2.getVertexY(i + 1), s2.getVertexZ(i + 1));

			}
			// wieder schließen
			else {
				s.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i));
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i));
				s.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0));

				s.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0));
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i));
				s.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0));
			}
		}

		s.end(CLOSE);
		return s;
	}

	public PShape generateConnectionQUADS(PShape s1, PShape s2) {

		PShape s = createShape(QUADS);

		s.texture(texture_connection);

		// println("s1.getVertexCount(): " + s1.getVertexCount());
		s.fill(0, 255, 0);

		// Stroke für Connections wieder Ausblenden!
		s.noStroke();

		for (int i = 0; i < s1.getVertexCount(); i++) {
			// int i = 0;

			if (i < s1.getVertexCount() - 1) {
				// Vec3D vec1 = new Vec3D(s2.getVertexX(i), s2.getVertexY(i),
				// s2.getVertexZ(i)).sub(new Vec3D(s1
				// .getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i)));
				// Vec3D vec2 = new Vec3D(s2.getVertexX(i + 1), s2.getVertexY(i
				// + 1), s2.getVertexZ(i + 1)).sub(new Vec3D(
				// s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i)));
				// Vec3D normal = vec1.cross(vec2);
				// normal.normalize();
				// normal(normal.x, normal.y, normal.z);

				// s.vertex(s1.getVertexX(i), s1.getVertexY(i),
				// s1.getVertexZ(i));
				// s.vertex(s2.getVertexX(i), s2.getVertexY(i),
				// s2.getVertexZ(i));
				// s.vertex(s2.getVertexX(i + 1), s2.getVertexY(i + 1),
				// s2.getVertexZ(i + 1));
				// s.vertex(s1.getVertexX(i + 1), s1.getVertexY(i + 1),
				// s1.getVertexZ(i + 1));

				s.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i), 0, 0);
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i), texture_connection.width, 0);
				s.vertex(s2.getVertexX(i + 1), s2.getVertexY(i + 1), s2.getVertexZ(i + 1), texture_connection.width,
						texture_connection.height);
				s.vertex(s1.getVertexX(i + 1), s1.getVertexY(i + 1), s1.getVertexZ(i + 1), 0, texture_connection.height);
			}
			// wieder schließen
			else {

				// Vec3D vec1 = new Vec3D(s2.getVertexX(i), s2.getVertexY(i),
				// s2.getVertexZ(i)).sub(new Vec3D(s1
				// .getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i)));
				// Vec3D vec2 = new Vec3D(s2.getVertexX(0), s2.getVertexY(0),
				// s2.getVertexZ(0)).sub(new Vec3D(s2
				// .getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i)));
				// Vec3D normal = vec1.cross(vec2);
				// normal.normalize();
				// normal(normal.x, normal.y, normal.z);

				// s.vertex(s1.getVertexX(i), s1.getVertexY(i),
				// s1.getVertexZ(i));
				// s.vertex(s2.getVertexX(i), s2.getVertexY(i),
				// s2.getVertexZ(i));
				// s.vertex(s2.getVertexX(0), s2.getVertexY(0),
				// s2.getVertexZ(0));
				// s.vertex(s1.getVertexX(0), s1.getVertexY(0),
				// s1.getVertexZ(0));

				s.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i), 0, 0);
				s.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i), texture_connection.width, 0);
				s.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0), texture_connection.width,
						texture_connection.height);
				s.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0), 0, texture_connection.height);
			}
		}

		s.end(CLOSE);
		return s;
	}

	public PShape generateConnection(PShape s1, PShape s2) {

		PShape s = createShape(GROUP);

		for (int i = 0; i < s1.getVertexCount(); i++) {
			PShape r = createShape();
			r.texture(texture_connection);
			r.fill(0, 0, 255);
			r.noStroke();

			if (i < s1.getVertexCount() - 1) {
				r.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i), 0, 0);
				r.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i), texture_connection.width, 0);
				r.vertex(s2.getVertexX(i + 1), s2.getVertexY(i + 1), s2.getVertexZ(i + 1), texture_connection.width,
						texture_connection.height);
				r.vertex(s1.getVertexX(i + 1), s1.getVertexY(i + 1), s1.getVertexZ(i + 1), 0, texture_connection.height);
			}
			// wieder schließen
			else {
				r.vertex(s1.getVertexX(i), s1.getVertexY(i), s1.getVertexZ(i), 0, 0);
				r.vertex(s2.getVertexX(i), s2.getVertexY(i), s2.getVertexZ(i), texture_connection.width, 0);
				r.vertex(s2.getVertexX(0), s2.getVertexY(0), s2.getVertexZ(0), texture_connection.width,
						texture_connection.height);
				r.vertex(s1.getVertexX(0), s1.getVertexY(0), s1.getVertexZ(0), 0, texture_connection.height);
			}
			r.end(CLOSE);
			s.addChild(r);
		}
		return s;
	}

	// ///////// INPUT ///////////////////

	int lastMouseY;
	int lastMouseX;

	public void mouseDragged() {

		float mouseDraggedY = (lastMouseY - mouseY);
		lastMouseY = mouseY;

		float mouseDraggedX = (lastMouseX - mouseX);
		lastMouseX = mouseX;

		// nur wenn zeitachse aktiviert ist
		// if (setZTimeAxis) {
		// cam_eyetargetpos.z += mouseDraggedY/2;
		// }

		// CamMover
		if (setZTimeAxis && timeChooser.camMover.mouseOver()) {
			timeChooser.camMover.setY(mouseY);
		}

		if (!timeChooser.mouseOverScala() && !timeChooser.camMover.mouseOver()) {
			cam_eyetargetpos.y += mouseDraggedY * 10;

			if (cam_eyetargetpos.y > height / 2) {
				cam_eyetargetpos.y = height / 2;
			}
		}

		if (position1D && !timeChooser.mouseOverScala() && !timeChooser.camMover.mouseOver()) {
			cam_eyetargetpos.x += mouseDraggedX;

			if (cam_eyetargetpos.x < width / 2) {
				cam_eyetargetpos.x = width / 2;
			}
		}
	}

	public void mousePressed() {
		mouseActive = true;
		// mouseDragged
		lastMouseY = mouseY;
		lastMouseX = mouseX;

		// lastClick = new Timestamp(System.currentTimeMillis());

		System.out.println("void mousePressed auskommentiert Tag verbindung Interaktion!");
		// TagVerbindungen
		// for (Tag t : showFiles) {
		//
		// if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y +
		// mainscreen.height / 2, t.z, 30, 30)) {
		// startTag = t;
		// }
		// }
		// for (Tag t : attributes) {
		// if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y +
		// mainscreen.height / 2, t.z, 30, 30)) {
		// startTag = t;
		// }
		// }
	}

	Timestamp lastMinTime = new Timestamp(System.currentTimeMillis());

	public void mouseReleased() {
		if (minTime != null) {
			if (lastMinTime.getTime() != minTime.getTime()) {
				println("void mouseRelease(): neue minTime -> updateShowFiles()");
				lastMinTime = (Timestamp) minTime.clone();
				updateShowFiles();
				updateTags();
			}
		}

		lastClick = new Timestamp(System.currentTimeMillis());
		startClickNextFrame = true;

		
		// tagVerbindungen - auskommentiert in mousePressed
		if (startTag != null) {
			if (startTag instanceof Tag_File) {
				// File -> Attribute
				for (Tag t : attributes) {
					if (mouseOver(mainscreen, t.x + mainscreen.width / 2, t.y + mainscreen.height / 2, t.z, 30, 30)) {
						Tag_File file = (Tag_File) startTag;
						file.attributeBindings.add(t);
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
	}

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
		// case 'U':
		// // Set User
		// user = (Tag_User) SQL.queryTagList("users").get(1);
		// filterList.clear();
		// filterList.add(new Filter(user, true));
		// updateShowFiles();
		// updateTags();
		// updateSprings();
		// break;
		// case 'I':
		// // Set User
		// user = (Tag_User) SQL.queryTagList("users").get(2);
		// filterList.clear();
		// filterList.add(new Filter(user, true));
		// updateShowFiles();
		// updateTags();
		// updateSprings();
		// break;
		// case 'L':
		// createPromt("locations");
		// break;
		// case 'K':
		// createPromt("keywords");
		// break;
		// case 'P':
		// createPromt("projects");
		// break;
		// case 'T':
		// // Bind File - Tag
		// Tag_File file = (Tag_File) showFiles.get(0);
		//
		// // beispiel tag löschen!
		// Tag tag = new Tag_Location("locations", 5, "Ort", "coordinaten");
		// SQL.bindTag(file, tag);
		// updateTags();
		// updateSprings();
		// break;
		// case 'F':
		// filterList.clear();
		// System.out.println(attributes.get(0).name + " " +
		// attributes.get(0).type);
		// System.out.println(attributes.get(1).name + " " +
		// attributes.get(1).type);
		// filterList.add(new Filter(attributes.get(0), true));
		// filterList.add(new Filter(attributes.get(1), false));
		//
		// updateShowFiles();
		// updateTags();
		// updateSprings();
		// break;
		// case 'C':
		// // b = new GTextField(this, 0, 0, 100, 100);
		// // PromtNewFile p = new PromtNewFile(this);
		// // promts.add(p);
		// break;
		// case 'D':
		//
		// // if (promts.size() > 0) {
		// // println("dispose");
		// // PromtNewFile pro = promts.get(0);
		// // pro.dispose();
		// // pro = null;
		// //
		// //
		// // // pro = null;
		// // // pro.markForDisposal();
		// // // promts.remove(p);
		// // }
		// break;
		// case 'a':
		// cam_eye_target.x -= 150;
		// // println("cam_eye.x = " + cam_eye.x);
		// break;
		// case 'd':
		// cam_eye_target.x += 150;
		// // println("cam_eye.x = " + cam_eye.x);
		// break;
		// case 'w':
		// cam_eye_target.y -= 150;
		// // println("cam_eye.y = " + cam_eye.y);
		// break;
		// case 's':
		// cam_eye_target.y += 150;
		// // println("cam_eye.y = " + cam_eye.y);
		// break;
		// case 'y':
		// cam_eye_target.z -= 150;
		// // println("cam_eye.y = " + cam_eye.y);
		// break;
		// case 'x':
		// cam_eye_target.z += 150;
		// // println("cam_eye.y = " + cam_eye.y);
		// break;
		case 'T':
			println("Set minTime als Filter");
			Timestamp ts = new Timestamp(System.currentTimeMillis() - 14L * 24 * 60 * 60 * 1000);
			minTime = ts;
			// Tag_LastTime lt = new Tag_LastTime(this, ts);
			// Filter minTime = new Filter(lt, true);
			// filterList.add(minTime);
			updateShowFiles();
			break;
		case 'w':
			cam_eyetargetpos.z += 20;
			break;
		case 's':
			cam_eyetargetpos.z -= 20;
			break;
		case '2':

			cam_eyetargetpos.y -= 100;
			// cam_eyetargetpos = cam_eye2Dpos;
			break;
		case '3':
			cam_eyetargetpos.y += 100;
			if (cam_eyetargetpos.y > height / 2) {
				cam_eyetargetpos.y = height / 2;
			}
			// cam_eyetargetpos = cam_eye3Dpos;
			break;

		case '9':
			showButtons = !showButtons;
			break;
		// case '0':
		// createTreeMap(showFiles);
		// break;
		}

	}

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
			// updateTags();
			// updateSprings();
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

	// in HoverPlane neuen Tag erzeugen
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
		// if (!tagName.trim().equals("")) {
		// createChooserButtons("tagInput", tagName);
		// }
	}

	// Hoverplane Textinput Feld nicht mehr gebraucht
	public void tagInputHoverPlane(String tagName) {
		println("Textfield tagInputHoverPlane content: " + tagName);
		// if (!tagName.trim().equals("")) {
		// createChooserButtons("tagInputHoverPlane", tagName);
		// }
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

	// void calcBillboardRotation() {
	//
	// Vec3D cam = cam_eye.sub(cam_target);
	//
	// Vec3D cam_yz = new Vec3D(0, cam.y, cam.z).normalize();
	// Vec3D cam_xz = new Vec3D(cam.x, 0, cam.z).normalize();
	// Vec3D cam_xy = new Vec3D(cam.x, cam.y, 0).normalize();
	//
	// if (cam.y >= 0) {
	// xBillboardRotation = -cam_yz.angleBetween(new Vec3D(0, 0, 1));
	// } else {
	// xBillboardRotation = cam_yz.angleBetween(new Vec3D(0, 0, 1));
	// }
	// if (cam.x >= 0) {
	// yBillboardRotation = cam_xz.angleBetween(new Vec3D(0, 0, 1));
	// } else {
	// yBillboardRotation = -cam_xz.angleBetween(new Vec3D(0, 0, 1));
	// }
	//
	// // zBillboardRotation = cam_xy.angleBetween(new Vec3D(0, -1, 0));
	// // println(zBillboardRotation);
	//
	// // zBillboardRotation = 0;
	//
	// if (cam.z < 0) {
	// xBillboardRotation += PI;
	// yBillboardRotation *= -1;
	// }
	//
	// // println("x:rotation: " + xBillboardRotation);
	// // println("y:rotation: " + yBillboardRotation);
	// }

	void loadImages() {
		close = loadImage("../data/close.png");
		close_h = loadImage("../data/close_h.png");
		open = loadImage("../data/open.png");

		texture_RECT = loadImage("../data/fileRect.png");
		texture_IMAGE = loadImage("../data/bild.png");
		texture_MESSAGE = loadImage("../data/nachricht.png");
		texture_TEXT = loadImage("../data/dokument.png");
		texture_WEB = loadImage("../data/web.png");
		texture_FONT = loadImage("../data/font.png");
		texture_VIDEO = loadImage("../data/video.png");
		texture_AUDIO = loadImage("../data/musik.png");
		texture_LAYOUT = loadImage("../data/layout.png");
		texture_VECTOR = loadImage("../data/vektor.png");
		texture_CODE = loadImage("../data/code.png");

		texture_connection = loadImage("../data/texture_connection.png");
		mapImg = loadImage("../data/map.png");
		userImg = loadImage("../data/userProfil.png");
		controlImg = loadImage("../data/control.png");

		cameraImg = loadImage("../data/camera.png");

		newsButton = loadImage("../data/newsButton.png");
		newsButton_h = loadImage("../data/newsButton_h.png");

		schatten = loadImage("../data/schatten.png");

		appsButton = loadImage(VersionBuilder.versionsVerzeichnis + "applications/apps.png");
		backgroundApp = loadImage("../data/backgroundApp.png");

		backgroundTransition = loadImage("../data/backgroundTransition_linesTop.png");
		// backgroundTransition =
		// loadImage("../data/hintergrund_1600x1200.png");
		// backgroundTransition = loadImage("../data/background.png");

		// Label miniaturen Dropdown Menu & Filter
		minCall = loadImage("../data/call.png");
		minEvent = loadImage("../data/event.png");
		minFilter = loadImage("../data/filter.png");
		minKeyword = loadImage("../data/keyword.png");
		minLocation = loadImage("../data/location.png");
		minMessage = loadImage("../data/message.png");
		minUser = loadImage("../data/user.png");
		minDocument = loadImage("../data/typ_document.png");
		minProject = loadImage("../data/project.png");

		// Tag Miniaturen File Tags
		tagMinProject = loadImage("../data/tagProject.png");
		tagMinUser = loadImage("../data/tagUser.png");
		tagMinEvent = loadImage("../data/tagEvent.png");
		tagMinKeyword = loadImage("../data/tagKeyword.png");
		tagMinLocation = loadImage("../data/tagLocation.png");

		// new Tags
		newLocation = loadImage("../data/newLocation.png");
		newUser = loadImage("../data/newUser.png");
		newKeyword = loadImage("../data/newKeyword.png");
		newProject = loadImage("../data/newProject.png");
		newEvent = loadImage("../data/newEvent.png");

		// news Images
		newsCall = loadImage("../data/news_call.png");
		newsAppointment = loadImage("../data/news_appointment.png");
		newsFile = loadImage("../data/news_file.png");
		newsMessage = loadImage("../data/news_message.png");

	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorerprocessing2.TagExplorerProcessing2.class.getName() });
	}

	public void stop() {
		// Do whatever you want here.
		for (Filter f : filterList) {
			SQL.setFilterTime(f.tag, false);
		}
		super.stop();
	}

	
	public void showVersions(boolean onOff) {
		println("showVersions(): " + onOff);
		showVersions = onOff;
		if(showVersions && !setZTimeAxis){
			setZTimeAxis(true);
		}
		updateShowFiles();

	}

	public void drawAccessShapes(boolean onOff) {
		println("drawAccessShapes: " + onOff);
		drawAccessShapes = onOff;
		if (drawAccessShapes) {

			draw2DShape = false;
			
			if(drawTreemap){
				drawTreemap = false;
				position2D(true);
			}
			

			setShape();

			// fallse keine zTime Ordnung ist
			if (setZTimeAxis == false) {
				setZTimeAxis(true);
			}
		}

		// updateShowFiles();
	}

	public void draw2DShape(boolean onOff) {
		draw2DShape = onOff;
		if (draw2DShape) {

			drawAccessShapes = false;

			if(drawTreemap){
				drawTreemap = false;
				position2D(true);
			}
			
			updateShowFiles();
//			setShape();

//			setZTimeAxis(setZTimeAxis); // wird in updateShoFiles
		}
	}

	public void drawTreemap(boolean onOff) {
		drawTreemap = onOff;

		// erzeuge Treemap Texturen oder setze auf standartTexturen zurück
		// updateTextures = true;
		if (drawTreemap) {
			// cp5_Test.get(Toggle.class, "drawAccessShapes").setState(false);

			drawAccessShapes = false;
			draw2DShape = false;
			
			position2D = false;
			position1D = false;

			// setze Partikelpositon neu als Treemap
			updateShowFiles();

			// passiert in updateShowFiles()
//			setZTimeAxis(false);
		}
	}

	public void setZTimeAxis(boolean onOff) {

		println("setZTimeAxis: " + onOff);
		setZTimeAxis = onOff;

		if (onOff) {
			showTimeline = true;
			println("setZTimeAxis: " + setZTimeAxis + " setZNewestTime()!!!");
			setZNewestTime();
		} else {

			// camera auf nullpunkt
			println("setZTimeAxis(): reset Cameraposition");
			timeChooser.camMover.reset();
			cam_eyetargetpos = cam_eye2Dpos.copy();

			// drawAccessShape nur im ZTime Modus!
			if (drawAccessShapes) {
				draw2DShape(true);
				drawAccessShapes = false;
			}

			showTimeline = false;
			println("setZTimeAxis: " + setZTimeAxis + " resetZshowFiles()");

			resetZshowFiles();

		}
		updateSprings();
	}

	public void position1D(boolean onOff) {
		position1D = onOff;
		if (position1D) {
			position2D = false;
			drawTreemap = false;
		}
		updateShowFiles();
	}

	public void position2D(boolean onOff) {
		position2D = onOff;
		if (position2D) {
			position1D = false;
			drawTreemap = false;
		}
		updateShowFiles();
	}

	public void enableVersionBinding(boolean onOff) {
		enableVersionBinding = onOff;
		updateSprings();
	}

	public void enableTagBinding(boolean onOff) {
		enableTagBinding = onOff;
		updateSprings();
	}

	public void enableFileBinding(boolean onOff) {
		enableFileBinding = onOff;
		updateSprings();
	}

	public void setMinTime(boolean onOff) {
		setMinTime = onOff;
		if (setMinTime) {
			minTime = new Timestamp(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000); // "1 month"
		} else {
			minTime = null;
		}
		updateShowFiles();
	}

	public void drawTags(boolean onOff) {
		drawTags = onOff;

		if (onOff) {
			updateTags();
		}
	}

	public void setButtonStates(){
		
		
	
		setButtonState("position1D", position1D);
		setButtonState("position2D", position2D);
		
		setButtonState("drawTreemap", drawTreemap);
		setButtonState("draw2DShape", draw2DShape);
		setButtonState("drawAccessShapes", drawAccessShapes);
		
		setButtonState("setZTimeAxis", setZTimeAxis);
		
		setButtonState("showVersions", showVersions);
		
		
		setButtonState("drawTags", drawTags);
		setButtonState("showTimeline", showTimeline);
		
		
		setButtonState("enableVersionBinding", enableVersionBinding);
		setButtonState("enableTagBinding", enableTagBinding);
		setButtonState("enableFileBinding", enableFileBinding);
	}
	
	public void setButtonState(String buttonName, boolean onOff) {
		for (Button_LabelToggle b : testButton) {
			if (b.label.equals(buttonName)) {
				b.onOff = onOff;
			}
		}
	}

	public Vec3D interpolateVec(Vec3D aktuell, Vec3D target) {
		aktuell.x = interpolate(aktuell.x, target.x);
		aktuell.y = interpolate(aktuell.y, target.y);
		aktuell.z = interpolate(aktuell.z, target.z);
		return new Vec3D(aktuell.x, aktuell.y, aktuell.z);
	}

	public float interpolate(float aktuell, float target) {

		if (abs(aktuell - target) > 0.1f) {
			aktuell += (target - aktuell) / 20.0f;
			// println("aktuell: " + aktuell);
		} else {
			aktuell = target;
		}
		return aktuell;
	}
}

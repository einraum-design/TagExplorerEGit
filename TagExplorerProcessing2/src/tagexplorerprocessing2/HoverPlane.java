package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.SimpleTimeZone;

import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

public class HoverPlane extends Plane {

	PShape infoBox;
	PShape timeline;
	String fileName;
	Tag tag;

	int w = 0;

	int _h = 240;
	int h = _h;
	int rand = 20;

	int tagsHeight = 0;

	Button_Symbol openButton;

	String infoText = "add Tag";
	String inputFieldName = "tagInputHoverPlane";

	public HoverPlane(TagExplorerProcessing2 p5, Tag tag, int x, int y) {
		super(p5, x, y);
		this.fileName = tag.name;
		this.tag = tag;
		cp5 = p5.cp5_Menu;

		p5.textFont(p5.font, 20);
		w = (int) p5.textWidth(fileName) + 60 + rand;
		if (w < 300) {
			w = 300;
		}

		if (tag instanceof Tag_File) {

			// wenn Attributes hat h + 30
			if (((Tag_File) this.tag).attributeBindings.size() > 0) {
				tagsHeight = 30;
				h = _h + tagsHeight;
				infoBox = createInfoBox(h);
			} else {
				infoBox = createInfoBox(h);
			}
			
			timeline = createTimeline();
			
			infoBox.addChild(timeline);

			openButton = new Button_Symbol(p5, "open", x + w / 2 - rand / 2 - p5.open.width, y - h + rand / 2);

			createTextField(inputFieldName, infoText, x - w / 2 + rand, y - 125);

			createTypeButtons(inputFieldName);
		}
		// Tag:
		else {
			infoBox = createInfoBox(h + 30);
		}
		// buttonList.add(b);

		// b = new Button_Symbol(p5, p5.closeImg, x+w-15-80, y-30-20);
		// buttonList.add(b);

		// System.out.println(this.toString());
	}

	@Override
	public String toString() {
		return "HoverPlane [p5=" + p5 + ", infoBox=" + infoBox + ", fileName=" + fileName + ", w=" + w + ", h=" + h
				+ "]";
	}

	public void render() {
		// p5.fill(255);
		// p5.noStroke();
		p5.shape(infoBox, x, y);

		p5.textFont(p5.font, 20);
		p5.fill(p5.cFont);
		p5.textAlign(PConstants.LEFT, PConstants.BOTTOM);
		p5.text(fileName, x - w / 2 + 60, y - h + 55);

		// for(Button b:buttonList){
		// b.render();
		// }

		// Wenn es sich um eine File handelt!
		if (this.tag instanceof Tag_File) {

			// draw Erstellungs und letzter Zugriff
			p5.sdf.applyPattern("dd. MM. yyyy");

			Date creationDate = new Date(((Tag_File) this.tag).creation_time.getTime());
			Date lastAccessDate = new Date(p5.getNewestDate((Tag_File) tag).getTime());

			p5.textFont(p5.font, 12);
			p5.fill(p5.cFont);

			p5.textAlign(PConstants.LEFT, PConstants.BOTTOM);
			p5.text(p5.sdf.format(creationDate), x - w / 2 + rand, y - 40);

			p5.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
			p5.text(p5.sdf.format(lastAccessDate), x + w / 2 - rand, y - 40);

			// verwende die funktion von MenüPlane
			updateTextfieldValue(inputFieldName, infoText);

			// draw Dropdown List
			dropDownHeight = drawTagAuswahlList(inputFieldName);

			// buttons to save new Tag
			drawSaveTypeButtons();

			// Open Button
			openButton.render();
			if (p5.mouseActive && p5.mousePressed && openButton.mouseOver() && ((Tag_File) tag).delete_time == null) {
				p5.SQL.setAccessTimeNow((Tag_File) tag);

				p5.setShape((Tag_File) tag);

				p5.lastClick = new Timestamp(System.currentTimeMillis());
				p5.mouseActive = false;
				System.out.println("ResetLastClick in HoverPlane.render()");
			}

			// draw binded Tags
			int xShift = 0;
			int yShift = 0;
			for (Tag tag : ((Tag_File) this.tag).attributeBindings) {
				int tagWidth = tag.getWidth(p5, (Tag_File) this.tag);

				if (xShift + tagWidth > w - 2 * rand) {

					xShift = 0;
					yShift += 30;
				}

				xShift += tag.renderTag(p5, (Tag_File) this.tag, (int) x - w / 2 + rand + xShift, (int) y - h + 75
						+ yShift);
				xShift += 3; // tag X abstand
			}

			if (((Tag_File) this.tag).attributeBindings.size() > 0) {
				yShift += 30;
			}

			if (tagsHeight != yShift) {
				tagsHeight = yShift;

				h = _h + tagsHeight;
				infoBox = createInfoBox(h);
				infoBox.addChild(timeline);
				openButton.y = y - h + rand / 2;
			}

			// !!!!!! nur wenn Attribute sich ändern!
			// update attributeBindings der File

			ArrayList<Tag> bindetTags = p5.SQL.getBindedTagList(((Tag_File) this.tag));

			if (bindetTags.size() != ((Tag_File) this.tag).attributeBindings.size()) {
				((Tag_File) this.tag).attributeBindings = bindetTags;
				// wenn attribut geändert wurden:
				((Tag_File) this.tag).setTextur(p5.generateTexture((Tag_File) this.tag));
				p5.setShape((Tag_File) this.tag);
				p5.updateSprings();
			}
		}

	}

	// draw TagList Auswahl Textfeld
	public int drawTagAuswahlList(String inputFieldName) {
		if (cp5.get(Textfield.class, inputFieldName).isFocus()) {

			ArrayList<Tag> sortedTags = sort(inputFieldName, 12);

			for (int i = 0; i < sortedTags.size(); i++) {
				Tag tag = sortedTags.get(i);
				Button_DropDownTag b = new Button_DropDownTag(p5, tag.name + " " + tag.bindCount, tag.type, cp5.get(
						Textfield.class, inputFieldName).getWidth(), cp5.get(Textfield.class, inputFieldName)
						.getHeight(), (int) cp5.get(Textfield.class, inputFieldName).getPosition().x, (int) cp5.get(
						Textfield.class, inputFieldName).getPosition().y);
				b.y = b.y + cp5.get(Textfield.class, inputFieldName).getHeight() * (i + 1);
				b.render();
				if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {

					// add TagBinding to Tag_File!
					((Tag_File) this.tag).attributeBindings.add(tag);
					p5.SQL.bindTag((Tag_File) this.tag, tag);

					// p5.updateShowFiles();
					p5.updateTags();
					p5.updateSprings();
					p5.lastClick = new Timestamp(System.currentTimeMillis());
					p5.mouseActive = false;
				}
			}
			return (sortedTags.size() + 1) * cp5.get(Textfield.class, inputFieldName).getHeight();

		}

		return 0;
	}

	public void drawSaveTypeButtons() {
		for (Button b : buttonTypeList) {
			b.render();
			// text nicht gleich "" oder infotext
			if (!cp5.get(Textfield.class, inputFieldName).getText().trim().toLowerCase().equals("")
					&& !cp5.get(Textfield.class, inputFieldName).getText().equals(infoText)) {
				if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {

					System.out.println(cp5.get(Textfield.class, inputFieldName).getText().trim());
					// create neuen Tag + update Tags + Springs
					Tag tag = p5.createNewTag(cp5.get(Textfield.class, inputFieldName).getText().trim(),
							((Button_TagType) b).type);

					// wenn tag schon existiert gebe tag zurück (nur
					// absicherung)
					if (tag == null) {
						System.out.println("HoverPlane.drawSaveTypeButtons(): Tag exixts already - use existing tag");
						tag = p5.getTagByName(cp5.get(Textfield.class, inputFieldName).getText().trim());
					}

					// add File-Tag binding
					((Tag_File) this.tag).attributeBindings.add(tag);
					p5.SQL.bindTag(((Tag_File) this.tag), tag);
					p5.updateTags();

					p5.lastClick = new Timestamp(System.currentTimeMillis());
					p5.mouseActive = false;
				}
			} else {
				//
				// System.out.println("inputTextField ist " +
				// cp5.get(Textfield.class, inputFieldName).getText());
			}

		}

		// return (int) buttonTypeList.get(buttonTypeList.size() - 1).x +
		// buttonTypeList.get(buttonTypeList.size() - 1).h;
	}

	public ArrayList<Tag> sort(String inputFieldName) {
		ArrayList<Tag> sortedTags = new ArrayList<Tag>();
		// nicht gleich "":
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (inputText.equals("")) {
			// sortiere nach Häufigkeit
			sortedTags = (ArrayList<Tag>) p5.attributes.clone();
			Collections.sort(sortedTags, comp_bindCount);
		} else {
			// sortiere nach Anfangsbuchstaben

			for (Tag tag : p5.attributes) {
				if (tag.name.toLowerCase().startsWith(
						cp5.get(Textfield.class, inputFieldName).getText().trim().toLowerCase())) {
					sortedTags.add(tag);
				}
			}

			Collections.sort(sortedTags, comp_bindCount); // oder comp_Name
		}

		// entferne schon gewählt Tags aus sorted Tags
		for (Tag tag : ((Tag_File) this.tag).attributeBindings) {
			if (sortedTags.contains(tag)) {
				sortedTags.remove(tag);
			}
		}

		// maximal Anzahl
		while (sortedTags.size() > 6) {
			sortedTags.remove(sortedTags.size() - 1);
		}
		// System.out.println("sortedTags.size()" + sortedTags.size());

		return sortedTags;
	}

	// private PShape createInfoBox() {
	// float yOffset = 20;
	// float xOffset = 12;
	// float rad = 5.0f;
	// float kappa = (4.0f * (PApplet.sqrt(2.0f) - 1.0f) / 3.0f);
	// PShape infoBox = p5.createShape();
	// infoBox.fill(255);
	// infoBox.vertex(0, 0);
	// infoBox.vertex(0, -h + rad);
	// infoBox.bezierVertex(0, -h + rad - rad * kappa, rad - rad * kappa, -h,
	// rad, -h);
	// infoBox.vertex(w - rad, -h);
	// infoBox.bezierVertex(w - rad + rad * kappa, -h, w, -h + rad - rad *
	// kappa, w, -h + rad);
	// infoBox.vertex(w, -yOffset - rad);
	// infoBox.bezierVertex(w, -yOffset - rad + rad * kappa, w - rad + rad *
	// kappa, -yOffset, w - rad, -yOffset);
	// infoBox.vertex(xOffset, -yOffset);
	// infoBox.end(PConstants.CLOSE);
	// return infoBox;
	// }

	private PShape createInfoBox(int h) {

		PShape shape = p5.createShape(PConstants.GROUP);

		float yOffset = 28;
		float xOffset = 28;
		// float rad = 5.0f;
		// float kappa = (4.0f * (PApplet.sqrt(2.0f) - 1.0f) / 3.0f);
		PShape infoBox = p5.createShape();
		infoBox.fill(255);
		infoBox.stroke(p5.cBorderHover);
		infoBox.vertex(0, 0);
		infoBox.vertex(-xOffset, -yOffset);
		infoBox.vertex(-w / 2, -yOffset);
		infoBox.vertex(-w / 2, -h);
		infoBox.vertex(w / 2, -h);
		infoBox.vertex(w / 2, -yOffset);
		infoBox.vertex(xOffset, -yOffset);
		infoBox.end(PConstants.CLOSE);

		shape.addChild(infoBox);

		// line
		PShape line = p5.createShape(PConstants.LINES);
		line.stroke(p5.cBorder);
		// Unterstreichung
		line.vertex(-w / 2 + rand, (-h + 60));
		line.vertex(w / 2 - rand, (-h + 60));

		// line für access Shape
		if (tag instanceof Tag_File) {
			line.vertex(-w / 2 + rand, yTimeline);
			line.vertex(w / 2 - rand, yTimeline);
		}
		line.end();

		shape.addChild(line);

		

		// Accesses Shape

		return shape;
	}
	
	int yTimeline = -75;
	
	public PShape createTimeline(){
		PShape shape = p5.createShape(PConstants.GROUP);
		long startTime = ((Tag_File) this.tag).creation_time.getTime();
		long endTime = (p5.getNewestDate((Tag_File) tag).getTime());

		float rad = 6;
		// startEllipse
		PShape startEllipse = p5.createShape(PConstants.ELLIPSE, -w/2+ rand -rad/2, yTimeline -rad/2, rad, rad);
		startEllipse.fill(p5.cFont);

		shape.addChild(startEllipse);

		for (Access access : ((Tag_File) this.tag).getAccesses()) {
			
			float xPos = PApplet.map(access.date.getTime(), startTime, endTime, -w/2 + rand, w/2 -rand);
			
			PShape ellips = p5.createShape(PConstants.ELLIPSE, xPos - rad/2, yTimeline-rad/2, rad, rad);
			ellips.fill(p5.cFont);
			shape.addChild(ellips);
		}
		
		
		return shape;
	}

	public void createTextField(String name, String value, float x, float y) {
		// System.out.println("createTextfield");

		cp5.addTextfield(name).setValue(value).setPosition(x, y).setSize(w - 120, 18)
				.setFont(p5.createFont("arial", 14)).setFocus(false).setColorCursor(0)
				.setColorBackground(p5.color(255)).setColorActive(p5.color(0, 255, 50)).setColor(p5.color(0))
				.getCaptionLabel().setVisible(false);
		// cp5.get(Textfield.class, inputFieldName).getLabel().
	}

	ArrayList<Button> buttonTypeList = new ArrayList<Button>();

	public void createTypeButtons(String inputFieldName) {
		// cp5.get(Textfield.class, inputFieldName).setValue(tagName);

		// create Type Buttons
		int shiftX = (int) cp5.get(Textfield.class, inputFieldName).getPosition().x;
		int shiftY = (int) cp5.get(Textfield.class, inputFieldName).getPosition().y - 24;

		Button keywordButton = new Button_TagType(p5, p5.newKeyword, "keywords", 20, 20, shiftX, shiftY);
		// cp5.get(Textfield.class, inputFieldName).getWidth(),
		// cp5.get(Textfield.class, inputFieldName).getHeight(),
		// shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5,
		// shiftY); //+ (int) cp5.get(Textfield.class,
		// inputFieldName).getHeight());

		buttonTypeList.add(keywordButton);

		shiftX += keywordButton.w + 5;

		Button locationButton = new Button_TagType(p5, p5.newLocation, "locations", 20, 20, shiftX, shiftY);
		// cp5.get(Textfield.class, inputFieldName).getWidth(),
		// cp5.get(Textfield.class, inputFieldName).getHeight(),
		// shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5,
		// shiftY); //+ (int) cp5.get(Textfield.class,
		// inputFieldName).getHeight());
		buttonTypeList.add(locationButton);

		shiftX += keywordButton.w + 5;

		Button userButton = new Button_TagType(p5, p5.newUser, "users", 20, 20, shiftX, shiftY);
		// cp5.get(Textfield.class, inputFieldName).getWidth(),
		// cp5.get(Textfield.class, inputFieldName).getHeight(),
		// shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5,
		// shiftY); //+ (int) cp5.get(Textfield.class,
		// inputFieldName).getHeight());
		buttonTypeList.add(userButton);

		shiftX += keywordButton.w + 5;

		Button projectButton = new Button_TagType(p5, p5.newProject, "projects", 20, 20, shiftX, shiftY);
		// cp5.get(Textfield.class, inputFieldName).getWidth(),
		// cp5.get(Textfield.class, inputFieldName).getHeight(),
		// shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5,
		// shiftY); //+ (int) cp5.get(Textfield.class,
		// inputFieldName).getHeight());
		buttonTypeList.add(projectButton);

		shiftX += projectButton.w + 5;

		// weiter Button Typen
	}

	public boolean mouseOver() {
		boolean over = false;
		if (p5.mouseX >= x - w / 2 && p5.mouseX < x + w / 2 && p5.mouseY < y + 10 && p5.mouseY > y - h) {
			over = true;
		} else if (dropDownHeight > 0
				&& cp5.get(Textfield.class, inputFieldName).isFocus()
				&& p5.mouseX >= cp5.get(Textfield.class, inputFieldName).getPosition().x - 10
				&& p5.mouseX < cp5.get(Textfield.class, inputFieldName).getPosition().x
						+ cp5.get(Textfield.class, inputFieldName).getWidth() + 10
				&& p5.mouseY > cp5.get(Textfield.class, inputFieldName).getPosition().y - 10
				&& p5.mouseY < cp5.get(Textfield.class, inputFieldName).getPosition().y + dropDownHeight + 10) {

			over = true;
		}

		return over;
	}
}

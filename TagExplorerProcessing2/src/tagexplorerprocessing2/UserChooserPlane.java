package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import controlP5.Textfield;
import processing.core.PConstants;

public class UserChooserPlane extends Plane {

	Tag_User user = null;

	String infoText = "Wähle Nutzer";
	String inputFieldName = "userInput";

	String passwortText = "******";
	String passwortFieldName = "passwortInput";

	int w;
	int h;

	Button_Symbol b_ok;

	boolean ok = false;

	public UserChooserPlane(TagExplorerProcessing2 p5, int x, int y) {
		super(p5, x, y);
		w = 700;
		h = 430;

		createTextField(inputFieldName, infoText, x - w / 2 + 140, y + 120, 300, 28);

		b_ok = new Button_Symbol(p5, "user", (int) (x + w / 2 - cp5.get(Textfield.class, inputFieldName).getHeight()
				/ 2 - 50), (int) (cp5.get(Textfield.class, inputFieldName).getPosition().y + cp5.get(Textfield.class,
				inputFieldName).getHeight() / 2));
		// Passwort
		// createTextField(passwortFieldName, passwortText, x - w / 2 + 140, y +
		// 120, 300, 28);
	}

	public void render() {

		// weiße Fläche
		p5.fill(255);
		p5.stroke(p5.cBorderHover);
		p5.rect(x - w / 2, y, w, h);

		// Anmelden
		p5.fill(p5.cFont);
		p5.textFont(p5.font, 28);
		p5.textAlign(PConstants.LEFT, PConstants.BOTTOM);
		p5.text("Anmelden", x - w / 2 + 140, y + 60);

		// Filter Vorschläge
		p5.textFont(p5.font, 14);
		p5.text("Filter Vorschlag", x - w / 2 + 140, y + 210);

		if (user != null) {

			p5.imageMode(PConstants.CORNER);
			if (user.img != null) {
				p5.image(user.img,x - w / 2 + 50, y + 100, 64, 64);
			} else{
				p5.image(p5.userImg,x - w / 2 + 50, y + 100, 64, 64);
			}

			if (user.id == 2) {
				p5.text("Heute, 11.30 – 12.30	Masterpräsentation FH Würzburg", x - w / 2 + 140, y + 240);
				p5.text("Check in, 10.54 Uhr", x - w / 2 + 140, y + 270);
				p5.text("Veranstaltung: 11.30 Uhr, Masterpräsentation", x - w / 2 + 140, y + 300);

				p5.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
				p5.text("foursquare", x - w / 2 + 120, y + 270);
				p5.text("facebook", x - w / 2 + 120, y + 300);

				// Präsentation
				new Filter(p5.getTagByID("keywords", 14), true)
						.renderSmall(p5, (int) (x - w / 2 + 140 + p5
								.textWidth("Heute, 11.30 – 12.30	Masterpräsentation FH Würzburg")) + 10,
								(int) (y + 240) - 18);

				// Fachhochschule Würzburg
				new Filter(p5.getTagByID("locations", 2), true).renderSmall(p5,
						(int) (x - w / 2 + 140 + p5.textWidth("Check in, 10.54 Uhr")) + 10, (int) (y + 270) - 18);

				// Fachhochschule Würzburg
				new Filter(p5.getTagByID("locations", 2), true).renderSmall(p5,
						(int) (x - w / 2 + 140 + p5.textWidth("Veranstaltung: 11.30 Uhr, Masterpräsentation")) + 10,
						(int) (y + 300) - 18);

				p5.imageMode(PConstants.CORNER);
				// Event Icon
				p5.image(p5.newEvent, x - w / 2 + 120 - p5.newEvent.width, y + 240 - p5.newEvent.height);

				// map Button (keine Funktion)
				p5.image(p5.mapImg, x - w / 2 + 140, y + 330);
			}
		}

		p5.stroke(p5.cBorder);
		p5.line(x - w / 2 + 140, y + 70, x + w / 2 - 50, y + 70);

		// p5.stroke(p5.cBorder);
		p5.line(x - w / 2 + 140, y + 215, x + w / 2 - 50, y + 215);

		updateTextfieldValue(inputFieldName, infoText);

		// OK Button
		p5.noFill();
		p5.rect(x + w / 2 - cp5.get(Textfield.class, inputFieldName).getHeight() - 50,
				cp5.get(Textfield.class, inputFieldName).getPosition().y, cp5.get(Textfield.class, inputFieldName)
						.getHeight(), cp5.get(Textfield.class, inputFieldName).getHeight());
		b_ok.render();

		// User Auswahl bestätigen
		if (user != null && p5.mouseActive && b_ok.mouseOver() && p5.mousePressed) {
			ok = true;
			p5.lastClick = new Timestamp(System.currentTimeMillis());
			p5.mouseActive = false;

			p5.mainUser = user;
			p5.filterList.add(new Filter(p5.mainUser, true));
			p5.updateShowFiles();
			p5.updateTags();
			p5.updateApplications();
			p5.updateSprings();
		}

		// draw Dropdown unter Textfield
		dropDownHeight = drawTagList(inputFieldName);
	}

	public void createTextField(String name, String value, float x, float y, int w, int h) {
		// System.out.println("createTextfield");

		cp5.addTextfield(name).setValue(value).setPosition(x, y).setSize(w, h).setFont(p5.createFont("arial", 20))
				.setFocus(false).setColorCursor(p5.cFont).setColorBackground(p5.color(255)).setColor(p5.cFont)
				.setColorActive(p5.cBorderHover).getCaptionLabel().setVisible(false);
		// cp5.get(Textfield.class, inputFieldName).getLabel().
	}

	// draw Dropdown unter Textfield
	public int drawTagList(String inputFieldName) {
		if (cp5.get(Textfield.class, inputFieldName).isFocus()) {

			// gibt sortierte List an User zurück
			ArrayList<Tag> sortedTags = sort(inputFieldName, 12);

			for (int i = 0; i < sortedTags.size(); i++) {
				Tag tag = sortedTags.get(i);
				Button_DropDownGross b = new Button_DropDownGross(p5, tag.name + " " + tag.bindCount, tag.type, cp5
						.get(Textfield.class, inputFieldName).getWidth(), cp5.get(Textfield.class, inputFieldName)
						.getHeight(), (int) cp5.get(Textfield.class, inputFieldName).getPosition().x, (int) cp5.get(
						Textfield.class, inputFieldName).getPosition().y);
				b.y = b.y + cp5.get(Textfield.class, inputFieldName).getHeight() * (i + 1);
				b.renderOhneFilter();
				if (p5.mouseActive && b.mouseOverField() && p5.mousePressed) {

					// add to filterList
					// p5.filterList.add(new Filter(tag, true));

					// add as user -> wird zu mainUser bei ok
					user = (Tag_User) tag;
					System.out.println("Gewählter Nutzer: " + user.name);

					// remove from filterList ist in Filter class!

					// get lastTime Tag used as Filter: Timestamp
					tag.lastStartFilterTime = p5.SQL.getFilterTime(tag, "start_time");
					tag.lastEndFilterTime = p5.SQL.getFilterTime(tag, "end_time");

					// Save Timestamp and TagID and TagType -> SQL filter_time
					p5.SQL.setFilterTime(tag, true); // startFilter

					// erst bei Bestätigung auf UserChooser aktualisieren!
					// p5.updateShowFiles();
					// p5.updateTags();
					// p5.updateApplications();
					// p5.updateSprings();
					p5.lastClick = new Timestamp(System.currentTimeMillis());
					p5.mouseActive = false;
				}
			}
			return (sortedTags.size() + 1) * cp5.get(Textfield.class, inputFieldName).getHeight();
		}
		return 0;
	}

	public ArrayList<Tag> sort(String inputFieldName) {
		ArrayList<Tag> sortedTags = new ArrayList<Tag>();
		// nicht gleich "":
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (inputText.equals("")) {
			// sortiere nach Häufigkeit
			sortedTags = (ArrayList<Tag>) p5.availableUsers.clone();
			Collections.sort(p5.availableUsers, comp_bindCount);
		} else {
			// sortiere nach Anfangsbuchstaben alle Users -> nur bei gleichem
			// anfang

			for (Tag tag : p5.availableUsers) {
				if (tag.name.toLowerCase().startsWith(
						cp5.get(Textfield.class, inputFieldName).getText().trim().toLowerCase())) {
					sortedTags.add(tag);
				}
			}

			Collections.sort(sortedTags, comp_bindCount); // oder comp_Name
		}

		// entferne schon gewählt Tags aus sorted Tags
		for (Filter f : p5.filterList) {
			if (sortedTags.contains(f.tag)) {
				sortedTags.remove(f.tag);
			}
		}

		// maximal Anzahl
		// while (sortedTags.size() > 6) {
		// sortedTags.remove(sortedTags.size() - 1);
		// }
		// System.out.println("sortedTags.size()" + sortedTags.size());

		return sortedTags;
	}

	public ArrayList<Tag> sort(String inputFieldName, int maxCount) {
		ArrayList<Tag> sortedTags = sort(inputFieldName);

		// maximal Anzahl
		while (sortedTags.size() > maxCount) {
			sortedTags.remove(sortedTags.size() - 1);
		}
		return sortedTags;
	}

	public void updateTextfieldValue(String inputFieldName, String infoText) {
		// nicht gleich infotext:
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (cp5.get(Textfield.class, inputFieldName).isFocus() && inputText.equals(infoText)) {
			cp5.get(Textfield.class, inputFieldName).setValue("");
		}

		// nicht Focus -> infoText
		if (!cp5.get(Textfield.class, inputFieldName).isFocus()) {
			if (user != null) {
				cp5.get(Textfield.class, inputFieldName).setValue(user.name);
			} else {
				cp5.get(Textfield.class, inputFieldName).setValue(infoText);
				// System.out.println("Choose User!");
			}
		}
	}

}

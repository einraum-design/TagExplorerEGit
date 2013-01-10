package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import controlP5.ControlP5;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;

public class MenuPlane {
	TagExplorerProcessing2 p5;

	Tag_Comparator_BindCount comp_bindCount;
	Tag_Comparator_Name comp_Name;
	
	ControlP5 cp5;
	PFont font;

	float minH = 30;

	float h = minH;

	String infoText = "search or new Tagname";

	// methode in TagExplorerProcessing.class muss genauso hei§en!
	String inputFieldName = "tagInput";

	public MenuPlane(TagExplorerProcessing2 p5, ControlP5 cp5) {
		this.p5 = p5;
		this.cp5 = cp5;
		
		comp_bindCount = new Tag_Comparator_BindCount();
		comp_Name = new Tag_Comparator_Name();

		font = p5.createFont("arial", 18);

		createTextField(inputFieldName, infoText);

	}

	public void update() {
		if (mouseOver()) {
			h = 80;
		} else {
			h = minH;
			;
		}

		// nicht gleich infotext:
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (cp5.get(Textfield.class, inputFieldName).isFocus() && inputText.equals(infoText)) {
			cp5.get(Textfield.class, inputFieldName).setValue("");
		}
		
		// nicht Focus -> infoText
		if (!cp5.get(Textfield.class, inputFieldName).isFocus()) {
			cp5.get(Textfield.class, inputFieldName).setValue(infoText);
		}

		// draw TagList
		if (cp5.get(Textfield.class, inputFieldName).isFocus()) {

			ArrayList<Tag> sortedTags = sort();

			for (int i = 0; i < sortedTags.size(); i++) {
				Tag tag = sortedTags.get(i);
				Button_Label b = new Button_Label(p5, tag.name + " " + tag.bindCount, cp5.get(Textfield.class,
						inputFieldName).getWidth(), cp5.get(Textfield.class, inputFieldName).getHeight(), (int) cp5
						.get(Textfield.class, inputFieldName).getPosition().x, (int) cp5.get(Textfield.class,
						inputFieldName).getPosition().y);
				b.y = b.y + cp5.get(Textfield.class, inputFieldName).getHeight() * (i + 1);
				b.render();
				if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {
					p5.filterList.add(new Filter(tag, true));
					p5.updateShowFiles();
					p5.updateTags();
					p5.updateSprings();
					p5.lastClick = new Timestamp(System.currentTimeMillis());
					p5.mouseActive = false;
				}
			}
		}

	}

	private ArrayList<Tag> sort() {
		ArrayList<Tag> sortedTags = new ArrayList<Tag>();
		// nicht gleich "":
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (inputText.equals("")) {
			// sortiere nach HŠufigkeit
			sortedTags = (ArrayList<Tag>) p5.availableTags.clone();
			Collections.sort(p5.availableTags, comp_bindCount);
		} else {
			// sortiere nach Anfangsbuchstaben
			
			for(Tag tag : p5.attributes){
				if(tag.name.toLowerCase().startsWith(cp5.get(Textfield.class, inputFieldName).getText().trim().toLowerCase())){
					sortedTags.add(tag);
				}
			}

			Collections.sort(sortedTags, comp_bindCount); // oder comp_Name
		}
		
		// entferne schon gewŠhlt Tags aus sorted Tags
		for(Filter f : p5.filterList){
			if(sortedTags.contains(f.tag)){
				sortedTags.remove(f.tag);
			}
		}
		
		// maximal Anzahl
		while(sortedTags.size() > 6){
			sortedTags.remove(sortedTags.size()-1);
		}
//		System.out.println("sortedTags.size()" + sortedTags.size());
		
		return sortedTags;
	}

	public void render() {
		p5.fill(255, 220);
		p5.noStroke();
		p5.rect(0, 0, p5.width, h);

		int xShift = 0;
		int yShift = 0;
		for (Filter filter : p5.filterList) {
			xShift += filter.render(p5, 10 + xShift, 10 + yShift) + 3;

			if (xShift > p5.width - 200) {
				xShift = 0;
				yShift += 30;
			}
		}
	}

	public void createTextField(String name, String value) {
		System.out.println("createTextfield");

		cp5.addTextfield(name).setValue(value).setPosition(10, 40).setSize(200, 28).setFont(font).setFocus(false)
				.setColorCursor(0).setColorBackground(p5.color(255)).setColorActive(p5.color(0, 255, 50))
				.setColor(p5.color(0));
	}

	public boolean mouseOver() {
		boolean over = false;

		// referenz links oben
		if (p5.mouseY < h) { // p5.mouseX >= x && p5.mouseX < x + w && &&
								// p5.mouseY < y+h
			over = true;
		}
		return over;
	}
}

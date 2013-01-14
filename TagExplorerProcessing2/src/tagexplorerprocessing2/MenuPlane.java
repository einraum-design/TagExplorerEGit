package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;

import controlP5.Textfield;

public class MenuPlane extends Plane {

	float minH = 30;

	float h = minH;

	

	// methode in TagExplorerProcessing.class muss genauso hei§en!
	String infoText = "search or new Tagname";
	String inputFieldName = "tagInput";

	public MenuPlane(TagExplorerProcessing2 p5) {
		super(p5);
		createTextField(inputFieldName, infoText, 10, 40);
	}

	public void update() {
		if (mouseOver()) {
			h = 80;
		} else {
			h = minH;
		}

		updateTextfieldValue(inputFieldName, infoText);

	}

	public void render() {
		// wei§e FlŠche
		p5.fill(255, 220);
		p5.noStroke();
		p5.rect(0, 0, p5.width, h);

		int xShift = 0;
		int yShift = 0;
		for (Filter filter : p5.filterList) {
			
			// zeichnet Tag Button mit Button_Symbol
			xShift += filter.render(p5, 10 + xShift, 10 + yShift) + 3;

			if (xShift > p5.width - 200) {
				xShift = 0;
				yShift += 30;
			}
		}
		
		dropDownHeight = drawTagList(inputFieldName);
	}
	
	public int drawTagList(String inputFieldName){
		if (cp5.get(Textfield.class, inputFieldName).isFocus()) {

			ArrayList<Tag> sortedTags = sort(inputFieldName,12);

			for (int i = 0; i < sortedTags.size(); i++) {
				Tag tag = sortedTags.get(i);
				Button_DropDown b = new Button_DropDown(p5, tag.name + " " + tag.bindCount, cp5.get(Textfield.class,
						inputFieldName).getWidth(), cp5.get(Textfield.class, inputFieldName).getHeight(), (int) cp5
						.get(Textfield.class, inputFieldName).getPosition().x, (int) cp5.get(Textfield.class,
						inputFieldName).getPosition().y);
				b.y = b.y + cp5.get(Textfield.class, inputFieldName).getHeight() * (i + 1);
				b.render();
				if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {
					
					// add to filterList
					p5.filterList.add(new Filter(tag, true));
					
					// get lastTime Tag used as Filter: Timestamp
					
//					ArrayList<Timestamp> lastTime = p5.SQL.getFilterTime(tag);
//					System.out.println("lastTime.size() = " + lastTime.size());
//					if(lastTime.get(0) != null){
						tag.lastStartFilterTime = p5.SQL.getFilterTime(tag, "start_time");
//						System.out.println("tag.lastStartFilterTime " + tag.lastStartFilterTime);
//					}
//					if(lastTime.get(1) != null){
						tag.lastEndFilterTime = p5.SQL.getFilterTime(tag, "end_time");
//						System.out.println("tag.lastEndFilterTime " + tag.lastEndFilterTime);
//					}
					
					// Save Timestamp and TagID and TagType -> SQL filter_time
					p5.SQL.setFilterTime(tag, true); // startFilter
					
					p5.updateShowFiles();
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

	public void createTextField(String name, String value, float x, float y) {
		// System.out.println("createTextfield");

		cp5.addTextfield(name).setValue(value).setPosition(x, y).setSize(200, 28).setFont(p5.createFont("arial", 18)).setFocus(false)
				.setColorCursor(0).setColorBackground(p5.color(255)).setColorActive(p5.color(0, 255, 50)).setColor(p5.color(0)).getCaptionLabel().setVisible(false);
		// cp5.get(Textfield.class, inputFieldName).getLabel().
	}

	public boolean mouseOver() {
		boolean over = false;

		// referenz links oben
		if (p5.mouseY < h) { // p5.mouseX >= x && p5.mouseX < x + w && &&
								// p5.mouseY < y+h
			over = true;
		} else if(dropDownHeight > 0 && cp5.get(Textfield.class, inputFieldName).isFocus()
//				&& p5.mouseX >= cp5.get(Textfield.class, inputFieldName).getPosition().x - 10
//				&& p5.mouseX < cp5.get(Textfield.class, inputFieldName).getPosition().x + cp5.get(Textfield.class, inputFieldName).getWidth() + 10
//				&& p5.mouseY > cp5.get(Textfield.class, inputFieldName).getPosition().y - 10 
//				&& p5.mouseY < cp5.get(Textfield.class, inputFieldName).getPosition().y + dropDownHeight + 10
				) {
			over = true;
		}
		return over;
	}
}

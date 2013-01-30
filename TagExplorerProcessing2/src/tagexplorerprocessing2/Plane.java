package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import controlP5.ControlP5;
import controlP5.Textfield;
import toxi.geom.Vec2D;

public class Plane extends Vec2D {
	TagExplorerProcessing2 p5;
	ControlP5 cp5;
	
	Tag_Comparator_BindCount comp_bindCount;
	Tag_Comparator_Name comp_Name;
	
	int dropDownHeight = 0;
	
	public Plane(TagExplorerProcessing2 p5){
		super(0, 0);
		this.p5 = p5;
		cp5 = p5.cp5_Menu;
		
		comp_bindCount = new Tag_Comparator_BindCount();
		comp_Name = new Tag_Comparator_Name();
	}
	
	public Plane(TagExplorerProcessing2 p5, int x, int y){
		super(x, y);
		this.p5 = p5;
		cp5 = p5.cp5_Menu;
		
		comp_bindCount = new Tag_Comparator_BindCount();
		comp_Name = new Tag_Comparator_Name();
	}
	
	
	
	
	String lastInput = null;
	
	public void updateTextfieldValue(String inputFieldName, String infoText) {
		
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		
		if(!inputText.equals(infoText) && !inputText.equals("")){
			lastInput = cp5.get(Textfield.class, inputFieldName).getText().trim();
		}
		
		// nicht gleich infotext:
		// lšsche Infotext aus feld
		
		if (cp5.get(Textfield.class, inputFieldName).isFocus() && inputText.equals(infoText)) {
			cp5.get(Textfield.class, inputFieldName).setValue("");
		}

		// nicht Focus -> infoText
		if (!cp5.get(Textfield.class, inputFieldName).isFocus()) {
			cp5.get(Textfield.class, inputFieldName).setValue(infoText);
		}
	}
	
	
	public ArrayList<Tag> sort(String inputFieldName) {
		ArrayList<Tag> sortedTags = new ArrayList<Tag>();
		// nicht gleich "":
		String inputText = cp5.get(Textfield.class, inputFieldName).getText().trim();
		if (inputText.equals("")) {
			// sortiere nach HŠufigkeit
			sortedTags = (ArrayList<Tag>) p5.availableTags.clone();
			Collections.sort(p5.availableTags, comp_bindCount);
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

		// entferne schon gewŠhlt Tags aus sorted Tags
		for (Filter f : p5.filterList) {
			if (sortedTags.contains(f.tag)) {
				sortedTags.remove(f.tag);
			}
		}

		// maximal Anzahl
//		while (sortedTags.size() > 6) {
//			sortedTags.remove(sortedTags.size() - 1);
//		}
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
	

	
	
}

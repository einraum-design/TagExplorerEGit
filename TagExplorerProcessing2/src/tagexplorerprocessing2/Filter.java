package tagexplorerprocessing2;

import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PImage;

public class Filter {
	Tag tag;
	boolean inOut;
	int id;
	static int counter = 0;
	
	public Filter(Tag tag, boolean inOut) {
		this.tag = tag;
		this.inOut = inOut;
		this.id = counter;
		counter++;
	}
	public int getWidth(TagExplorerProcessing2 p5){
		p5.textFont(p5.font, 16);
		int w = (int) p5.textWidth(tag.name) + 2*h;
		return w;
	}
	
	int h = 28;
	public int render(TagExplorerProcessing2 p5, int x, int y) {

		int w = (int) p5.textWidth(tag.name) + 2*h;
		
		p5.fill(p5.cButtonBright);
		p5.stroke(p5.cBorder);
		p5.rect(x, y, w, h);
		
		p5.imageMode(PConstants.CENTER);
		// img Type icon
		switch (tag.type) {
		case "keywords":
			p5.image(p5.minKeyword, x+h/2, y + h/2);
			break;
		case "locations":
			p5.image(p5.minLocation, x+h/2, y + h/2);
			break;
		case "projects":
			p5.image(p5.minProject, x+h/2, y + h/2);
			break;
		case "users":
			p5.image(p5.minUser, x+h/2, y + h/2);
			break;
		case "events":
			p5.image(p5.minEvent, x+h/2, y + h/2);
			break;
		default:
			p5.image(p5.minKeyword, x+h/2, y + h/2);
		}

		
		p5.fill(p5.cFont);
		p5.textAlign(PConstants.LEFT, PConstants.CENTER);
		p5.text(tag.name, x + h, y + h / 2);
		
		
		// wieder ausblenden!
//		if(tag.lastStartFilterTime != null){
//			p5.text(tag.lastStartFilterTime.toGMTString(), x + 5, y + h / 2 + 50);
//		}
		
		Button_Symbol b = new Button_Symbol(p5, "close", x + w - h/2, y + h / 2);
		b.render();

		// remove From FilterList
		if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {
			removeFromFilterList(p5);
		}

		return w;
	}
	
	void removeFromFilterList(TagExplorerProcessing2 p5){

			ArrayList<Filter> myFilters = (ArrayList<Filter>) p5.filterList.clone();
			myFilters.remove(this);
			p5.SQL.setFilterTime(this.tag, false);
			p5.filterList = myFilters;

			System.out.println("Filter.removeFromFilterList: updateShowFile & Springs in Filter");
			p5.updateShowFiles();
			p5.updateTags();
			p5.updateApplications();
			p5.updateSprings();
	}

	@Override
	public String toString() {
		return "FilterM [tag=" + tag + ", inOut=" + inOut + ", id=" + id+ "]";
	}
}
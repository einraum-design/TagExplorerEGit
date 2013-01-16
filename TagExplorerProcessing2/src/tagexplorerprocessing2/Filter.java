package tagexplorerprocessing2;

import java.util.ArrayList;

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

	public int render(TagExplorerProcessing2 p5, int x, int y) {

		int w = (int) p5.textWidth(tag.name) + 25;
		int h = 22;
		p5.fill(150);
		p5.rect(x, y, w, h);
		p5.fill(0);
		p5.textAlign(p5.LEFT, p5.CENTER);
		p5.text(tag.name, x + 5, y + h / 2);
		
		
		// wieder ausblenden!
		if(tag.lastStartFilterTime != null){
			p5.text(tag.lastStartFilterTime.toGMTString(), x + 5, y + h / 2 + 50);
		}
		

		Button_Symbol b = new Button_Symbol(p5, p5.closeImg, x + w - p5.closeImg.width - 5, y + h / 2
				- p5.closeImg.height / 2);
		b.render();

		// remove From FilterList
		if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {
			removeFromFilterList(p5);
		}

		// update filterList
		// System.out.println("filterList.size(): " + filterMList.size());
		

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
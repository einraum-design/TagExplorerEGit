package tagexplorerprocessing2;

import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

import treemap.SimpleMapModel;

public class FileMap extends SimpleMapModel {
	//HashMap mapItems;
	
	MapItem_Comparator comp_item;
	
	ArrayList<MapItem> mapItems;
	PApplet p5;
	
	public FileMap(PApplet p5){
		comp_item = new MapItem_Comparator();
		this.p5 = p5;
		//mapItems = new HashMap();
		mapItems = new ArrayList<MapItem>();
		
		
	}
	
	public void addItem(int id, int count) {
	    MapItem item = new MapItem(p5, id, count);
	    //mapItems.put(name, item);
	    
	    mapItems.add(item);
	  }
	
	public void finishAdd(){
		Collections.sort(mapItems, comp_item);
		this.items = new MapItem[mapItems.size()]; // Array von MapItems
		
		mapItems.toArray(items); // werden an Array übergeben // lässt sich verkürzen!
	}
}

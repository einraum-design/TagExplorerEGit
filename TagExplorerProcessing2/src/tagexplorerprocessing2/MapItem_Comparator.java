package tagexplorerprocessing2;

import java.util.Comparator;

public class MapItem_Comparator implements Comparator<MapItem> {

	@Override
	public int compare(MapItem o1, MapItem o2) {
		// TODO Auto-generated method stub
		return (int) (o2.getSize() - o1.getSize());
	}

}

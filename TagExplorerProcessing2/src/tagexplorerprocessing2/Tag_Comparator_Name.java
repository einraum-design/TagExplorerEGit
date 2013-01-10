package tagexplorerprocessing2;

import java.util.Comparator;

public class Tag_Comparator_Name implements Comparator<Tag> {

	@Override
	public int compare(Tag tag1, Tag tag2) {
		if(tag1.name == null && tag1.name == null){
			return 0;
		}else if(tag1.name == null){
			return 1;
		} else if(tag2.name == null){
			return -1;
		}
		return tag1.name.toLowerCase().compareTo(tag2.name.toLowerCase());
	}

}

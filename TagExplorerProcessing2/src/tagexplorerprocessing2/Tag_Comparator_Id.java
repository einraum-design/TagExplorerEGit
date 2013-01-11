package tagexplorerprocessing2;

import java.util.Comparator;

public class Tag_Comparator_Id implements Comparator<Tag> {

	public int compare(Tag tag1, Tag tag2) {

		return tag2.id - tag1.id;
	}
}

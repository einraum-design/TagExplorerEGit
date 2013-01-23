package tagexplorerprocessing2;

import java.util.Comparator;

public class App_Comparator_Count implements Comparator<Tag> {

	@Override
	public int compare(Tag tag1, Tag tag2) {
		
		Tag_App app1 = (Tag_App) tag1;
		Tag_App app2 = (Tag_App) tag2;
		
		return app2.count-app1.count;
	}

}

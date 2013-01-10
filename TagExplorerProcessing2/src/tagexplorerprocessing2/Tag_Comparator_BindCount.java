package tagexplorerprocessing2;

import java.util.Comparator;

public class Tag_Comparator_BindCount implements Comparator<Tag> {

	@Override
	public int compare(Tag tag1, Tag tag2) {
		if(tag1.bindCount == tag2.bindCount){
			return 0;
		} else if(tag1.bindCount > tag2.bindCount){
			return -1;
		}else{
			return 1;
		}
	}

}

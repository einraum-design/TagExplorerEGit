package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.Comparator;

public class Tag_Comparator_Time implements Comparator<Tag> {
	@Override
	public int compare(Tag tag1, Tag tag2) {
		Tag_File file1 = (Tag_File) tag1;
		Tag_File file2 = (Tag_File) tag2;
		
		
		if(getNewestDate(file1).before(getNewestDate(file2))){
			return 1;
		} else{
			return -1;
		}
	}
	
	
	Timestamp getNewestDate(Tag_File file) {
		Timestamp newest = file.creation_time;

		// deleteTime ist neueste
		if (file.delete_time != null && file.delete_time.after(newest)) {
			newest = file.delete_time;
			return newest;
		}

		// accesses:
		for (Access c : file.getAccesses()) {
			if (c.date.after(newest)) {
				newest = c.date;
			}
		}

		// System.out.println("getNewestDate: " + newest.toGMTString());
		return newest;
	}
}

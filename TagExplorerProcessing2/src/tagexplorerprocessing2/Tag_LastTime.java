package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.Date;

public class Tag_LastTime extends Tag {
	TagExplorerProcessing2 p5;
	
	public Tag_LastTime(TagExplorerProcessing2 p5, Timestamp startTime){
		this.p5 = p5;
		
		p5.sdf.applyPattern("dd. MM. yyyy");
		Date startDate = new Date(startTime.getTime());
		
		this.name = p5.sdf.format(startDate);
		type = "minTime";		
	}
}

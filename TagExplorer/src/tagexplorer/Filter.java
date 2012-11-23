package tagexplorer;


public class Filter {
	Tag tag;
	boolean inOut; 
	int id;
	static int counter = 0;
	
	public Filter(Tag tag, boolean inOut){
		this.tag = tag;
		this.inOut = inOut;
		this.id = counter;
		counter ++;
	}
	
	
}

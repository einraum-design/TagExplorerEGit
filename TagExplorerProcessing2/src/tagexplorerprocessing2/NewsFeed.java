      package tagexplorerprocessing2;

import java.sql.Timestamp;
import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PImage;
import toxi.geom.Vec2D;

public class NewsFeed extends Vec2D{
	
	TagExplorerProcessing2 p5;
	
	
	Button_Image newButton;
	ArrayList<News> news = new ArrayList<News>();
	
	public NewsFeed(TagExplorerProcessing2 p5, int x, int y){
		super(x, y);
		this.p5 = p5;
		
		
		
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 6L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 8L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 16L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 24L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 26L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 27L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 44L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 49L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 50L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 66L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 70L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 73L * 60 * 60 * 1000)));
		news.add(new News(p5, new Timestamp(System.currentTimeMillis() - 102L * 60 * 60 * 1000)));
		
		
		newButton = new Button_Image(p5, "news", x , y);
	}
	
	
	public void update(){

	}
	
	public void setNewNews(ArrayList<Tag> newTags){
		for(int i = 0; i<newTags.size(); i++){
			
			Tag_File file = (Tag_File) newTags.get(i);
			
			if(file.getAccesses().size() > 0){
				Access access = file.getAccesses().get(file.getAccesses().size() - 1);
				news.add(i, new News(p5, access.date, "file", access.user, access.comment));
			}	
		}
	}
	
	public void render(){
		
		int yShift = 40; // platz für "NEWS"
		
		p5.textAlign(PConstants.CENTER, PConstants.BOTTOM);
		
		for(News n : news){
			n.render(0, y + yShift);
			
			yShift += n.h;
			
		}
		
//		p5.noStroke();
//		p5.fill(p5.cBorder);
//		p5.rect(x, y, w, 22);
//		
//		p5.fill(p5.cButtonBright);
//		p5.stroke(p5.cBorder);
//		
//		int yShift = 22;
//		for(int i = 0; i<12; i++){
//			p5.rect(x-w, y + yShift, w, 60);
//			yShift += 60;
//		}
//		
//		yShift = 22;
//		
//		p5.textAlign(PConstants.CENTER);
//		p5.textFont(p5.font, 12);
//		p5.fill(p5.cFont);
//		
//		p5.sdf.applyPattern("dd. MM. yyyy");
//		
//		for(News n : news){
//			
//			p5.imageMode(PConstants.CENTER);
//			p5.image(getTypeImage(n.type), x-40, y + yShift + 20, 30, 20);
//			
//			p5.text(p5.sdf.format(n.ts), x-40, y + yShift + 50);
//			yShift += 60;
//		}
		
//		for(int i = 0; i<12; i++){
//			p5.text("12. 1. 2013", x-40, y + 50 + yShift);
//			yShift += 60;
//		}	
		newButton.render();
		
	}


	
}

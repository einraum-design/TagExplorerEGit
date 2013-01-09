package tagexplorerprocessing2;

import java.sql.Timestamp;

import controlP5.ControlP5;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;

public class MenuPlane {
	TagExplorerProcessing2 p5;
	
	ControlP5 cp5;
	PFont font;

	float minH = 30;
	
	float h = minH;
	
	String infoText = "search or new Tagname";
	
	// methode in TagExplorerProcessing.class muss genauso hei§en!
	String inputFieldName = "tagInput";
	
	
	
	public MenuPlane(TagExplorerProcessing2 p5, ControlP5 cp5){
		this.p5 = p5;
		this.cp5 = cp5;
		
		font = p5.createFont("arial", 18);
		
		createTextField(inputFieldName, infoText);
		
		
		
	}
	
	public void update(){
		if(mouseOver()){
			h = 80;
		} else{
			h = minH;;
		}
		// nicht gleich infotext oder "":
		String inputText = cp5.get(Textfield.class,inputFieldName).getText().trim();
		if(!inputText.equals(infoText) && !inputText.equals("")){
			System.out.println(inputText);
		}
		
		if(cp5.get(Textfield.class,inputFieldName).isFocus()){
			for(int i = 0; i<p5.availableTags.size(); i++){
				Tag tag = p5.availableTags.get(i);
				Button_Label b = new Button_Label(p5, tag.name + " " + tag.bindCount, cp5.get(Textfield.class,inputFieldName).getWidth(), cp5.get(Textfield.class,inputFieldName).getHeight(), (int) cp5.get(Textfield.class,inputFieldName).getPosition().x, (int)cp5.get(Textfield.class,inputFieldName).getPosition().y);
				b.y = b.y + cp5.get(Textfield.class,inputFieldName).getHeight()*(i+1);
				b.render();
				if(p5.mouseActive && b.mouseOver() && p5.mousePressed){
					p5.filterList.add(new Filter(tag, true));
					p5.updateShowFiles();
					p5.updateTags();
					p5.updateSprings();
					p5.lastClick = new Timestamp(System.currentTimeMillis());
					p5.mouseActive = false;
				}
			}
		}
		
	}
	
	public void render(){
		p5.fill(255, 220);
		p5.noStroke();
		p5.rect(0, 0, p5.width, h);
		
		int xShift = 0;
		int yShift = 0;
		for(Filter filter : p5.filterList){
			xShift += filter.render(p5, 10 + xShift, 10 + yShift) + 3;
			
			if(xShift > p5.width-200){
				xShift = 0;
				yShift += 30;
			}
		}
	}
	
	public void createTextField(String name, String value) {
		System.out.println("createTextfield");

		cp5.addTextfield(name).setValue(value).setPosition(20, 100)
				.setSize(200, 20).setFont(font).setFocus(true)
				.setColor(p5.color(255, 2525, 255));
	}
	
	public boolean mouseOver(){
		boolean over = false;
		
		// referenz links oben
		if(p5.mouseY < h) { //p5.mouseX >= x && p5.mouseX < x + w &&  && p5.mouseY < y+h
			over = true;
		}	
		return over;
	}
}

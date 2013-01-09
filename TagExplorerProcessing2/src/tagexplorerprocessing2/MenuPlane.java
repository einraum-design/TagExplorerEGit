package tagexplorerprocessing2;

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

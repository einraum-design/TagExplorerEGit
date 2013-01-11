package tagexplorerprocessing2;

import java.util.ArrayList;

import processing.core.PApplet;
import controlP5.ControlP5;
import controlP5.Textfield;

public class Promt_TypeChooser {
	PApplet p5;
	ControlP5 cp5;

	//String inputFieldName = "createNewTag";

	int w = 400;
	int h = 300;

	ArrayList<Button> buttonList = new ArrayList<Button>();

	public Promt_TypeChooser(TagExplorerProcessing2 p5, ControlP5 cp5, String inputFieldName, String tagName) {
		this.p5 = p5;
		this.cp5 = cp5;

		//createTextField(inputFieldName, tagName);
		
		
		cp5.get(Textfield.class, inputFieldName).setValue(tagName);

		// create Type Buttons
		int shiftX = (int) cp5.get(Textfield.class, inputFieldName).getPosition().x;
		
		Button keywordButton = new Button_Label(p5, "new Keyword", 100, 
				cp5.get(Textfield.class, inputFieldName).getHeight(), 
				shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5, 
				(int) cp5.get(Textfield.class, inputFieldName).getPosition().y);
		
		buttonList.add(keywordButton);
		
		shiftX += keywordButton.w + 5;
		
		Button locationButton = new Button_Label(p5, "new Location", 100, 
				cp5.get(Textfield.class, inputFieldName).getHeight(),  
				shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5, 
				(int) cp5.get(Textfield.class, inputFieldName).getPosition().y);
		buttonList.add(locationButton);
		
		shiftX += locationButton.w + 5;
		
		Button userButton = new Button_Label(p5, "new User", 100, 
				cp5.get(Textfield.class, inputFieldName).getHeight(), 
				shiftX + cp5.get(Textfield.class, inputFieldName).getWidth() + 5, 
				(int) cp5.get(Textfield.class, inputFieldName).getPosition().y);
		buttonList.add(userButton);
		
		shiftX += userButton.w + 5;
		
		// weitere Buttons!

	}

	public void createTextField(String name, String value) {
		System.out.println("createTextfield");
		cp5.addTextfield(name).setValue(value).setPosition(20, 100).setSize(200, 40)
				.setFont(p5.createFont("arial", 18)).setFocus(true).setColor(p5.color(0));
	}

	public void render(){
		
		p5.fill(255);
		p5.rect(p5.width/2 - w/2 , p5.height/2 - h/2 - 100, w, h);
		
		for(Button b : buttonList){
			b.render();
		}
	}
}

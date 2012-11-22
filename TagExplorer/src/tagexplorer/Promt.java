package tagexplorer;

import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

import controlP5.Button;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;

public class Promt {
	PApplet p5;
	ControlP5 cp5;
	PFont font;
	
	String message = "";
	
	
	public Promt(PApplet p5, ControlP5 cp5, String label){
		this.p5 = p5;
		this.cp5 = cp5;
		
		font = p5.createFont("arial", 20);

		
		createButton("save", 1, 240, 100);
		createButton("cancel", 1, 300, 100);
		
		// nur ein Textfield erlaubt, sonst unterscheidung bei save function nštig!
		createTextField(label, "Type Location name here");
		
		System.out.println("new Promt");
	}
	
	public void createTextField(String name, String value) {
		System.out.println("createTextfield");
		cp5.addTextfield(name).setValue(value).setPosition(20, 100)
				.setSize(200, 40).setFont(font).setFocus(true)
				.setColor(p5.color(255, 2525, 255));
	}

	public void createButton(String name, float value, int x, int y) {
		System.out.println("createButton");
		cp5.addButton(name).setValue(value).activateBy(ControlP5.RELEASE)
				.setPosition(x, y).setSize(80, 40).getCaptionLabel()
				.align(ControlP5.CENTER, ControlP5.CENTER);
	}
	
	public void showMessages(){
		p5.fill(150);
		p5.text(message, 20, 180);
	}
	
	
	
}

package tagexplorerprocessing2;

import g4p_controls.GAbstractControl;
import g4p_controls.GButton;
import g4p_controls.GPanel;

import java.util.ArrayList;

import processing.core.PApplet;

public class PromtNewFile extends GPanel{
	PApplet p5;
	
	ArrayList<GAbstractControl> controls = new ArrayList<GAbstractControl>();
	
	GPanel pnl;
	GButton btnSave;
	GButton btnCancel;
	
	
	public PromtNewFile(PApplet p5){
		super(p5, 0, 0, 400, 200, "Set Attributes");
		
		this.p5 = p5;
		createSavePromt();
	}
	
	public void createSavePromt(){
//		String save = "speichern";
		
		btnSave = new GButton(p5, 0, 0, 120, 40, "save");
		btnCancel = new GButton(p5,0, 0, 120, 40, "cancel");

		this.addControl(btnSave, 10, 40);
		this.addControl(btnCancel, 10, 80);
		
		this.setCollapsed(false);
		
	}
	
//	public void dispose(){
//		super.dispose();
//	}

}

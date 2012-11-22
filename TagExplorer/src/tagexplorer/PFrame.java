package tagexplorer;

import java.awt.Frame;

import processing.core.PApplet;

public class PFrame extends Frame {
	NewApplet s;
	
	public PFrame(PApplet p5) {
        setBounds(100,100,400,300);
        s = new NewApplet();
        add(s);
        s.init();
        setVisible(true);
    }
}

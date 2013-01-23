package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PConstants;

import toxi.physics.VerletParticle;

public class Tag extends VerletParticle {
	int id;
	String name;
	String type = "tag";
//	TagField tagField;
	int bindCount = 0;
	
	Timestamp lastStartFilterTime = null;
	Timestamp lastEndFilterTime = null;
	
	public Tag(){
		super(0, 0, 0);
	}
	
	public Tag(String tableName, int id, String name){
		this();
		this.id = id;
		this.name = name;
		type = tableName;
		//this.tagField = new TagField(p5);
		
	}
	
	public int getWidth(TagExplorerProcessing2 p5, Tag_File file){
		p5.textFont(p5.font, 13);
		int w = (int) p5.textWidth(this.name) + 25;
		return w;
	}
	
	public int renderTag(TagExplorerProcessing2 p5, Tag_File file, int x, int y) {

		int w = (int) p5.textWidth(this.name) + 25;
		int h = 22;
		p5.fill(p5.cButtonBright);
		p5.stroke(p5.cBorder);
		p5.rect((int)x, (int)y, (int)w, (int)h);
		p5.fill(p5.cFont);
		p5.textAlign(PConstants.LEFT, PConstants.CENTER);
		p5.text(this.name, x + 5, y + h / 2);

		Button_Symbol b = new Button_Symbol(p5, "close", x + w - p5.close.width - 5, y + h / 2
				- p5.close.height / 2);
		b.render();

		if (p5.mouseActive && b.mouseOver() && p5.mousePressed) {
			// lŠsst sich nicht wŠhrend scheifendurchlauf lšschen -> passiert nach rendert Tag in HoverPlane.render()
			//file.attributeBindings.remove(this);
			p5.SQL.unbindTag(file, this);
		}

		return w;
	}
	
//	public void updateFileBinding(){
//		
//	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", type=" + type + ", x="
				+ x + ", y=" + y + ", z=" + z + "]";
	}
}

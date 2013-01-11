package tagexplorerprocessing2;

import toxi.physics.VerletParticle;

public class Tag extends VerletParticle {
	int id;
	String name;
	String type = "tag";
//	TagField tagField;
	int bindCount = 0;
	
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
	
	
	
	public int renderTag(TagExplorerProcessing2 p5, Tag_File file, int x, int y) {

		int w = (int) p5.textWidth(this.name) + 25;
		int h = 22;
		p5.fill(150);
		p5.rect(x, y, w, h);
		p5.fill(0);
		p5.textAlign(p5.LEFT, p5.CENTER);
		p5.text(this.name, x + 5, y + h / 2);

		Button_Symbol b = new Button_Symbol(p5, p5.closeImg, x + w - p5.closeImg.width - 5, y + h / 2
				- p5.closeImg.height / 2);
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

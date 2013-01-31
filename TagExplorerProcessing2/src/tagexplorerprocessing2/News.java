package tagexplorerprocessing2;

import java.sql.Timestamp;

import processing.core.PConstants;
import processing.core.PImage;

public class News {
	TagExplorerProcessing2 p5;
	Timestamp ts;
	String type; // symbol
	Tag_User user;
	String comment;

	float x;
	float y;
	int w = 100;
	int wOpen = 400;
	int h = 60;

	public News(TagExplorerProcessing2 p5, Timestamp ts) {
		this.p5 = p5;
		this.ts = ts;
		this.type = generateType();
		this.user = generateUser();
		this.comment = generateComment();
	}

	public News(Timestamp ts, String type) {
		this.ts = ts;
		this.type = type;
		this.user = generateUser();
		this.comment = generateComment();
	}

	public News(Timestamp ts, String type, Tag_User user, String comment) {
		this.ts = ts;
		this.type = type;
		this.user = user;
		this.comment = comment;
	}

	
	
	public void render(float x, float y) {
		this.x = x;
		this.y = y;
		
		//float aktW = w;
		
		p5.noStroke();

		if (mouseOver()) {
			//aktW = wOpen;
			p5.fill(p5.cBorderHover);
			p5.rect(x, y, wOpen, h);
			
			p5.stroke(255);
			p5.line(w, y+22, wOpen - 18, y+22);
			
			p5.fill(255);
			p5.textFont(p5.font, 13);
			p5.textAlign(PConstants.RIGHT, PConstants.BOTTOM);
			
			// datum
			p5.sdf.applyPattern("dd. MM. yyyy  HH:mm:ss");
			p5.text(p5.sdf.format(ts), x + wOpen - 18, y + 19);
			
			p5.textAlign(PConstants.LEFT, PConstants.BOTTOM);
			p5.text(user.name, x + w, y + 19);
			
			p5.textFont(p5.font, 16);
			p5.text(comment, x + w, y + 53);
			
			
			
			
		} else{
			p5.fill(p5.cPopUp);
			p5.rect(x, y, w, h);
		}
		p5.stroke(255);
		p5.line(0, y, w - 18, y);

		// p5.textAlign(PConstants.CENTER);

		p5.sdf.applyPattern("dd. MM. yyyy");

		p5.imageMode(PConstants.CENTER);
		p5.image(getTypeImage(type), x + w / 2, y + 20, 36, 36);

		p5.textAlign(p5.CENTER, p5.BOTTOM);
		p5.textFont(p5.font, 13);
		p5.fill(255);
		p5.text(p5.sdf.format(ts), x + w / 2, y + 53);
	}

	String[] names = {"Herbert Fischer", "Stefan HŸttel", "Elke Schmitt", "Friedrich Meier", "Peter Schneider"};
	public Tag_User generateUser() {
		Tag_User user = new Tag_User("users", 0, names[(int) p5.random(0, names.length - 0.1f)], "pw");
		return user;
	}

	String[] types = { "call", "message", "file", "appointment" };

	public String generateType() {
		String type = types[(int) p5.random(0, types.length - 0.1f)];
		return type;
	}

	String[] comments = {"Ich habe den Datensatz aktualisiert", "Bitte freigeben", "Zwischenstand", "Feedback zum Projekt"};
	public String generateComment() {
		String comment = comments[(int) p5.random(0, comments.length - 0.1f)];
		return comment;
	}

	private PImage getTypeImage(String type) {
		PImage typeImage;

		switch (type) {
		case "call":
			typeImage = p5.newsCall;
			break;
		case "message":
			typeImage = p5.newsMessage;
			break;
		case "file":
			typeImage = p5.newsFile;
			break;
		case "appointment":
			typeImage = p5.newsAppointment;
			break;
		default:
			typeImage = p5.newsCall;
			break;
		}
		return typeImage;
	}
	
	public boolean mouseOver() {
		boolean over = false;
		int zusatz = 0;
		// referenz links oben
		if (p5.mouseX >= x && p5.mouseX < x + w && p5.mouseY > y - zusatz && p5.mouseY < y + h + zusatz) {
			over = true;
		}
		return over;
	}

}

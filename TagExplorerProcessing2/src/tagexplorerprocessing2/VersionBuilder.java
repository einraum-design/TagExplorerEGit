package tagexplorerprocessing2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VersionBuilder extends Thread {

	static String versionsVerzeichnis = "/Users/manuel/Documents/Testumgebung/Sicherung/";

	public VersionBuilder() {

	}

	String path;
	int nr;
	boolean started = false;

	public VersionBuilder(String path, int nr) {
		this.nr = nr;
		this.path = path;
	}

	public void start() {
			// Print messages
			System.out.println("start run method");
			// Do whatever start does in Thread, don't forget this!
			super.start();
	}

	public void run() {
		createVersion(path, nr);
	}

	public void createVersion(String path, int nr) {
		File file = new File(path);
		String target = versionsVerzeichnis + String.format("%06d", nr)
				+ file.getName();
		copy(file, target);
	}

	public void copy(File source, String target) {
		File inputFile = source;

		if (inputFile.isFile()) {
			File outputFile = new File(target);

			FileReader in;
			FileWriter out = null;

			try {
				in = new FileReader(inputFile);
				out = new FileWriter(outputFile);
				int c;

				while ((c = in.read()) != -1)
					out.write(c);

				in.close();
				out.close();
				System.out.println("File Saved! - " + outputFile.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		quit();
	}

	// Our method that quits the thread
	public void quit() {
		System.out.println("Quitting.");
		// IUn case the thread is waiting. . .
		interrupt();
	}
}

package tagexplorerprocessing2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VersionBuilder{

	static String versionsVerzeichnis = "/Users/manuel/Documents/Testumgebung/Sicherung/";

	public VersionBuilder() {
		
	}

	public VersionBuilder(String versionsVerzeichnis) {
		this.versionsVerzeichnis = versionsVerzeichnis;
	}

	public static void createVersion(String path, int nr) {
		File file = new File(path);
			String target = versionsVerzeichnis + String.format("%04d", nr)
					+ file.getName();
			copy(file, target);
	}

	public static void copy(File source, String target) {
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
	}
}

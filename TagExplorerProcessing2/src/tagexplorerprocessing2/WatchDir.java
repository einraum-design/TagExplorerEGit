package tagexplorerprocessing2;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PGraphics;
import processing.core.PImage;

import tagexplorerprocessing2.Connection.ConnectionType;

public class WatchDir extends Thread {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	TagExplorerProcessing2 p5;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	// thread methods
	// Overriding "start()"
	public void start() {
		// Print messages
		System.out.println("start run method");
		// Do whatever start does in Thread, don't forget this!
		super.start();
	}

	// Our method that quits the thread
	public void quit() {
		System.out.println("Quitting.");
		// IUn case the thread is waiting. . .
		interrupt();
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	SQLhelper SQL;

	WatchDir(TagExplorerProcessing2 p5, Path dir, boolean recursive) throws IOException {
		this.p5 = p5;
		SQL = new SQLhelper(p5);
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		if (recursive) {
			System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			System.out.println("Done.");
		} else {
			register(dir);
		}

		// enable trace after initial registration
		this.trace = true;
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	// renamed processEvents
	public void run() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);
				// System.out.println(event.kind().type().toString());

				// EVENTS to do's
				if (event.kind() == ENTRY_MODIFY) {
					createNewTagFileVersion(child);
				} else if (event.kind() == ENTRY_CREATE) {
					createNewTagFile(child);
				} else if (event.kind() == ENTRY_DELETE) {
					setTagFileDead(child);
				}

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	ArrayList<VersionBuilder> vbList = new ArrayList<VersionBuilder>();

	public void createNewTagFile(Path path) {
		// check if isFile and not Directory, dann recursive durchsuchen.
		ifDirectoryRecursive(path);

		String fileName = path.getFileName().toString();

		if (!isRegularFile(path)) {
			System.out.println("ignore " + fileName + " in WatchDir.createNewTagFile(). Not a file.");
			return;
		}

		Tag_File file = p5.createNewFile("files", path);

		if (file != null) {
			new VersionBuilder(path.toString(), file.id).start();

			// create Image if image
			if (file instanceof Tag_File_Image) {
				savePrevImage((Tag_File_Image) file);
			}

			// add all current filters
			for (Filter filter : p5.filterList) {
				if (filter.inOut) {
					SQL.bindTag(file, filter.tag);
					System.out.println("bind to new File: " + filter.tag.name);
				}
			}

			// update File
			p5.updateFileTagBinding(file);
			p5.updateFileFileBinding(file);
			p5.updateVersionBinding(file);

			file.setTextur(null);
			file.updateViewName();
			// muss vor files.add erzeugt werden!
			// p5.generateShape(file);
			p5.files.add(file);
			p5.updateShowFiles();
			p5.updateTags();
			p5.updateSprings();
		}
	}

	public void createNewTagFileVersion(Path path) {
		// check if isFile and not Directory, dann rekursiv durchsuchen.
		// ifDirectoryRecursive(path);

		String fileName = path.getFileName().toString();
		if (!isRegularFile(path)) {
			System.out.println("ignore " + fileName + " in WatchDir.createNewTagFile(). Not a file.");
			return;
		}

		String s = path.toString().trim();
		Tag_File file = (Tag_File) SQL.createDbTag("files", s);

		// set Origin get origin ID und setzes sie als origin
		// origin File SQL.inDataBase(tableName, s)

		Tag_File parent = getParent(file);

		System.out.println("createNewTagFileVersion(): get parent: " + parent.name + " "
				+ parent.attributeBindings.toString() + " " + parent.id);
		for (Tag t : parent.attributeBindings) {
			SQL.bindTag(file, t);
		}

		SQL.bindFile(parent, file, ConnectionType.VERSION);
		parent.setVersionBinding(file);

		file.parent_ID = parent.id;

		if (parent.origin_ID == 0) {
			file.origin_ID = parent.id;
		} else {
			file.origin_ID = parent.origin_ID;
		}

		SQL.setDBOrigin(file);
		SQL.setDBParent(file);

		// if (file != null) {
		new VersionBuilder(path.toString(), file.id).start();

		// add all current filters
		for (Filter filter : p5.filterList) {
			if (filter.inOut) {
				SQL.bindTag(file, filter.tag);
				System.out.println("bind to new File: " + filter.tag.name);
			}
		}

		// x + y von parent;
		// file.x = parent.x;
		// file.y = parent.y;

		System.out.print(file.toString());

		// update File
		file.updateViewName();
		// muss vor files.add erzeugt werden!
		p5.updateFileTagBinding(file);
		p5.updateFileFileBinding(file);
		p5.updateVersionBinding(file);
		p5.files.add(file);
		p5.updateShowFiles();
		p5.updateTags();
		p5.updateSprings();
		System.out.print(file.toString());
		// }
	}

	private void ifDirectoryRecursive(Path path) {
		File f = new File(path.toString());

		if (f.isDirectory()) {
			File[] file_ref = f.listFiles();
			for (int i = 0; i < file_ref.length; i++) {
				createNewTagFile(file_ref[i].toPath());
			}
		}
	}

	private boolean isRegularFile(File file_ref) {
		boolean regularFile = false;
		if (file_ref.isFile()) {
			String temp = file_ref.getName();
			// check if link or unvisible File
			if (temp.lastIndexOf(".") >= 0 && !temp.startsWith(".")) {
				regularFile = true;
			}
		}
		return regularFile;
	}

	private boolean isRegularFile(Path path) {
		File file_ref = new File(path.toString());
		return isRegularFile(file_ref);
	}

	public void setTagFileDead(Path path) {
		ArrayList<Tag_File> files = new ArrayList<Tag_File>();
		for (Tag f : p5.files) {
			Tag_File file = (Tag_File) f;
			if (file.path.equals(path.toString())) {
				files.add(file);
			}
		}

		// nur delete Time von neuester Version setzen
		Tag_File newest = files.get(0);
		for (Tag_File file : files) {
			if (file.id > newest.id) {
				newest = file;
			}
		}

		// System.out.println("file.name: " + file.name);
		newest.setDeleteTime(new Timestamp(System.currentTimeMillis()));
		SQL.setDBDeletTime(newest);

		newest.setShape(p5.generateAccessShape(newest));
	}

	public Tag_File getParent(Tag_File file) {
		Tag_File parent = null;

		ArrayList<Tag_File> parents = new ArrayList<Tag_File>();
		for (Tag t : p5.files) {
			if (t instanceof Tag_File) {
				// path!
				if (((Tag_File) t).path.trim().toLowerCase().equals(file.path.trim().toLowerCase())) {
					parents.add((Tag_File) t);
				}
			}
		}

		parent = parents.get(0);
		if (parents.size() > 1) {
			for (Tag_File tag : parents) {
				if (tag.creation_time.after(parent.creation_time)) {
					parent = tag;
				}
			}
		}
		return parent;
	}

	void savePrevImage(Tag_File_Image file) {
		int w = 120;
		int h = 80;
		savePrevImage(file, w, h);
	}

	void _savePrevImage(Tag_File_Image file, int w, int h) {

		// System.out.println("AUSKOMMENTIERT Couldn't save: " +
		// VersionBuilder.versionsVerzeichnis + "prev" + String.format("%06d",
		// file.id) + file.name.substring(0, file.name.lastIndexOf('.')) +
		// ".png");

		try {
			PImage img = p5.loadImage(file.path);

			PGraphics pg = p5.createGraphics(w, h, p5.P3D);
			pg.imageMode(p5.CENTER);

			pg.beginDraw();
			pg.background(0, 0);

			if (w / h > img.width / img.height) {
				pg.image(img, pg.width / 2, pg.height / 2, ((float) pg.height / img.height) * img.width, pg.height);
			} else {
				pg.image(img, pg.width / 2, pg.height / 2, pg.width, ((float) pg.width / img.width) * img.height);
			}

			pg.endDraw();
			pg.save(VersionBuilder.versionsVerzeichnis + "prev" + String.format("%06d", file.id)
					+ file.name.substring(0, file.name.lastIndexOf('.')) + ".png");
			System.out.println("saved :" + VersionBuilder.versionsVerzeichnis + "prev" + String.format("%06d", file.id)
					+ file.name.substring(0, file.name.lastIndexOf('.')) + ".png");
		} catch (Exception e) {
			System.out.println("Couldn't save: " + VersionBuilder.versionsVerzeichnis + "prev"
					+ String.format("%06d", file.id) + file.name.substring(0, file.name.lastIndexOf('.')) + ".png");
		}
	}

	void savePrevImage(Tag_File_Image file, int w, int h) {

		System.out.println("AUSKOMMENTIERT Couldn't save: " + VersionBuilder.versionsVerzeichnis + "prev"
				+ String.format("%06d", file.id) + file.name.substring(0, file.name.lastIndexOf('.')) + ".png");

		// try {
		// PImage img = p5.loadImage(file.path);
		// img.resize((int) ((float) h / img.height) * img.width, h);

		//
		// if (w / h > img.width / img.height) {
		// img.resize((int) ((float) h / img.height) * img.width, h);
		//
		// } else {
		// img.resize(w, (int) ((float) w / img.width) * img.height);
		// }
		// img.save(VersionBuilder.versionsVerzeichnis + "prev" +
		// String.format("%06d", file.id)
		// + file.name.substring(0, file.name.lastIndexOf('.')) + ".png");

		// PGraphics pg = p5.createGraphics(w, h, p5.P3D);
		// pg.imageMode(p5.CENTER);
		//
		// pg.beginDraw();
		// pg.background(0, 0);
		//
		// if (w / h > img.width / img.height) {
		// pg.image(img, pg.width / 2, pg.height / 2, ((float) pg.height /
		// img.height) * img.width, pg.height);
		// } else {
		// pg.image(img, pg.width / 2, pg.height / 2, pg.width, ((float)
		// pg.width / img.width) * img.height);
		// }
		//
		// pg.endDraw();
		// pg.save(VersionBuilder.versionsVerzeichnis + "prev" +
		// String.format("%06d", file.id) + file.name.substring(0,
		// file.name.lastIndexOf('.')) + ".png");
		// System.out.println("saved :" + VersionBuilder.versionsVerzeichnis +
		// "prev" + String.format("%06d", file.id)
		// + file.name.substring(0, file.name.lastIndexOf('.')) + ".png");
		// } catch (Exception e) {
		// System.out.println("Couldn't save: " +
		// VersionBuilder.versionsVerzeichnis + "prev"
		// + String.format("%06d", file.id) + file.name.substring(0,
		// file.name.lastIndexOf('.')) + ".png");
		// }
	}

	/*
	 * static void usage() {
	 * System.err.println("usage: java WatchDir [-r] dir"); System.exit(-1); }
	 */

	// public static void main(String[] args) throws IOException {
	// // parse arguments
	// /*
	// if (args.length == 0 || args.length > 2)
	// usage();
	// boolean recursive = false;
	// int dirArg = 0;
	// if (args[0].equals("-r")) {
	// if (args.length < 2)
	// usage();
	// recursive = true;
	// dirArg++;
	// }
	// */
	//
	// // register directory and process its events
	// //Path dir = Paths.get(args[dirArg]);
	// //new WatchDir(dir, recursive).processEvents();
	//
	// Path p =
	// FileSystems.getDefault().getPath("/Users/manuel/Documents/Testumgebung/Test");
	// new WatchDir(p, true).processEvents();
	// }
}

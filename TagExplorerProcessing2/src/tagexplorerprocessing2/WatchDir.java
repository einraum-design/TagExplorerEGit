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
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);
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
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
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

	WatchDir(TagExplorerProcessing2 p5, Path dir, boolean recursive)
			throws IOException {
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
				WatchEvent.Kind kind = event.kind();

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

		String fileName = path.getFileName().toString();

		if (fileName.startsWith(".")) {
			System.out.println("ignore " + fileName
					+ " in WatchDir.createNewTagFile(). Not a file.");
			return;
		}

		Tag_File file = p5.createNewFile("files", path);

		if (file != null) {
			vbList.add(new VersionBuilder(path.toString(), file.id));
			for(VersionBuilder vb : vbList){
				vb.start();
			}
			
			//VersionBuilder.createVersion(path.toString(), file.id);
			
			if (p5.user != null) {
				// System.out.println(file.toString());
				SQL.bindTag(file, p5.user);
			}

			// update File
			file.setAttributes(SQL.getBindedTagList(file));
			file.updateViewName();
			p5.files.add(file);
			p5.updateShowFiles();
			p5.updateSprings();
		}
	}
	
	
	public void createNewTagFileVersion(Path path) {

		String fileName = path.getFileName().toString();

		if (fileName.startsWith(".")) {
			System.out.println("ignore " + fileName
					+ " in WatchDir.createNewTagFile(). Not a file.");
			return;
		}

		String s = path.toString().trim();
		Tag_File file  = (Tag_File) SQL.createDbTag("files", s);
		
		
		// set Origin get origin ID und setzes sie als origin
		// origin File SQL.inDataBase(tableName, s)
		
		Tag_File parent = SQL.getParent(file);
		
		for(Tag t : parent.attributes){
			SQL.bindTag(file, t);			
		}
		file.setAttributes(SQL.getBindedTagList(file));
		file.updateViewName();
		
		file.parent_ID = parent.id;
		
		if(parent.origin_ID == 0){
			file.origin_ID = parent.id;
		}
		
		SQL.setDBOrigin(file);
		SQL.setDBParent(file);


		if (file != null) {
			new VersionBuilder(path.toString(), file.id).start();
//			for(VersionBuilder vb : vbList){
//				if(!vb.started){
//					vb.start();
//				}
//			}
						
			if (p5.user != null) {
				// System.out.println(file.toString());
				SQL.bindTag(file, p5.user);
			}

			// update File
			file.setAttributes(SQL.getBindedTagList(file));
			file.updateViewName();
			p5.files.add(file);
			p5.updateShowFiles();
			p5.updateSprings();
		}
	}

	public void setTagFileDead(Path path) {
		for (Tag f : p5.files) {
			Tag_File file = (Tag_File) f;

			if (file.path.equals(path.toString())) {
				System.out.println("file.name: " + file.name);
				file.setDeletTime(new Timestamp(System.currentTimeMillis()));
				SQL.setDBDeletTime(file);
			}

		}
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

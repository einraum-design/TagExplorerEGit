package tagexplorerprocessing2;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TreeSet;

import processing.core.PImage;

import tagexplorerprocessing2.Connection.ConnectionType;
import tagexplorerprocessing2.Tag_File.FileType;

import de.bezier.data.sql.MySQL;

public class SQLhelper {
	TagExplorerProcessing2 p5;

	MySQL msql;
	MySQL msql2;
	String user = "root";
	String pass = "root";
	String database = "files_db";
	String host = "localhost:8889";

	// HashMap<String, String> queries = new HashMap<String, String>();
	// queries.put("files",
	// "ID, name, size, path, creation_time, expiration_time, origin_ID, score");

	public SQLhelper(TagExplorerProcessing2 p5) {
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		msql.connect();

		msql2 = new MySQL(p5, host, database, user, pass);
		msql2.connect();
		System.out.println("SQL connection: " + checkConnection());
	}

	public SQLhelper(TagExplorerProcessing2 p5, String user, String pass, String database, String host) {
		this.user = user;
		this.pass = pass;
		this.database = database;
		this.host = host;
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		msql.connect();
		msql2 = new MySQL(p5, host, database, user, pass);
		msql2.connect();
		System.out.println("SQL connection: " + checkConnection());
	}

	boolean checkConnection() {
		boolean connected = true;

		// if (msql.connect()) {
		// connected = true;
		// // System.out.println("SQL connected");
		// }
		return connected;
	}

	boolean checkConnection(MySQL msql) {
		boolean connected = true;

		// if (msql.connect()) {
		// connected = true;
		// // System.out.println("SQL connected");
		// }
		return connected;
	}

	// ALLE FELDER UND FELDTYPEN EINER TABELLE ABFRAGEN
	// ArrayList<SQLTableInfo> tableInfo = getTableFields("files");
	// for (SQLTableInfo info : tableInfo) {
	// queryFields += " " + info.name;
	// }
	public ArrayList<SQLTableInfo> getTableFields(String tableName) {
		ArrayList<SQLTableInfo> tableInfo = new ArrayList<SQLTableInfo>();

		// String query =
		// "SELECT 'COLUMN_NAME' FROM 'INFORMATION_SCHEMA'.'COLUMNS' WHERE 'TABLE_SCHEMA'='files_db' AND 'TABLE_NAME'='"
		// + tableName + "'";
		String query = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='files_db' AND TABLE_NAME='"
				+ tableName + "'";

		if (checkConnection()) {
			msql.query(query);
			while (msql.next()) {
				tableInfo.add(new SQLTableInfo(msql.getString(1), msql.getString(2)));
				// System.out.println(msql.getString(1)); //COLUMN_NAME
				// System.out.println(msql.getString(2)); //DATA_TYPE
			}
		} else {
			System.out.println("not Connected listUsers()");
		}

		return tableInfo;
	}

	// ALLE TAGS DER TABELLE ABFRAGEN
	public ArrayList<Tag> queryTagList(String tableName) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {
			// String queryFields = queries.get(tableName);
			// msql.query("SELECT (" + queryFields + ") FROM " + tableName);
			msql.query("SELECT * FROM " + tableName + " ORDER BY ID ASC");

			while (msql.next()) {
				Tag t = getSpecificTags(tableName);
				if (t != null) {
					tags.add(t);
				}
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}

	// TAGS DER TABELLE GEFILTERT ABFRAGEN
	// Ein Filter
	public ArrayList<Tag> queryTagListFiltered(String tableName, Filter filter) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {

			String selectString = "SELECT " + tableName + ".* FROM " + tableName + " INNER JOIN tag_binding ON ("
					+ tableName + ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '" + filter.tag.type
					+ "' AND tag_binding.tag_ID LIKE '" + filter.tag.id + "'";
			String selectStringId = "SELECT " + tableName + ".ID FROM " + tableName + " INNER JOIN tag_binding ON ("
					+ tableName + ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '" + filter.tag.type
					+ "' AND tag_binding.tag_ID LIKE '" + filter.tag.id + "'";

			if (filter.inOut) {
				msql.query(selectString);
			} else {
				// Funktioniert noch nicht richtig! Es werden nur Dateien
				// gewählt, die ein tag binding mit dem filtertyp haben. andere
				// werden ignoriert
				msql.query("SELECT " + tableName + ".* FROM " + tableName + " WHERE " + tableName + ".ID NOT IN ("
						+ selectStringId + ")");
				// + "WHERE tag_binding.type NOT LIKE '" + filter.tag.type +
				// "' OR (tag_binding.type LIKE '" + filter.tag.type +
				// "' AND tag_binding.tag_ID NOT LIKE '" + filter.tag.id +
				// "')");
			}
			while (msql.next()) {
				Tag t = getSpecificTags(tableName);
				if (t != null) {
					tags.add(t);
				}
			}
		} else {
			System.out.println("not Connected queryTagListFiltered()");
		}
		return tags;
	}

	// funktioniert nur für filter.inOut = true
	public ArrayList<Integer> queryIDsFiltered(String tableName, Filter filter) {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		if (checkConnection()) {

			String selectStringId = "SELECT " + tableName + ".ID FROM " + tableName + " INNER JOIN tag_binding ON ("
					+ tableName + ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '" + filter.tag.type
					+ "' AND tag_binding.tag_ID LIKE '" + filter.tag.id + "'";

			System.out.println(selectStringId);
			msql.query(selectStringId);

			while (msql.next()) {
				ids.add(msql.getInt("ID"));
			}
		} else {
			System.out.println("not Connected queryTagListFiltered()");
		}
		return ids;
	}

	// Filter List on files IDs der selected und nicht selected werden ge-merged
	public ArrayList<Tag> queryTagListFiltered(String tableName, ArrayList<Filter> filterList) {

		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {

			TreeSet<Integer> selected = new TreeSet<Integer>();
			TreeSet<Integer> deselected = new TreeSet<Integer>();

			for (Filter f : filterList) {
				if (f.inOut) {
					selected.addAll(queryIDsFiltered(tableName, f));
				} else {
					deselected.addAll(queryIDsFiltered(tableName, f));
				}
			}

			// entferne die deselected
			for (int i : deselected) {
				selected.remove(i);
			}

			String query = "SELECT * FROM " + tableName + " WHERE ";

			boolean first = true;
			for (int id : selected) {
				if (!first) {
					query += " OR ";
				}

				query += "ID = '" + id + "'";
				first = false;
			}
			
			
			if(!query.equals("SELECT * FROM " + tableName + " WHERE ")){
				msql.query(query);
	
				// get Tags
				while (msql.next()) {
					Tag t = getSpecificTags(tableName);
					if (t != null) {
						tags.add(t);
					}
				}
			} else{
				// wenn keine übrig bleiben!
				System.out.println("queryTagListFiltered(): Filter ergeben keinen Treffer!");
			}
		} else {
			System.out.println("not Connected queryTagListFiltered()");
		}

		System.out.println("return " + tags.size() + " tags from queryTagListFiltered()");
		return tags;
	}

	// alle verknüpften Tags finden
	public ArrayList<Tag> getBindedTagList(Tag_File file) {
		ArrayList<Tag> tagList = new ArrayList<Tag>();

		if (checkConnection()) {

			msql.query("SELECT type, tag_ID FROM tag_binding WHERE file_ID = " + file.id);
			ArrayList<String> types = new ArrayList<String>();
			ArrayList<Integer> tagIds = new ArrayList<Integer>();
			while (msql.next()) {
				types.add(msql.getString("type"));
				tagIds.add(msql.getInt("tag_ID"));
			}

			for (int i = 0; i < types.size(); i++) {
				msql.query("SELECT * FROM " + types.get(i) + " WHERE ID = " + tagIds.get(i));
				while (msql.next()) {
					Tag t = getSpecificTags(types.get(i));
					if (t != null) {
						tagList.add(t);
					}
				}
			}

		} else {
			System.out.println("not Connected getBindedTagList()");
		}

		return tagList;
	}

	// alle verknüpften Files finden
	public ArrayList<Tag_File> getBindedFileList(Tag_File file, ConnectionType type) {
		ArrayList<Tag_File> fileList = new ArrayList<Tag_File>();

		if (checkConnection()) {

			msql.query("SELECT file2_ID FROM file_binding WHERE type = \"" + type.toString() + "\" && file1_ID = "
					+ file.id);
			ArrayList<Integer> fileIds = new ArrayList<Integer>();
			while (msql.next()) {
				fileIds.add(msql.getInt("file2_ID"));
			}

			for (int i = 0; i < fileIds.size(); i++) {
				msql.query("SELECT * FROM files WHERE ID = " + fileIds.get(i));
				while (msql.next()) {
					Tag_File t = (Tag_File) getSpecificTags("files");
					if (t != null) {
						fileList.add(t);
					}
				}
			}

		} else {
			System.out.println("not Connected getBindedTagList()");
		}

		return fileList;
	}

	// not finished, get Ids and types
	public ArrayList<Tag> queryConnectedTagList(String tableName, Tag_File t) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {
			msql.query("SELECT tag_ID, type FROM " + tableName + " WHERE file_ID = " + t.id);

			ArrayList tagIds = new ArrayList();
			while (msql.next()) {
				tagIds.add(msql.getInt("tag_ID"));
				tagIds.add(msql.getString("type"));
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}

	// /////////// Tag in Datenbank eintragen ////////////////
	public Tag createDbTag(String tableName, String s) {
		// keywords or projects
		if (tableName.equals("keywords") || tableName.equals("projects")) {
			msql.execute("INSERT INTO " + tableName + " (name) VALUES (\"" + s + "\")");
			System.out.println("Keyword " + s + " registered");
			return getLastCreatedTag(tableName);
		}
		// locations
		else if (tableName.equals("locations")) {
			String locationName = s;
			String coordinates = "46.39342, 2.2134";
			msql.execute("INSERT INTO " + tableName + " (name, coordinates) VALUES (\"" + locationName + "\", \" "
					+ coordinates + "\")");
			System.out.println("Location " + locationName + " registered");
			return getLastCreatedTag(tableName);
		}
		// files
		else if (tableName.equals("files")) {
			Path file = FileSystems.getDefault().getPath(s);
			BasicFileAttributes attr;
			try {
				attr = Files.readAttributes(file, BasicFileAttributes.class);

				if (!attr.isSymbolicLink()) {
					// add FileType wenn bekannt:
					FileType fileType = getFileType(file.getFileName().toString().trim());

					if (fileType == null) {
						System.out.println("fileType = null extension nicht bekannt, filename: " + file.getFileName());
						msql.execute("INSERT INTO " + tableName + " (name, path, size, creation_time) VALUES (\""
								+ file.getFileName().toString().trim() + "\", \"" + s.trim() + "\", \"" + attr.size()
								+ "\", \"" + new Timestamp(attr.creationTime().toMillis()) + "\")");

						// msql.execute( expiration_time

						// create Accesses Entry last access Time
						setLastAccessTime(attr);
					} else {
						System.out.println(fileType.toString());
						msql.execute("INSERT INTO " + tableName + " (name, type, path, size, creation_time) VALUES (\""
								+ file.getFileName().toString().trim() + "\", \"" + fileType.toString() + "\", \""
								+ s.trim() + "\", \"" + attr.size() + "\", \""
								+ new Timestamp(attr.creationTime().toMillis()) + "\")");

						// create Accesses Entry last access Time
						setLastAccessTime(attr);
					}
					System.out.println("File " + file.getFileName().toString() + " registered in DB");
				} else {
					System.out.println("File " + file.getFileName().toString() + " ist keine Datei, sondern ein Link!");
				}
				return getLastCreatedTag(tableName);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("File not saved");
			}
		} else {
			System.out.println(tableName + "wrong or not yet in SQL.createDBTag");
		}
		return null;
	}

	// public void setLastAccessTime(){
	// if(msql2.connect()){
	// msql2.query("SELECT MAX(ID) FROM files");
	// msql2.next();
	// int fileId = msql2.getInt(1);
	// System.out.println("fileId: " + fileId);
	// msql2.execute("INSERT INTO accesses (fileID, date, comment) VALUES (" +
	// fileId + ", \"" + new Timestamp(attr.lastAccessTime().toMillis()) +
	// "\", \"" + "comment" + "\")");
	// }
	// }

	public void setLastAccessTime(BasicFileAttributes attr) {
		if (msql2.connect()) {
			msql2.query("SELECT MAX(ID) FROM files");
			msql2.next();
			int fileId = msql2.getInt(1);
			System.out.println("fileId: " + fileId);
			if (p5.user != null) {
				msql2.execute("INSERT INTO accesses (fileID, date, comment, userID) VALUES (" + fileId + ", \""
						+ new Timestamp(attr.lastAccessTime().toMillis()) + "\", \"" + "comment" + "\", \""
						+ p5.user.id + "\")");
			} else {
				msql2.execute("INSERT INTO accesses (fileID, date, comment) VALUES (" + fileId + ", \""
						+ new Timestamp(attr.lastAccessTime().toMillis()) + "\", \"" + "comment" + "\")");
			}
		}
	}

	public void setLastAccessTime(Tag_File file) {
		if (msql2.connect()) {

			Path filePath = FileSystems.getDefault().getPath(file.path);
			BasicFileAttributes attr;
			try {
				attr = Files.readAttributes(filePath, BasicFileAttributes.class);

				// wenn lastAccessTime der Datei neuer ist als newest Timestamp
				// des Tag_Files
				if (new Timestamp(attr.lastAccessTime().toMillis()).after(p5.getNewestDate(file))) {

					if (p5.user != null) {
						msql2.execute("INSERT INTO accesses (fileID, date, comment, userID) VALUES (" + file.id
								+ ", \"" + new Timestamp(attr.lastAccessTime().toMillis()) + "\", \"" + "comment"
								+ "\", \"" + p5.user.id + "\")");
					} else {
						msql2.execute("INSERT INTO accesses (fileID, date, comment) VALUES (" + file.id + ", \""
								+ new Timestamp(attr.lastAccessTime().toMillis()) + "\", \"" + "comment" + "\")");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("couldn't reach file to update access time");
			}
		}
	}

	public void setAccessTimeNow(Tag_File file) {
		if (msql2.connect()) {
			//System.out.println("setAccessTimeNow: " + file.name);
			
			Timestamp now = new Timestamp(System.currentTimeMillis());
			
			// letzter zugriff mindestens 5 sekunden her!
			if(now.getTime() - p5.getNewestDate(file).getTime() > 5000){
				System.out.println("setAccessTimeNow: open! " + file.name);
				
				if (p5.user != null) {
					msql2.execute("INSERT INTO accesses (fileID, date, comment, userID) VALUES (" + file.id + ", \""
							+ now + "\", \"" + "comment" + "\", \""
							+ p5.user.id + "\")");
				} else {
					msql2.execute("INSERT INTO accesses (fileID, date, comment) VALUES (" + file.id + ", \""
							+ now + "\", \"" + "per mouseclick" + "\")");
				}
				
				file.addAccess(new Access(now, "per mouseclick"));
				file.setShape(p5.generateShape(file));
				file.z = -p5.timeline.mapExp(p5.getNewestDate(file));
			}

		}
	}

	private FileType getFileType(String fileName) {
		String[] extensions = p5.split(fileName, ".");
		FileType fileType = null;

		if (extensions.length > 1) {
			String extension = extensions[extensions.length - 1];

			for (String ex : p5.imageExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.IMAGE;
					return fileType;
				}
			}

			for (String ex : p5.vectorExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.VECTOR;
					return fileType;
				}
			}

			for (String ex : p5.layoutExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.LAYOUT;
					return fileType;
				}
			}

			for (String ex : p5.audioExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.AUDIO;
					return fileType;
				}
			}

			for (String ex : p5.videoExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.VIDEO;
					return fileType;
				}
			}

			for (String ex : p5.textExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.TEXT;

					System.out.println("TextExtention!");

					return fileType;
				}
			}

			for (String ex : p5.webExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.WEB;
					return fileType;
				}
			}

			for (String ex : p5.fontExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.FONT;
					return fileType;
				}
			}

			for (String ex : p5.messageExtension) {
				if (ex.equals(extension)) {
					fileType = FileType.MESSAGE;
					return fileType;
				}
			}
		}

		return null;
	}

	// TAG BINDING ERSTELLEN
	public void bindTag(Tag_File file, Tag tag) {
		if (checkConnection()) {
			// files
			msql.query("SELECT COUNT(*) FROM tag_binding WHERE file_ID = \"" + file.id + "\" AND type = \"" + tag.type
					+ "\" AND tag_ID = \"" + tag.id + "\"");
			msql.next();
			// System.out.println("number of rows: " + msql.getInt(1));

			if (msql.getInt(1) == 0) {

				msql.execute("INSERT INTO tag_binding (file_ID, type, tag_ID, time) VALUES (\"" + file.id + "\", \""
						+ tag.type + "\", \"" + tag.id + "\", \"" + new Timestamp(System.currentTimeMillis()) + "\")");
				System.out.println("Added Tag Binding");
			} else {
				System.out.println("File-Tag binding exists already");
			}
		}
	}
	
	public void unbindTag(Tag_File file, Tag tag){
		if (checkConnection()) {
			msql.execute("DELETE FROM tag_binding WHERE file_ID = \"" + file.id + "\" AND tag_ID = \"" + tag.id + "\"");
		} else {
			System.out.println("unbindTag() - couldn't connect");
		}
	}

	public void bindFile(Tag_File file1, Tag_File file2, ConnectionType type) {
		if (checkConnection()) {

			// file1 older than file2!
			msql.query("SELECT COUNT(*) FROM file_binding WHERE file1_ID = \"" + file1.id + "\" AND file2_ID = \""
					+ file2.id + "\"");

			msql.next();

			if (msql.getInt(1) == 0) {

				msql.execute("INSERT INTO file_binding (file1_ID, file2_ID, type, time) VALUES (\"" + file1.id
						+ "\", \"" + file2.id + "\", \"" + type.toString() + "\", \""
						+ new Timestamp(System.currentTimeMillis()) + "\")");
				System.out.println("Added File-File Binding");
			} else {
				System.out.println("File-File binding exists already");
			}
		}

	}

	public boolean inDataBase(String tableName, String theText) {
		boolean isInDB = false;

		ArrayList<Tag> tagList = queryTagList(tableName);

		for (Tag t : tagList) {
			if (t instanceof Tag_Location) {
				if (t.name.trim().toLowerCase().equals(theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else if (t instanceof Tag_File) {
				// path!
				if (((Tag_File) t).path.trim().toLowerCase().equals(theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else if (t instanceof Tag) {
				if (t.name.trim().toLowerCase().equals(theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else {
				System.out.println("What kind of Tag is it? in inDataBase()");
			}
			// System.out.println("inDataBase for-schleife" + t.toString());
		}
		return isInDB;
	}

	// noch nicht fertig! - sollte gehen!
	public Tag getLastCreatedTag(String tableName) {
		Tag tag = null;

		// get last ID in table
		msql.query("SELECT MAX(ID) FROM " + tableName);
		msql.next();
		int fileId = msql.getInt(1);

		msql.query("SELECT * FROM " + tableName + " WHERE ID = \"" + fileId + "\"");
		msql.next();

		tag = getSpecificTags(tableName);

		return tag;
	}

	//
	public Tag getSpecificTags(String tableName) {
		Tag t = null;

		// existiert das Attribute in p5.attributes? - Dann gib existierends
		// Attribut
		// zurück
		if (!tableName.equals("files") && p5.attributes != null) {
			for (Tag _tag : p5.attributes) {
				if (tableName.trim().equals(_tag.type.trim()) && msql.getInt("ID") == _tag.id) {
					// System.out.println("übergabe " + _tag.name + " "
					// + _tag.type);
					return _tag;
				}
			}
		}
		// existiert die Datei in p5.files? - Dann gib existierends Attribut
		// zurück
		if (tableName.equals("files") && p5.files != null) {
			for (Tag _tag : p5.files) {
				if (tableName.trim().equals(_tag.type.trim()) && msql.getInt("ID") == _tag.id) {
					// System.out.println("übergabe " + _tag.name + " "
					// + _tag.type);
					return _tag;
				}
			}
		}

		// ansonsten erstellen neuen Tag
		if (tableName.equals("files")) {

			Tag_File tag = null;

			String fileTypeString = msql.getString("type");
			int id = msql.getInt("ID");

			// FileType
			if (fileTypeString != null && fileTypeString.length() > 0) {
				// IMAGE
				if (fileTypeString.equals(FileType.IMAGE.toString())) {
					String name = msql.getString("name");

					String blancName = name.substring(0, name.lastIndexOf('.'));

					PImage img = null;

					try {
						img = p5.loadImage(VersionBuilder.versionsVerzeichnis + "prev" + String.format("%06d", id)
								+ blancName + ".png");
					} catch (Exception e) {
						img = p5.loadImage("versionsVerzeichnis/prev.png");
					}

					tag = new Tag_File_Image(tableName, id, name, msql.getFloat("size"), msql.getString("path"),
							msql.getTimestamp("creation_time"), msql.getTimestamp("expiration_time"),
							msql.getInt("parent_ID"), msql.getInt("origin_ID"), msql.getInt("score"), img);

					tag.setFileType(FileType.IMAGE);
				}
				// Default FileType
				else {
					tag = new Tag_File(tableName, id, msql.getString("name"), msql.getFloat("size"),
							msql.getString("path"), msql.getTimestamp("creation_time"),
							msql.getTimestamp("expiration_time"), msql.getInt("parent_ID"), msql.getInt("origin_ID"),
							msql.getInt("score"));
				}
			}
			// sonstige File
			else {
				tag = new Tag_File(tableName, id, msql.getString("name"), msql.getFloat("size"),
						msql.getString("path"), msql.getTimestamp("creation_time"),
						msql.getTimestamp("expiration_time"), msql.getInt("parent_ID"), msql.getInt("origin_ID"),
						msql.getInt("score"));
			}

			Timestamp ts = msql.getTimestamp("delete_time");
			if (ts != null) {
				tag.setDeleteTime(ts);
			}

			// übergebe alle Accesses
			tag.setAccesses(getFileAccesses(id));

			t = tag;

		} else if (tableName.equals("locations")) {
			Tag tag = new Tag_Location(tableName, msql.getInt("ID"), msql.getString("name"),
					msql.getString("coordinates"));
			t = tag;
		} else if (tableName.equals("users")) {
			Tag_User tag = new Tag_User("users", msql.getInt("ID"), msql.getString("name"), msql.getString("password"));
			t = tag;
		} else if (tableName.equals("projects") || tableName.equals("keywords")) {
			Tag tag = new Tag(tableName, msql.getInt("ID"), msql.getString("name"));
			t = tag;
		} else {
			System.out.println(tableName + " not yet Listed in queryTagList");
		}
		// System.out.println("neu erstellen: " + t.name + " " + t.type);
		return t;
	}

	public void setDBDeletTime(Tag_File file) {
		if (checkConnection()) {
			String s = "UPDATE files SET delete_time = '" + file.delete_time + "' WHERE ID = " + file.id;
			msql.execute(s);
		} else {
			System.out.println("not Connected setDBDeletTime()");
		}
	}

	public void setDBOrigin(Tag_File file) {
		if (checkConnection()) {
			String s = "UPDATE files SET origin_ID = '" + file.origin_ID + "' WHERE ID = " + file.id;
			msql.execute(s);
		} else {
			System.out.println("not Connected setDBOrigin()");
		}
	}

	public void setDBParent(Tag_File file) {
		if (checkConnection()) {
			String s = "UPDATE files SET parent_ID = '" + file.parent_ID + "' WHERE ID = " + file.id;
			msql.execute(s);
		} else {
			System.out.println("not Connected setDBParent()");
		}
	}

	public ArrayList<Access> getFileAccesses(int id) {
		ArrayList<Access> accesses = new ArrayList<Access>();

		if (msql2.connect()) {

			String s = "SELECT * FROM accesses WHERE fileID = \"" + id + "\"";
			msql2.query(s);
			while (msql2.next()) {
				// System.out.println("checkConnection: " +
				// checkConnection(msql2) + " in next()");
				Access c = new Access(msql2.getTimestamp("date"), msql2.getString("comment"));
				accesses.add(c);
			}
			// System.out.println(id + " accesses: " + accesses.size());
		}

		return accesses;
	}
}

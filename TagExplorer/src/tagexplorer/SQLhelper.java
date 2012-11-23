package tagexplorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import de.bezier.data.sql.MySQL;

public class SQLhelper {
	PApplet p5;

	MySQL msql;
	String user = "root";
	String pass = "root";
	String database = "files_db";
	String host = "localhost:8889";

	// HashMap<String, String> queries = new HashMap<String, String>();
	// queries.put("files",
	// "ID, name, size, path, creation_time, expiration_time, origin_ID, score");

	public SQLhelper(PApplet p5) {
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());
	}

	public SQLhelper(PApplet p5, String user, String pass, String database,
			String host) {
		this.user = user;
		this.pass = pass;
		this.database = database;
		this.host = host;
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());
	}

	boolean checkConnection() {
		boolean connected = false;
		if (msql.connect()) {
			connected = true;
			// System.out.println("SQL connected");
		}
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
				tableInfo.add(new SQLTableInfo(msql.getString(1), msql
						.getString(2)));
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
			msql.query("SELECT * FROM " + tableName);

			while (msql.next()) {

				if (tableName.equals("files")) {
					Tag tag = new Tag_File(tableName, msql.getInt("ID"),
							msql.getString("name"), msql.getFloat("size"),
							msql.getString("path"),
							msql.getTimestamp("creation_time"),
							msql.getTimestamp("expiration_time"),
							msql.getInt("origin_ID"), msql.getInt("score"));
					tags.add(tag);
				} else if (tableName.equals("locations")) {
					Tag tag = new Tag_Location(tableName, msql.getInt("ID"),
							msql.getString("name"),
							msql.getString("coordinates"));
					tags.add(tag);
				} else if (tableName.equals("users")) {
					Tag_User user = new Tag_User("users", msql.getInt("ID"),
							msql.getString("name"), msql.getString("password"));
					tags.add(user);
				}
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}

	// not finished, get Ids and types
	public ArrayList<Tag> queryConnectedTagList(String tableName, Tag_File t) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {
			msql.query("SELECT tag_ID, type FROM " + tableName
					+ " WHERE file_ID = " + t.id);

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
			msql.execute("INSERT INTO " + tableName + " (name) VALUES (\"" + s
					+ "\")");
			System.out.println("Keyword " + s + " registered");
			return getLastCreatedTag(tableName);
		}
		// locations
		else if (tableName.equals("locations")) {
			String locationName = s;
			String coordinates = "46.39342, 2.2134";
			msql.execute("INSERT INTO " + tableName
					+ " (name, coordinates) VALUES (\"" + locationName
					+ "\", \" " + coordinates + "\")");
			System.out.println("Location " + locationName + " registered");
			return getLastCreatedTag(tableName);
		}
		// files
		else if (tableName.equals("files")) {
			int index = s.lastIndexOf("/");
			String fileName = s.substring(index + 1);
			Path file = FileSystems.getDefault().getPath(s);
			BasicFileAttributes attr;
			try {
				attr = Files.readAttributes(file, BasicFileAttributes.class);

				if (!attr.isSymbolicLink()) {
					msql.execute("INSERT INTO "
							+ tableName
							+ " (name, path, size, creation_time, expiration_time) VALUES (\""
							+ fileName.trim() + "\", \"" + s.trim() + "\", \""
							+ attr.size() + "\", \""
							+ new Timestamp(attr.creationTime().toMillis())
							+ "\", \""
							+ new Timestamp(attr.lastAccessTime().toMillis())
							+ "\")");
					System.out.println("File " + fileName + " registered");
				} else {
					System.out.println("File " + fileName
							+ " ist keine Datei, sondern ein Link!");
				}
				return getLastCreatedTag(tableName);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("File not saved");
			}
		} else{
			System.out.println(tableName + "wrong or not yet in SQL.createDBTag");
		}
		return null;
	}

	// TAG BINDING ERSTELLEN
	public void bindTag(Tag_File file, Tag tag) {
		if (checkConnection()) {
			// files

			System.out.println("SELECT COUNT(*) FROM tag_binding WHERE file_ID = \""
							+ file.id
							+ "\" AND type = \""
							+ tag.type
							+ "\" AND tag_ID = \"" + tag.id + "\"");
			// ask if COUNT der connection == 0 -> binding exists
			msql.query("SELECT COUNT(*) FROM tag_binding WHERE file_ID = \""
					+ file.id
					+ "\" AND type = \""
					+ tag.type
					+ "\" AND tag_ID = \"" + tag.id + "\"");
			msql.next();
			System.out.println("number of rows: " + msql.getInt(1));

			if (msql.getInt(1) == 0) {

				msql.execute("INSERT INTO tag_binding (file_ID, type, tag_ID, time) VALUES (\""
						+ file.id
						+ "\", \""
						+ tag.type
						+ "\", \""
						+ tag.id
						+ "\", \""
						+ new Timestamp(System.currentTimeMillis())
						+ "\")");
				System.out.println("Added Tag Binding");
			} else {
				System.out.println("File-Tag binding exists already");
			}
		}
	}

	public boolean inDataBase(String tableName, String theText) {
		boolean isInDB = false;

		ArrayList<Tag> tagList = queryTagList(tableName);

		for (Tag t : tagList) {
			if (t instanceof Tag_Location) {
				if (t.name.trim().toLowerCase()
						.equals(theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else if (t instanceof Tag_File) {
				// path!
				if (((Tag_File) t).path.trim().toLowerCase()
						.equals(theText.trim().toLowerCase())) {
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
	
	
	// noch nicht fertig!
	public Tag getLastCreatedTag(String tableName){
		Tag tag = null;
		
		// get last ID in table
		msql.query("SELECT LAST_INSERT_ID() FROM files");
		msql.next();
		int fileId = msql.getInt(1);

		msql.query("SELECT * FROM " + tableName + " WHERE ID = \"" + fileId + "\"");
		msql.next();
		
		// files
		if(tableName.equals("files")){
			tag = new Tag_File("files", msql.getInt("ID"),
					msql.getString("name"), msql.getFloat("size"),
					msql.getString("path"), msql.getTimestamp("creation_time"),
					msql.getTimestamp("expiration_time"), msql.getInt("origin_ID"),
					msql.getInt("score"));

			System.out.println("TAG: " + tag.toString());
		} else if(tableName.equals("files")){
			System.out.println("SQL.getLastCreatedTag noch nicht fertig für " + tableName);
		} else {
			System.out.println("SQL.getLastCreatedTag noch nicht fertig für " + tableName);
		}

		return tag;
	}
	
	public Tag_File getLastFile() {
		Tag_File file = null;

		msql.query("SELECT LAST_INSERT_ID() FROM files");
		msql.next();
		int fileId = msql.getInt(1);

		msql.query("SELECT * FROM files WHERE ID = \"" + fileId + "\"");
		msql.next();

		// int originId = msql.getInt("origin_ID");
		// if(msql.getInt("origin_ID") == 0){
		// originId = fileId;
		// }
		Tag tag = new Tag_File("files", msql.getInt("ID"),
				msql.getString("name"), msql.getFloat("size"),
				msql.getString("path"), msql.getTimestamp("creation_time"),
				msql.getTimestamp("expiration_time"), msql.getInt("origin_ID"),
				msql.getInt("score"));

		System.out.println("TAG: " + tag.toString());

		// int originId = msql.getInt("origin_ID");
		// if(msql.getInt("origin_ID") == 0){
		// originId = fileId;
		// }
		//
		// int expirationTime =
		// msql.getTimestamp("expiration_time").getSeconds();
		// if(msql.getTimestamp("expiration_time") == null){
		// expirationTime = msql.getTimestamp(System.get).getSeconds();
		// }
		//
		// file = new Tag_File("files", msql.getInt("ID"),
		// msql.getString("name"), msql.getFloat("size"),
		// msql.getString("path"),
		// msql.getTimestamp("creation_time").getSeconds(), expirationTime,
		// msql.getInt("origin_ID"),
		// msql.getInt("score"));

		return file;
	}
}

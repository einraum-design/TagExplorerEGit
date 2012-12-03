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
	TagExplorer p5;

	MySQL msql;
	String user = "root";
	String pass = "root";
	String database = "files_db";
	String host = "localhost:8889";

	// HashMap<String, String> queries = new HashMap<String, String>();
	// queries.put("files",
	// "ID, name, size, path, creation_time, expiration_time, origin_ID, score");

	public SQLhelper(TagExplorer p5) {
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());
	}

	public SQLhelper(TagExplorer p5, String user, String pass, String database,
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

			String selectString = "SELECT "
					+ tableName
					+ ".* FROM "
					+ tableName
					+ " INNER JOIN tag_binding ON ("
					+ tableName
					+ ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '"
					+ filter.tag.type + "' AND tag_binding.tag_ID LIKE '"
					+ filter.tag.id + "'";
			String selectStringId = "SELECT "
					+ tableName
					+ ".ID FROM "
					+ tableName
					+ " INNER JOIN tag_binding ON ("
					+ tableName
					+ ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '"
					+ filter.tag.type + "' AND tag_binding.tag_ID LIKE '"
					+ filter.tag.id + "'";

			if (filter.inOut) {
				msql.query(selectString);
			} else {
				// Funktioniert noch nicht richtig! Es werden nur Dateien
				// gewählt, die ein tag binding mit dem filtertyp haben. andere
				// werden ignoriert
				msql.query("SELECT " + tableName + ".* FROM " + tableName
						+ " WHERE " + tableName + ".ID NOT IN ("
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

	// Filter List on files
	// not ready UNION ist falser WEG -> müssen IN sein
	public ArrayList<Tag> queryTagListFiltered(String tableName,
			ArrayList<Filter> filterList) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {

			// built queryString

			String inIdSelection = "";

			ArrayList<String> inIdSelectionList = new ArrayList<String>();
			ArrayList<String> outIdSelectionList = new ArrayList<String>();

			for (Filter f : filterList) {

				String selection = "SELECT "
						+ tableName
						+ ".ID FROM "
						+ tableName
						+ " INNER JOIN tag_binding ON ("
						+ tableName
						+ ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '"
						+ f.tag.type + "' AND tag_binding.tag_ID LIKE '"
						+ f.tag.id + "'";
				if (f.inOut) {
					inIdSelectionList.add(selection);
				} else {
					outIdSelectionList.add(selection);
				}

			}

			String inQuery = "";
			for (int i = 0; i < inIdSelectionList.size(); i++) {
				inQuery += "( " + inIdSelectionList.get(i) + " )";
				if (i != inIdSelectionList.size() - 1)
					inQuery += " UNION ";
			}
			System.out.println("inQuery: " + inQuery);

			String outQuery = "";
			for (int i = 0; i < outIdSelectionList.size(); i++) {
				outQuery += "( " + outIdSelectionList.get(i) + " )";
				if (i != outIdSelectionList.size() - 1)
					outQuery += " UNION ";
			}
			System.out.println("outQuery: " + outQuery);
			// msql.query()

			// String selectString = "SELECT " + tableName + ".* FROM " +
			// tableName + " INNER JOIN tag_binding ON (" + tableName +
			// ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '" +
			// filter.tag.type + "' AND tag_binding.tag_ID LIKE '" +
			// filter.tag.id + "'";
			// String selectStringId = "SELECT " + tableName + ".ID FROM " +
			// tableName + " INNER JOIN tag_binding ON (" + tableName +
			// ".ID = tag_binding.file_ID) WHERE tag_binding.type LIKE '" +
			// filter.tag.type + "' AND tag_binding.tag_ID LIKE '" +
			// filter.tag.id + "'";
			//
			// if(filter.inOut){
			// msql.query(selectString);
			// } else{
			// // Funktioniert noch nicht richtig! Es werden nur Dateien
			// gewählt, die ein tag binding mit dem filtertyp haben. andere
			// werden ignoriert
			// msql.query("SELECT " + tableName + ".* FROM " + tableName +
			// " WHERE " + tableName + ".ID NOT IN (" + selectStringId + ")");
			// //+ "WHERE tag_binding.type NOT LIKE '" + filter.tag.type +
			// "' OR (tag_binding.type LIKE '" + filter.tag.type +
			// "' AND tag_binding.tag_ID NOT LIKE '" + filter.tag.id + "')");
			// }
			// while (msql.next()) {
			// Tag t = getSpecificTags(tableName);
			// if(t != null){
			// tags.add(t);
			// }
			// }
		} else {
			System.out.println("not Connected queryTagListFiltered()");
		}
		return tags;
	}

	// alle verknüpften Tags finden
	public ArrayList<Tag> getBindedTagList(Tag_File file) {
		ArrayList<Tag> tagList = new ArrayList<Tag>();

		if (checkConnection()) {

			msql.query("SELECT type, tag_ID FROM tag_binding WHERE file_ID = "
					+ file.id);
			ArrayList<String> types = new ArrayList<String>();
			ArrayList tagIds = new ArrayList();
			while (msql.next()) {
				types.add(msql.getString("type"));
				tagIds.add(msql.getInt("tag_ID"));
			}

			for (int i = 0; i < types.size(); i++) {
				msql.query("SELECT * FROM " + types.get(i) + " WHERE ID = "
						+ tagIds.get(i));
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
		} else {
			System.out.println(tableName
					+ "wrong or not yet in SQL.createDBTag");
		}
		return null;
	}

	// TAG BINDING ERSTELLEN
	public void bindTag(Tag_File file, Tag tag) {
		if (checkConnection()) {
			// files
			msql.query("SELECT COUNT(*) FROM tag_binding WHERE file_ID = \""
					+ file.id + "\" AND type = \"" + tag.type
					+ "\" AND tag_ID = \"" + tag.id + "\"");
			msql.next();
			// System.out.println("number of rows: " + msql.getInt(1));

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
			} else if (t instanceof Tag) {
				if (t.name.trim().toLowerCase()
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
	public Tag getLastCreatedTag(String tableName) {
		Tag tag = null;

		// get last ID in table
		msql.query("SELECT LAST_INSERT_ID() FROM files");
		msql.next();
		int fileId = msql.getInt(1);

		msql.query("SELECT * FROM " + tableName + " WHERE ID = \"" + fileId
				+ "\"");
		msql.next();

		tag = getSpecificTags(tableName);

		return tag;
	}

	//
	public Tag getSpecificTags(String tableName) {
		Tag t = null;

		// existiert das Attribute in p5.attributes? - Dann gib existierends Attribut
		// zurück
		if (tableName != "files" && p5.attributes != null) {
			for (Tag _tag : p5.attributes) {
				if (tableName.trim().equals(_tag.type.trim()) && msql.getInt("ID") == _tag.id) {
					System.out.println("übergabe " + _tag.name + " " + _tag.type);
					return _tag;
				} 
			}
		}
		// existiert die Datei in p5.files? - Dann gib existierends Attribut
				// zurück
		if(tableName == "files" &&  p5.files != null){
			for (Tag _tag : p5.files) {
				if (tableName.trim().equals(_tag.type.trim()) && msql.getInt("ID") == _tag.id) {
					System.out.println("übergabe " + _tag.name + " " + _tag.type);
					return _tag;
				} 
			}
		}

		// ansonsten erstellen neuen Tag
		if (tableName.equals("files")) {
			Tag tag = new Tag_File(tableName, msql.getInt("ID"),
					msql.getString("name"), msql.getFloat("size"),
					msql.getString("path"), msql.getTimestamp("creation_time"),
					msql.getTimestamp("expiration_time"),
					msql.getInt("origin_ID"), msql.getInt("score"));
			t = tag;
		} else if (tableName.equals("locations")) {
			Tag tag = new Tag_Location(tableName, msql.getInt("ID"),
					msql.getString("name"), msql.getString("coordinates"));
			t = tag;
		} else if (tableName.equals("users")) {
			Tag_User tag = new Tag_User("users", msql.getInt("ID"),
					msql.getString("name"), msql.getString("password"));
			t = tag;
		} else if (tableName.equals("projects") || tableName.equals("keywords")) {
			Tag tag = new Tag(tableName, msql.getInt("ID"),
					msql.getString("name"));
			t = tag;
		} else {
			System.out.println(tableName + " not yet Listed in queryTagList");
		}
		System.out.println("neu erstellen: " + t.name + " " + t.type);
		return t;
	}
}

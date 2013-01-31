package tagexplorerprocessing2;

public class Tag_FileType extends Tag {

	Tag_File.FileType fileType;
	
	public Tag_FileType(Tag_File.FileType fileType){

		type = "fileType";
		this.fileType = fileType; 
		this.name = fileType.toString();
		
	}
}

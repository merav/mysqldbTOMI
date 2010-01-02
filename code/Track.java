package dbproj;

public class Track {
	
	private String title;
	private String artist;
	private String ext;
	
	// Title	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
	// Artist
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getArtist() {
		return artist;
	}
	
	// Ext
	public void setExt(String ext) {
		this.ext += ext;
	}
	public String getExt() {
		return ext;
	}
}

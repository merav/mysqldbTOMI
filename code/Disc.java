package dbproj;

import java.util.ArrayList;

public class Disc {
	
	private String discID;
	private Integer revision;
	private String artist;
	private String title;
	private Integer year;
	private String genre;
	private String extd;
	private ArrayList<Track> tracks;
	
	// Constructor
	
	public Disc() {
		tracks = new ArrayList<Track>();
	}
	
	// DiscID
	public void setDiscID(String discID) {
		this.discID = discID;
	}
	public String getDiscID() {
		return discID;
	}
	
	// DiscID
	public void setRevision(Integer revision) {
		this.revision = revision;
	}
	public Integer getRevision() {
		return revision;
	}
	
	// Artist
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getArtist() {
		return artist;
	}
	
	// Title
	public void setTitle(String title) {
		this.title = title;
	}
	public void addTitle(String title) {
		this.title += title;
	}
	public String getTitle() {
		return title;
	}
	
	// Year
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getYear() {
		return year;
	}
	
	// Genre
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getGenre() {
		return genre;
	}
	
	// Extd
	public void setExtd(String extd) {
		this.extd += extd;
	}
	public void addExtd(String extd) {
		this.extd += extd;
	}
	public String getExtd() {
		return extd;
	}
	
	// Tracks
	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}
	public ArrayList<Track> getTracks() {
		return tracks;
	}
	
}

package dbproj;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DBActions {
	
	private Connection conn;

	private int discsPerTransaction; 
	private int discCount=0;
	
	private PreparedStatement insertDisc; 
	private PreparedStatement insertGenre; 
	private PreparedStatement insertTheme;
	private PreparedStatement insertArtist;
	
	private Map<String, Integer> genres;
	private Map<String, Integer> artists;
	
	public DBActions(Connection conn, int discsPerTransaction)
	{
		this.conn = conn;
		this.discsPerTransaction = discsPerTransaction;
		
		genres = new HashMap<String, Integer>();
		artists = new HashMap<String, Integer>();
		
		try {
			
			insertDisc = conn.prepareStatement("INSERT INTO DISCS(DISC_ID,REVISION,TITLE,ARTIST,GENRE,YEAR,EXT,TRACKS_NUM) VALUES(?,?,?,?,?,?,?,?)");
			insertTheme = conn.prepareStatement("INSERT INTO THEMES(DISC_ID,DISC_REVISION,TRACK_NUM,ARTIST,THEME_NAME,EXT) VALUES(?,?,?,?,?,?)");
			insertGenre = conn.prepareStatement("INSERT INTO GENRES(GENRE_ID,GENRE_NAME) VALUES(?,?)");
			insertArtist = conn.prepareStatement("INSERT INTO ARTISTS(ARTIST_ID,ARTIST_NAME) VALUES(?,?)");
			
			//conn.setAutoCommit(false);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
	/**
	 * updateLineInDB
	 */
	public void updateDiskInDB(Disc disc, boolean last)
	{
		
		try
		{

			// insert disc
			insertDisc.setString(1, disc.getDiscID());
			insertDisc.setInt(2, disc.getRevision());
			insertDisc.setString(3, disc.getTitle());
			insertDisc.setInt(4, getArtistIDByName(disc.getArtist()));
			insertDisc.setInt(5, getGenereIDByName(disc.getGenre()));
			insertDisc.setInt(6, disc.getYear());
			insertDisc.setString(7, disc.getExtd());
			insertDisc.setInt(8, disc.getTracks().size());
			insertDisc.addBatch();

			// insert themes
			ArrayList<Track> tracks = disc.getTracks();
			Integer trackNumber = 1;
			for (Track track : tracks) {
				insertTheme.setString(1, disc.getDiscID());
				insertTheme.setInt(2, disc.getRevision());
				insertTheme.setInt(3, trackNumber);
				insertTheme.setInt(4, getArtistIDByName(track.getArtist()));
				insertTheme.setString(5, track.getTitle());
				insertTheme.setString(6, track.getExt());
				insertTheme.addBatch();
				trackNumber++;
				//System.out.println(disc.getDiscID() + ", " + getArtistIDByName(track.getArtist()) + ", " + track.getTitle());
				if (disc.getDiscID().compareTo("2a045904")==0)
				{
					System.out.println("debug");
				}
			}
			
			discCount++;
		
			// update in DB
			// Oracle JDBC completes executing the entire list of statements even if some
			// of them fail. The exception refers to the first failure.
			// All failures will be ignored. 
			if (discCount==discsPerTransaction || last) {

				discCount=0;
				boolean discFail = false;

				try 
				{
					insertGenre.executeBatch();
				} 
				catch (BatchUpdateException e) {
					System.out.println("ERROR insertGenre.executeBatch() - " + e.toString());
				}

				try 
				{
					insertArtist.executeBatch();
				} 
				catch (BatchUpdateException e) {
					System.out.println("ERROR insertArtist.executeBatch() - " + e.toString());
				}
				try 
				{
					insertDisc.executeBatch();
				} 
				catch (BatchUpdateException e) {
					System.out.println("ERROR insertDisc.executeBatch() (" + disc.getDiscID() + ") - " + e.toString());
					discFail = true;
				}
				try 
				{
					insertTheme.executeBatch();
				} 
				catch (BatchUpdateException e) {
					if (!discFail) {
						System.out.println("ERROR insertTheme.executeBatch() - " + e.toString());
					}
				}		

				//conn.commit();

			} 
		}
		catch (SQLException e)
		{
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("ERROR executeUpdate - " + e.toString());
			System.exit(0); 
		}

	}
	
	private Integer getGenereIDByName(String name) throws SQLException {
			
		Integer genreID;
		
		if (genres.containsKey(name)) {
			
			genreID = genres.get(name);
			
		} else { // add a new genre
			
			genreID = genres.size()+1;
			genres.put(name, genreID);
			insertGenre.setInt(1, genreID);
			insertGenre.setString(2, name);
			insertGenre.addBatch();
			
		}
		
		return genreID;
	}
	
	private Integer getArtistIDByName(String name) throws SQLException {
		
		Integer artistID;
		
		if (name==null) {
			return 0;
		}
			
		
		if (artists.containsKey(name)) {
			
			artistID = artists.get(name);
			
		} else { // add a new artist
			
			artistID = artists.size()+1;
			artists.put(name, artistID);
			insertArtist.setInt(1, artistID);
			insertArtist.setString(2, name);
			insertArtist.addBatch();
			
		}
		
		return artistID;
	}
	
	public void emptyTables() throws SQLException {
		
		Statement stmt = conn.createStatement();
		
		
		stmt.executeUpdate("DELETE FROM THEMES");
		stmt.executeUpdate("DELETE FROM DISCS");
		stmt.executeUpdate("DELETE FROM GENRES");
		stmt.executeUpdate("DELETE FROM ARTISTS");
		
		/*
		stmt.executeUpdate("TRUNCATE TABLE THEMES");
		stmt.executeUpdate("TRUNCATE TABLE DISCS");
		stmt.executeUpdate("TRUNCATE TABLE GENRES");
		stmt.executeUpdate("TRUNCATE TABLE ARTISTS");
		*/
		
		insertArtist.setInt(1, 0);
		insertArtist.setString(2, "empty");
		insertArtist.addBatch();
		insertArtist.executeBatch();
	
	}
	
	
	
}

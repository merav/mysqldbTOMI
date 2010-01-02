package dbproj;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

public class RunMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection conn = DBConnection.getConnection();
		DBActions act = new DBActions(conn, 1000);
		
		File zipFile = new File("C:\\tmp\\freeDB.tar.bz2");
		System.out.println(zipFile.getPath());

		FreeDBFileParser freeDBParser = new FreeDBFileParser(zipFile);
		
		System.out.println("Emptying DB tables...");
		try {
			act.emptyTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Done.");
		//System.exit(0);
		
		int i=0;
		int sum=0;
		Long time=0L;
		Long starttime = System.currentTimeMillis();
		
		for (Disc disc : freeDBParser) {
			i++;
			sum+=disc.getTracks().size();
			act.updateDiskInDB(disc, !freeDBParser.hasNext());
			if (i%1000==0) {
				time = (System.currentTimeMillis() - starttime) / 1000;
				System.out.println(i + " Discs, " + sum + " Tracks read (" + time + " sec)");
			}			
			//System.out.println(i + " " + disc.getDiscID() + " (" + disc.getTracks().size() + " tracks)");
		}
		time = (System.currentTimeMillis() - starttime) / 1000;
		System.out.println(i + " Discs, " + sum + " read (" + time + " sec)");

	}

}

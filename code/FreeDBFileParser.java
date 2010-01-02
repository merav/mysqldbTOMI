package dbproj;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.io.*;

import org.apache.tools.tar.*;
import org.apache.tools.bzip2.*;


public class FreeDBFileParser implements Iterable<Disc>, Iterator<Disc> {
	
	private TarInputStream tin;
	//private TarEntry nextEntry = null;
	private TarEntry _nextEntry = null;
	private Disc _nextDisc = null;
	
	private PipedInputStream in;
	private PipedOutputStream out;
	
	// assumption: no file is larger than 100KB
	private final Integer MAX_DISC_FILE = 50;
	private final Integer BUFFER_SIZE = 1024*MAX_DISC_FILE;  
	
	private ArrayList<Track> tracks;
	private Integer count;
	
	public FreeDBFileParser(File file)
	{
		InputStream is = null;
		CBZip2InputStream bz2is = null;
		
		try {
			
			is = new FileInputStream(file);
			
			// getting rid of the first 2 "BZ" bytes !!
			// see: http://www.kohsuke.org/bzip2/
			is.read();
			is.read();

			bz2is = new CBZip2InputStream(is);
			
			tin = new TarInputStream(bz2is);
			
			in = new PipedInputStream(BUFFER_SIZE);
			out = new PipedOutputStream(in);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		next(); // Initialize (first time always returns null!)
		
	}
	
	@Override
	public boolean hasNext() {
		return (_nextDisc != null);
	}
	
	private TarEntry getNextEntry() {
		
		TarEntry entry = null;
		try {
			while (true) {
				entry = tin.getNextEntry();
				if (entry!=null){
					if (entry.isDirectory()) {
						continue;
					}
					if (entry.getLinkName().compareTo("")!=0) { // ignore hard links
						continue;
					}
				}
				break;
			}
			
			tin.copyEntryContents(out);
			out.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entry;	
	}
	
	private Disc getNextDisc() {
		
		Disc disc = _nextDisc;
		
		while (true) {
		
			TarEntry entry = getNextEntry();
			
			if (entry==null) {
				_nextDisc=null;
				break;
			}
			
			_nextDisc = createDisc(in);
		
			if (_nextDisc!=null) { 
				break;
			}
				
		}
		
		return disc;
	}

	@Override
	public Disc next() {
			
		return getNextDisc();
		
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Disc> iterator() {
		return this;
	}
	
	private Disc createDisc(InputStream is) {
		
		Disc disc = new Disc();
		tracks = new ArrayList<Track>();
		count = 0;
		
		BufferedReader br = 
			new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(is, BUFFER_SIZE)));
		
		Line thisLine = null;
		Line nextLine = null;
		
		try {
			
			thisLine = getNextLine(br);
			
			// finish after last keyword is read (no end-of-stream indication...)
			// or if thisLine==null (Non ASCII characters)
			while (true) {
				
				if (thisLine.getKeyword()!=Keywords.PLAYORDER) {
					nextLine = getNextLine(br);
					
					if (thisLine==null || nextLine==null)
					{
						//System.out.println("Error - File has non-ASCII characters (DiscID: " + disc.getDiscID() + ")");
						return null;
					}

					if (thisLine.getKeywordStr().compareTo(nextLine.getKeywordStr())==0) {

						thisLine.concatValue(nextLine.getValue());

					} else {

						updateLine(thisLine, disc);
						thisLine = nextLine;
					}

				} else {
					
					updateLine(thisLine, disc);
					
					break;
					
				}
						
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return disc;
		
	}
	
	private void updateLine(Line line, Disc disc) throws IOException
	{		
				
		Keywords keyword = line.getKeyword();
		String value = line.getValue();
		
		String[] str;
		
		switch (keyword) {

		case DISCID:
			
			// is there are multiple IDs, take the first one.
			str = value.split(",", 2);
			
			disc.setDiscID(str[0].trim());
			
			// create tracks list
			tracks = new ArrayList<Track>();
			
			break;
			
		case REVISION:
			
			if (value.trim().compareTo("")==0) {
				value = "0";
			}
				
			disc.setRevision(Integer.valueOf(value.trim()));
		
			break;

		case DTITLE:
			
			str = value.split(" / ", 2);	
			
			if (str.length==2 && str[1].length()>0) {
				disc.setArtist(str[0]);
				disc.setTitle(str[1]);				
			} else {
				disc.setTitle(str[0]);
			}
			
			break;

		case DYEAR:
				
			if (value.trim().compareTo("")==0) {
				value = "0";
			}
				
			disc.setYear(Integer.valueOf(value.trim()));
		
			break;
			
		case DGENRE:
			
			disc.setGenre(value);
			
			break;
			
		case TTITLE:
			
			Track track = new Track();
			
			str = value.split(" / ", 2);	
			
			if (str.length==2) {
				track.setArtist(str[0]);
				track.setTitle(str[1]);				
			} else {
				track.setTitle(str[0]);
			}
			
			tracks.add(track);
			
			break;
			
		case EXTD:
			
			disc.setExtd(value);
			
			break;

		case EXTT:

			tracks.get(count).setExt(value);
			count++;
			
			break;
			
		case PLAYORDER:
			
			disc.setTracks(tracks);
			
			break;

		}

	}
	
	private Line getNextLine(BufferedReader br) throws IOException {
			
		String strLine = br.readLine();
		
		Pattern _notAsciiPattern = Pattern.compile("[^\\p{ASCII}]");
		if (_notAsciiPattern.matcher(strLine).find()) {
			return null;
		}
				
		// get rid of comments
		while (strLine!=null && strLine.charAt(0)=='#') {
			
			//System.out.println(strLine);
			
			if (strLine.startsWith("# Revision: "))
			{
				strLine = strLine.replace("# ", "");
				strLine = strLine.replace(": ", "=");
				strLine = strLine.toUpperCase();
				
				//System.out.println(strLine);
			}
			else
			{	
				strLine = br.readLine();
				if (_notAsciiPattern.matcher(strLine).find()) {
					return null;
				}
			}
		}
		
		if (strLine==null) {
			return null;
		}
		
		String[] str = strLine.split("=", 2);
		
		String keywordStr = str[0];
		String value = str[1];
			
		Keywords keyword = null;
		for (Keywords key : Keywords.values()) {
			if (keywordStr.startsWith(key.name())) {
				keyword = key;
				break;	
			}
		}
		
		if (keyword==null) {
			throw new IOException(); // TODO: Create a dedicated exception
		}
		
		Line line = new Line(keyword, keywordStr, value);

		return line;
		
	}
	
	private class Line {
		
		private Keywords keyword;
		private String keywordStr;
		private String value;
		
		public Line(Keywords keyword, String keywordStr, String value) {
			this.keyword = keyword;
			this.keywordStr = keywordStr;
			this.value = value;
		}
		
		public Keywords getKeyword() {
			return keyword;
		}
		
		public String getKeywordStr() {
			return keywordStr;
		}

		public String getValue() {
			return value;
		}
		
		public void concatValue(String value) {
			this.value += value;
		}
		
		
		
		
	}

}

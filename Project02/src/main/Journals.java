package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Journals {
	
	private Map<String, Journal> journals;

	public Journals() {
		File[] filelist = null; 
		try {
			String path = new File(".").getCanonicalPath();
			if (path.endsWith("bin")) { // the program is executed through the script.
				filelist = new File("../journals").listFiles();
			} else { // the program is executed through eclipse.
				filelist = new File("journals").listFiles(); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		journals = new HashMap<String, Journal>();
		for( File file : filelist ) {
			String filename = file.toString();
			Journal journal = new Journal(filename);
			journals.put(journal.patient, journal);
		}
	}
	
	public Journal get(String name) throws NoSuchJournalException {
		Journal journal = journals.get(name);
		if (journal == null) {
			throw new NoSuchJournalException(name);
		} else {
			return journal;
		}
	}
	public static void main(String[] args) {
		Journals journals = new Journals();
		for (String name : journals.journals.keySet() ) {
			System.out.println(name);
		}
	}
}
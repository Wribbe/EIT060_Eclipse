package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Journals {
	
	private Map<String, Journal> journals;

	public Journals() {
		File[] filelist = new File("journals").listFiles();
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
		try {
			System.out.println(journals.get("Batman Hultin").patient);
			System.out.println(journals.get("Patient that does not exist"));
		} catch (NoSuchJournalException e) {
			System.out.println(String.format("No journal found: %s.",e.getMessage()));
		}
	}
}
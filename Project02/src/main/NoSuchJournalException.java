package main;

public class NoSuchJournalException extends Exception {
	private String journal;
	public NoSuchJournalException(String journal) {
		this.journal = journal;
	}
	public String getMessage() {
		return journal;
	}
}
package main;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Journal {
	
	public String doctor,nurse,patient,section,affliction,age,status,division;
	public List<String> rawLines = new ArrayList<String>();
	
	public Journal(String patient, String doctor, String nurse) {
		this.patient = patient;
		this.doctor = doctor;
		this.nurse = nurse;
	}
	
	public Journal(String filename) {
		populateJournalFromFile(this, filename);
	}
	
	private void populateJournalFromFile(Journal journal, String filename) {
		Map<String,String> journalData = parseJournalData(filename);
		journal.patient = journalData.get("patient");
		journal.doctor = journalData.get("doctor");
		journal.nurse = journalData.get("nurse");
		journal.division = journalData.get("division");
		journal.affliction = journalData.get("affliction");
		journal.age = journalData.get("age");
		journal.status = journalData.get("current status");
	}
	
	private List<String> getLines(String filename) {
		Path filePath = Paths.get(filename);
		List<String> returnList;
		try {
			returnList = Files.readAllLines(filePath, Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println(String.format("Could not read %s.",filename));
			returnList = new ArrayList<String>();
		}
		this.rawLines = returnList;
		return returnList;
	}
	
	private Map<String,String> parseJournalData(String filename) {
		List<String> journalLines = getLines(filename);
		String header = journalLines.remove(0);
		Map<String,String> dataMap = new HashMap<String,String>();
		parseJournalHead(dataMap, header);
		parseJournalBody(dataMap, journalLines);
		return dataMap;
	}
	
	private void parseJournalHead(Map<String,String> dataMap, String header) { 
		String[] headerTokens = header.split(",");
		String[] tokenOrder = {"patient","doctor","nurse","division"};
		for (int i=0; i<tokenOrder.length; i++) {
			dataMap.put(tokenOrder[i],headerTokens[i].trim());
		}
	}
	
	private void parseJournalBody(Map<String,String> dataMap, List<String> journalLines) {
		String[] ignoredFields = {"Current physician","Current nurse", "Division"};
		for (String line : journalLines) {
			if (line.startsWith("=") || line.equals("")) {
				continue;
			} else {
				String[] fieldTokens = line.split(":");
				String fieldName = fieldTokens[0];
				String fieldValue = fieldTokens[1];
				for (String ignoreThisField : ignoredFields) {
					if( ignoreThisField.equals(fieldName)) {
						continue;
					} else {
						dataMap.put(fieldName.toLowerCase(), fieldValue.trim());
					}
				}
			}
		}
	}
	public static void main(String[] args) {
		Journal journal = new Journal("journals/Batman Hultin.txt");
		System.out.println(journal.patient);
		System.out.println(journal.age);
		System.out.println(journal.status);
	}
}
package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Persons {
	
	public Map<String, Person> persons = new HashMap<String,Person>();
	
	Persons () {
		String[] files = null;
		try {
			String[] binfiles =	{"../doctors.txt","../nurses.txt"};
			String[] eclipsfiles = {"doctors.txt","nurses.txt"};
			String path = new File(".").getCanonicalPath();
			if (path.endsWith("bin")) { // the program is executed through the script.
				files = binfiles;
			} else { // the program is executed through eclipse.
				files = eclipsfiles;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String filename : files) {
			populateFromFile(filename);
		}
	}
	
	private void populateFromFile(String filename) {
		List<String> lines = getLines(filename);
		for (String line : lines) {
			String[] nameTokens = line.split(";");
			String name = nameTokens[0].trim();
			String division = nameTokens[1].trim();
			Person person = new Person(name, division);
			persons.put(name,person);
		}
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
		return returnList;
	}
	
	public Person get(String name) {
		return persons.get(name);
	}
	
	public static void main(String[] args) {
		Persons persons = new Persons();
		for (Person person: persons.persons.values()) {
			System.out.println(String.format("%s --> %s",person.name,person.division));
		}
	}
}
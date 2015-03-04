package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuditLogger {
	private PrintStream out;

	public AuditLogger(String file) {
		try {
			FileOutputStream fout = new FileOutputStream(file, true);
			out = new PrintStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void log(String string) {
		out.println(getDate()+":"+string);
		out.flush();
	}
	
	public void connectAttempt(String user, String type, String result){
		out.println(getDate() + " " + user + " of type: " + type + " " + result + " logged in.");
	}

	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
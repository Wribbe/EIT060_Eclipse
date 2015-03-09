package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class StartClientAndServer {
	
	private static List<ProcessBuilder> clients = new ArrayList<ProcessBuilder>();
	private static String port = "1234";
	
	public static void main(String[] args) throws InterruptedException {

		Boolean rebuildCerts = true;
		Boolean recreateJournals = true;

		String port = "1234";

		ProcessBuilder runServer = new ProcessBuilder("bash", "run_server.sh", port);
		runServer.redirectErrorStream(true);
		
//		addClient("Victoria Zoran","password");
//		addClient("Svetlana Mercer","password");
//		addClient("Peter Miller", "password");
//		addClient("Adam Persson", "password");
//		addClient("Donny Nilsson", "password");
		addClient("Mr Black", "password");

		Process generatorThread = null;
		Process serverThread = null;
		Process journalThread = null;

		if(rebuildCerts) {
			try {
				ProcessBuilder generateCertificates = new ProcessBuilder("bash", "generate_certs.sh");
				generatorThread = generateCertificates.start();
			} catch (IOException e) {
				System.out.println(String.format("Could not generate scripts: %s",e.getMessage()));
				System.exit(1);
			}
			printOutput(generatorThread);
			try {
				generatorThread.waitFor();
			} catch (InterruptedException e1) {
				System.out.println("Error when waiting for generator.");
				System.exit(1);
			}
		}
		if(recreateJournals) {
			ProcessBuilder createJournals = new ProcessBuilder("python", "generate_journals.py");
			try {
				journalThread = createJournals.start();
			} catch (IOException e) {
				System.out.println(String.format("Could not create journals: %s",e.getMessage()));
				System.exit(1);
			}
			try {
				journalThread.waitFor();
			} catch (InterruptedException e1) {
				System.out.println("Error when waiting for journals.");
				System.exit(1);
			}
		}
		try {
			serverThread = runServer.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not start server: %s",e.getMessage()));
			System.exit(1);
		}
		try {
				startClients();
			} catch (IOException e) {
			System.out.println(String.format("Could not start client: %s",e.getMessage()));
			System.exit(1);
		}
		try {
			serverThread.waitFor();
		} catch (InterruptedException e1) {
			System.out.println("Error while waiting for server-thread.");
			System.exit(1);
		}
	}

	private static void addClient(String username, String password) {
//		username = username.replace(" ","\\ ");
//		username = String.format("\"%s\"",username);
		ProcessBuilder clientProcess = new ProcessBuilder("bash", "run_client.sh", port, username, password);
		clientProcess.redirectErrorStream(true);
		clients.add(clientProcess);
	}

	private static void startClients() throws IOException {
		for (ProcessBuilder client : clients) {
			client.start();
		}
	}

	private static void printOutput(Process proc) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = null;
		try {
			while ( (line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

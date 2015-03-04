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

		String port = "1234";
		String serverName = "server"; // Name of compiled class for server.

		ProcessBuilder generateCertificates = new ProcessBuilder("bash", "generate_certs.sh");
		ProcessBuilder runServer = new ProcessBuilder("bash", "run_server.sh", serverName, port);
		runServer.redirectErrorStream(true);
		
		addClient("Jones","password");
		addClient("Mandy","password");

		Process generatorThread = null;
		Process serverThread = null;

		try {
			generatorThread = generateCertificates.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not generate scripts: %s",e.getMessage()));
			System.exit(1);
		}
		//printOutput(generatorThread);
		try {
			generatorThread.waitFor();
		} catch (InterruptedException e1) {
			System.out.println("Error when waiting for generator.");
			System.exit(1);
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
		ProcessBuilder clientProcess = new ProcessBuilder("bash", "run_client.sh", "client", "localhost", port, username, password);
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

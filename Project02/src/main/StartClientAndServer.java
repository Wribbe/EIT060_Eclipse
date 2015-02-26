package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class StartClientAndServer {
	public static void main(String[] args) throws InterruptedException {

		String port = "1234";
		String clientName = "client"; // Name of compiled class for client.
		String serverName = "server"; // Name of compiled class for server.

		ProcessBuilder generateCertificates = new ProcessBuilder("bash", "generate_certs.sh");
		ProcessBuilder runServer = new ProcessBuilder("bash", "run_server.sh", serverName, port);
		runServer.redirectErrorStream(true);
		ProcessBuilder runClient = new ProcessBuilder("bash", "run_client.sh", clientName, "localhost", port, "username", "password");
		runClient.redirectErrorStream(true);

		Process generatorThread = null;
		Process serverThread = null;
		Process clientThread = null;

		try {
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
		try {
			serverThread = runServer.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not start server: %s",e.getMessage()));
			System.exit(1);
		}
		printOutput(serverThread);
		try {
			clientThread = runClient.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not start client: %s",e.getMessage()));
			System.exit(1);
		}
		printOutput(clientThread);
		try {
			serverThread.waitFor();
		} catch (InterruptedException e1) {
			System.out.println("Error while waiting for server-thread.");
			System.exit(1);
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

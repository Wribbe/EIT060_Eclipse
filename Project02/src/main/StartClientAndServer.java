package main;
import java.io.IOException;


public class StartClientAndServer {
	public static void main(String[] args) throws InterruptedException {

		ProcessBuilder generateCertificates = new ProcessBuilder("bash", "run_client_and_server.sh");
		ProcessBuilder runServerAndClient = new ProcessBuilder("bash", "run_client_and_server.sh");

		Process generatorThread = null;
		Process clientServerThread = null;

		try {
			generatorThread = generateCertificates.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not generate scripts: %s",e.getMessage()));
			System.exit(1);
		}
		try {
			generatorThread.waitFor();
		} catch (InterruptedException e1) {
			System.out.println("Error when waiting for generator.");
			System.exit(1);
		}
		try {
			clientServerThread = runServerAndClient.start();
		} catch (IOException e) {
			System.out.println(String.format("Could not start server and client : %s",e.getMessage()));
			System.exit(1);
		}
		try {
			clientServerThread.waitFor();
		} catch (InterruptedException e1) {
			System.out.println("Error when waiting for client server thread.");
			System.exit(1);
		}
	}
}

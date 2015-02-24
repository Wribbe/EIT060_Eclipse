package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;

public class server extends Thread {
	private SSLServerSocket serverSocket;
	private SSLSocket socket;
	private SSLSession session;
	private SSLServerSocketFactory ssf;
	private int port;
	private int numConnectedClients = 0;
	private AuditLogger audit;

	private BufferedReader reader;
	private BufferedWriter writer;

	public server(int port) {
		this.port = port;
		audit = new AuditLogger("../audit.txt");
		audit.connectAttempt("TEST", "TEST", "TEST");

	}

	public void run() {
		connect();

		// så länge anslutningen är öppen väntar servern på kommandon
		while (!socket.isClosed()) {
			System.out.println("Waiting for command...");

			doCommand(readFromClient());
		}
	}

	// Här ska kommandon skrivna via terminalen behandlas.
	// Hur tar vi hand om flera olika klienter samtidigt? hur kollar vi om de
	// har rättigheter att göra specifikt kommando?
	private void doCommand(String command) {
		String[] split = command.split(" ");

		if (split[0].equals("read")) {

		}
	}

	private String readFromClient() {
		while (!socket.isClosed()) {
			try {
				String s = null;
				if ((s = reader.readLine()) != null) {
					s = s.replace("\n", "");
					System.out.println(":>server recieveing " + s);
					return s;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void connect() {
		try {
			ssf = (SSLServerSocketFactory) getFactory("TLS");
			serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
			serverSocket.setNeedClientAuth(true);
			System.out.println("Waiting for client...");
			socket = (SSLSocket) serverSocket.accept();

			session = socket.getSession();
			X509Certificate cert = (X509Certificate) session
					.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			numConnectedClients++;
			System.out.println("client connected");
			System.out.println("client name (cert subject DN field): "
					+ subject);
			// Lägg till login i loggen
			audit.connectAttempt(splitSubject(subject)[1],
					splitSubject(subject)[0], "successfully");
			System.out.println(numConnectedClients
					+ " concurrent connection(s)\n");

			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Client could not connect");
			e.printStackTrace();
		}
	}

	private String[] splitSubject(String subj) {
		String[] temp = subj.split(", ");
		temp[0] = temp[0].substring(3);
		temp[1] = temp[1].substring(3);
		return temp;
	}

	/** Kopierad rakt av från förra projektet */

	private ServerSocketFactory getFactory(String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
			try { // set up key manager to perform server authentication
				SSLContext ctx = SSLContext.getInstance("TLS");
				KeyManagerFactory kmf = KeyManagerFactory
						.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory
						.getInstance("SunX509");
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				char[] password = "password".toCharArray();

				ks.load(new FileInputStream("stores/serverkeystore"), password); // keystore
				// password
				// (storepass)
				ts.load(new FileInputStream("stores/servertruststore"),
						password); // truststore
				// password
				// (storepass)
				kmf.init(ks, password); // certificate password (keypass)
				tmf.init(ts); // possible to use keystore as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}

	public static void main(String[] args) {
		new server(8888).start();
	}
}

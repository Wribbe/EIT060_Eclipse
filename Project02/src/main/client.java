package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;

public class client extends Thread {
	private String address;
	private int port;
	private boolean connected = false;

	private SSLSocket socket;
	private SSLSocketFactory socketFactory;

	private Scanner scan;
	private BufferedReader reader;
	private BufferedWriter writer;

	public client(String address, int port) {
		this.address = address;
		this.port = port;
		scan = new Scanner(System.in);
	}

	public void run() {
		connect();
		while (true) {
			if (scan.hasNext()) {
				writeToServer(scan.next());
			} else {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void writeToServer(String s) {
		try {
			writer.write(s + "\n");
			writer.flush();
			System.out.println("The client is sending " + s);
		} catch (IOException e) {
		}
	}

	public void connect() {
		while (!connected) {
			try {
				try {
					char[] password = "password".toCharArray();
					KeyStore ks = KeyStore.getInstance("JKS");
					KeyStore ts = KeyStore.getInstance("JKS");
					KeyManagerFactory kmf = KeyManagerFactory
							.getInstance("SunX509");
					TrustManagerFactory tmf = TrustManagerFactory
							.getInstance("SunX509");
					SSLContext ctx = SSLContext.getInstance("TLS");
					ks.load(new FileInputStream("stores/clientkeystore"),
							password); // keystore password (storepass)
					ts.load(new FileInputStream("stores/clienttruststore"),
							password); // truststore password (storepass);
					kmf.init(ks, password); // user password (keypass)
					tmf.init(ts); // keystore can be used as truststore here
					ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
					socketFactory = ctx.getSocketFactory();
				} catch (Exception e) {
					throw new IOException(e.getMessage());
				}
				socket = (SSLSocket) socketFactory.createSocket(address, port);
				System.out.println("\nsocket before handshake:\n" + socket
						+ "\n");

				/*
				 * send http request
				 * 
				 * See SSLSocketClient.java for more information about why there
				 * is a forced handshake here when using PrintWriters.
				 */
				socket.startHandshake();

				SSLSession session = socket.getSession();
				X509Certificate cert = (X509Certificate) session
						.getPeerCertificateChain()[0];
				String subject = cert.getSubjectDN().getName();
				System.out
						.println("certificate name (subject DN field) on certificate received from server:\n"
								+ subject + "\n");
				System.out.println("socket after handshake:\n" + socket + "\n");
				System.out.println("secure connection established\n\n");
				
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				connected = true;
			} catch (IOException e) {
				System.out
						.println("Could not authenticate, trying again in 3 seconds");
				try {
					sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new client("localhost", 8888).start();
	}
}

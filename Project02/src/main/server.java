package main;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class server implements Runnable {
    private ServerSocket serverSocket = null;
    private static int numConnectedClients = 0;

    public server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();
    }

    public void run() {
        try {
            SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
    	    numConnectedClients++;
            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            PrintWriter clientOutput = null;
            BufferedReader in = null;
            clientOutput = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            String responseToClient = "";
            while ((clientMsg = in.readLine()) != null) {
                System.out.println("received '" + clientMsg + "' from client");
                System.out.println("Checking for command.");
                responseToClient = parseClientInput(clientMsg, subject);
                System.out.println("Done checking for command.");
                sendToClient(responseToClient, clientOutput);
                System.out.println("done\n");
			}
			in.close();
			clientOutput.close();
			socket.close();
    	    numConnectedClients--;
            System.out.println("client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
    private void sendToClient(String message, PrintWriter clientOutput) {
    	System.out.print("sending '" + message + "' to client...");
    	clientOutput.println(message);
    	clientOutput.flush();
    }
    
    private String parseClientInput(String input, String subject) {

    	Map<String,String> credentials = parseCredentials(subject);

    	String usernameKey = "OU";
    	String accessKey = "CN";
    	
    	String username = credentials.get(usernameKey);
    	String accessLevel = credentials.get(accessKey);

    	if (input.equals("username")) {
    		return username;
    	} else if (input.equals("access")) {
    		return accessLevel;
    	}
    	return "Unknown command.";
    }
    
    private Map<String,String> parseCredentials(String subject) {
    	Map<String,String> returnMap = new HashMap<String,String>();
    	ArrayList<String> assignments = new ArrayList<String>();
    	 for(String assignmentToken : subject.split(",")) {
    		 assignments.add(assignmentToken.trim());
    	 }
    	 for(String assignment : assignments ) {
    		 String[] tokens = assignment.split("=");
    		 if (tokens.length < 2) {
    			 continue;
    		 }
    		 returnMap.put(tokens[0],tokens[1]);
    	 }
    	 return returnMap;
    }

    private void newListener() { (new Thread(this)).start(); } // calls run()

    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("stores/serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("stores/servertruststore"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
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
}

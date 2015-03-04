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
    private Journals journals = new Journals();
    private Persons persons = new Persons();
    private AuditLogger logger = new AuditLogger("../log.txt");

    private String usernameKey = "OU";
    private String accessKey = "CN";

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
    		logger.log(String.format("Client connected with subject: %s",subject));
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
    	clientOutput.flush();
    }
    
    private String parseClientInput(String inputString, String subject) {

    	Map<String,String> credentials = parseCredentials(subject);
    	String[] input = inputString.split(" ");
    	
    	String mainCommand = input[0];

    	String username = credentials.get(usernameKey).trim();
    	String accessLevel = credentials.get(accessKey).trim();

    	if (mainCommand.equals("username")) {
    		return username;
    	} else if (mainCommand.equals("access")) {
    		return accessLevel;
    	} else if (mainCommand.equals("read")) {
    		return readJournalCommand(credentials, input);
    	} else if (mainCommand.equals("division")) {
    		Person person = persons.get(username);
    		if (person == null) {
    			return String.format("No division for %s.",username);
    		}
    		return person.division;
    	} else if (mainCommand.equals("edit")) {
    		return editJournalCommand(credentials, input);
    	} else if (mainCommand.equals("remove")) {
    		return removeJournalCommand(credentials, input);
    	}
    	return "Unknown command.";
    }
    private String editJournalCommand(Map<String,String> credentials, String[] input) {
    	String journalName = "";
    	Journal journal;
    	try {
    		journalName = input[1]+" "+input[2];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		return "The name of the journal you want to edit can not be blank.";
    	}
    	try {
    		journal = journals.get(journalName);
    	} catch (NoSuchJournalException e) {
    		return String.format("There was no journal under the name %s",journalName);
    	}
    	String username = credentials.get(usernameKey).trim();
    	String access = credentials.get(accessKey);
    	String journalDoctor = journal.doctor;
    	String journalNurse = journal.nurse;
    	String journalDivision = journal.division;
    	Person person =  persons.get(username);
    	String usernameDivison = "";
    	if (person != null) {
    		usernameDivison = person.division;
    	}

    	if(username.equals(journalDoctor) || username.equals(journalNurse) || access.equals("agency") || journalDivision.equals(usernameDivison)) {
    		logger.log(String.format("%s edited journal: %s.",username,journalName));
    		return String.format("Grant read-access to %s",journalName);
    	} else {
    		logger.log(String.format("%s tried to edit journal: %s.",username,journalName));
    		return "You don't have permission to read this journal.";
    	}
    }

    private String removeJournalCommand(Map<String,String> credentials, String[] input) {
    	String journalName = "";
    	Journal journal;
    	String username = credentials.get(usernameKey);
    	try {
    		journalName = input[1]+" "+input[2];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		return "The name of the journal you want to remove can not be blank.";
    	}
    	try {
    		journal = journals.get(journalName);
    	} catch (NoSuchJournalException e) {
    		return String.format("There was no journal under the name %s",journalName);
    	}
    	String access = credentials.get(accessKey);

    	if(access.equals("agency")) {
    		logger.log(String.format("%s removed journal: %s.",username,journalName));
    		return String.format("removed %s",journalName);
    	} else {
    		logger.log(String.format("%s tried to remove journal: %s.",username,journalName));
    		return "You don't have permission to remove this journal.";
    	}
    }
    
    private String readJournalCommand(Map<String,String> credentials, String[] input) {
    	String journalName = "";
    	Journal journal;
    	try {
    		journalName = input[1]+" "+input[2];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		return "The name of the journal you want to read can not be blank.";
    	}
    	try {
    		journal = journals.get(journalName);
    	} catch (NoSuchJournalException e) {
    		return String.format("There was no journal under the name %s",journalName);
    	}
    	String username = credentials.get(usernameKey).trim();
    	String access = credentials.get(accessKey);
    	String journalDoctor = journal.doctor;
    	String journalNurse = journal.nurse;
    	String journalDivision = journal.division;
    	Person person =  persons.get(username);
    	String usernameDivison = "";
    	if (person != null) {
    		usernameDivison = person.division;
    	}
    	String patient = journal.patient;

    	if(username.equals(journalDoctor) || username.equals(journalNurse) || access.equals("agency") || journalDivision.equals(usernameDivison) || username.equals(patient)) {
    		logger.log(String.format("%s read journal: %s.",username,journalName));
    		return journal.toString();
    	} else {
    		logger.log(String.format("%s tried to read journal: %s.",username,journalName));
    		return "You don't have permission to read this journal.";
    	}
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

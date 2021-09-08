package allcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


/**
 * Server portion of the peer-to-peer system
 * Waits for connections from the peers (Clients) on well-known port 7734
 *
 * @author Marcus Lerro
 *
 */
public class Server {


    public static void main(final String[] args) throws IOException {
        System.out.println("Server starting...");

        // List of clients active on server
        //final LinkedList<Client> clientList = new LinkedList<Client>();

        // List of RFC's on the server
        //final LinkedList<RFC> rfcList = new LinkedList<RFC>();

        // Server is listening on port 7734
        final ServerSocket servSocket = new ServerSocket(7734);

        // Infinite loop for client requests
        while (true) {
            Socket socket = null;

            try {
                // socket object to receive incoming client requests
                socket = servSocket.accept();

                // Create the new client and add to the list
                //final Client client = new Client("hostname" + clientList.size() , socket.getPort());
                //clientList.add( client );

                System.out.println("A new client is connected : " + socket);

                // obtaining input and out streams
                final DataInputStream input = new DataInputStream(socket.getInputStream());
                final DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                // create a new thread object
                final Thread t = new ClientHandler(socket, input, output);

                // Invoking the start() method
                t.start();

                /*for (int i = 0; i < clientList.size(); i++) {
                    System.out.println("Hostname: " + clientList.get( i ).getHostname() + "\nPortnumber: " + clientList.get(i).getPortnumber());
                }
                System.out.println("List size: " + clientList.size());*/
            } catch (final Exception e) {
                socket.close();
                e.printStackTrace();
            }
        }
    }
}

/**
 * This class will handle the numerous clients
 * It will also keep track of our LinkedLists statically
 * Reference for Multithreading: https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
 *
 * @author Marcus Lerro
 *
 */
class ClientHandler extends Thread {

    // List of clients active on server
    final static LinkedList<Client> clientList = new LinkedList<Client>();

    // List of RFC's on the server
    final static LinkedList<RFC> rfcList = new LinkedList<RFC>();

    //DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    //DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream input;
    final DataOutputStream output;
    final Socket socket;


    // Class constructor
    public ClientHandler(final Socket socket, final DataInputStream input, final DataOutputStream output) {
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {

        // Create the new client and add to the list
        final Client client = new Client("client." + clientList.size() , socket.getPort());
        clientList.add( client );

        String received;
        final String toreturn;
        loop: while (true) {
            try {

                output.writeUTF( "Establish P2P or P2S connection?\n"
                        + "Type 'Exit' to close anytime" );

                // Ask user what he wants
                //output.writeUTF("What do you want?[Date | Time]..\n"+
                //"Type Exit to terminate connection.");

                // receive the answer from client
                received = input.readUTF();

                // creating Date object
                //final Date date = new Date();

                // Interact based on the answer from the client
                switch (received) {

                    case "Exit" :
                        // Remove RFC's from rfcList
                        for (int i = 0; i < rfcList.size(); i++) {

                            // If the rfc is owned by only 1 client, remove from list
                            if (rfcList.get( i ).getClientList().size() <= 1) {
                                for (int j = 0; j < rfcList.get( i ).getClientList().size(); j++) {
                                    if ( rfcList.get( i ).getClientList().get( j ).getHostname().equals( client.getHostname() ) ) {
                                        rfcList.remove( i );
                                        break;
                                    }
                                }
                            } else {
                                // If there is client that owns the rfc, just remove the client that disconnected from the rfc's list
                                for (int j = 0; j < rfcList.get( i ).getClientList().size(); j++) {
                                    if (rfcList.get( i ).getClientList().get( j ).getHostname().equals( client.getHostname() )) {
                                        rfcList.get( i ).removeClient( j );
                                        break;
                                    }
                                }
                            }
                        }
                        // Remove client from the clientList
                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get( i ).getHostname().equals( client.getHostname() )) {
                                clientList.remove( i );
                            }
                        }

                        // Closes the client
                        System.out.println("Client " + this.socket + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.socket.close();
                        System.out.println("Connection closed");

                        break loop;




                    case "P2P" :
                        output.writeUTF( "\nRequest Method?[GET]" );
                        received = input.readUTF();
                        if (received.equals( "GET" )) {
                            final String operatingSystem = System.getProperty( "os.name" );
                            final String host = client.getHostname();
                            int rfcNumber;

                            // Obtain information of the RFC to get
                            output.writeUTF( "RFC Number: " );
                            final String rfcString = input.readUTF();
                            try {
                                rfcNumber = Integer.parseInt( rfcString );
                            } catch (final Exception e) {
                                output.writeUTF( "\nGet Response Message:\n"
                                        + "P2P-CI/1.0 400 Bad Request\n"
                                        + "RFC " + rfcString + "\n" );
                                break;
                            }

                            String request = "\nGet Request Message:\nGET RFC "
                                    + rfcNumber + " P2P-Cl/1.0\n"
                                    + "Host: " + host
                                    + "\nOS: " + operatingSystem + "\n";


                            boolean rfcFound = false;
                            //Check if a client has this RFC and adds to their rfc list if found
                            //This loop searches the client list for clients that potentially have the rfc
                            for (int i = 0; i < clientList.size(); i++) {
                                final Client tempClient = clientList.get( i );

                                //This loop searches the rfc list of a client and tries to find the given rfc
                                for (int j = 0; j < tempClient.getRfcList().size(); j++ ) {
                                    if (tempClient.getRfcList().get( j ).getRfcNumber() == rfcNumber) {

                                        //This loop finds the current client and adds the RFC to its list of RFCs
                                        for (int k = 0; k < clientList.size(); k++) {
                                            if (clientList.get( k ).getHostname().equals(tempClient.getHostname())) {
                                                clientList.get( k ).addRFC( tempClient.getRfcList().get( j ) );
                                                rfcFound = true;
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }

                                // If an rfc has been found, there is no need to loop through the other clients
                                if (rfcFound) {
                                    break;
                                }
                            }
                            if (rfcFound) {
                                request = request + "\nGet Response Message:\n"
                                        + "P2P-Cl/1.0 200 OK\n"
                                        + "Date: " + "\n"
                                        + "OS: " + "\n"
                                        + "Last-Modified: " + "\n"
                                        + "Content-Length: " + "\n"
                                        + "Content-Type: " + "\n";
                            } else {
                                request = request + "\nGet Response Message:\n" + "P2P-Cl/1.0 404 Not Found\n";
                            }

                            output.writeUTF(request);


                        } else {
                            output.writeUTF( "Invalid input\n" );
                        }
                        break;

                    case "P2S" :
                        output.writeUTF( "\nRequest Method?[ADD | LOOKUP | LIST]");
                        received = input.readUTF();
                        if (received.equals( "ADD" )) {

                            // Obtain information of the RFC to add
                            output.writeUTF( "RFC Number: " );
                            final String rfcString = input.readUTF();
                            int rfcNumber;
                            try {
                                rfcNumber = Integer.parseInt( rfcString );
                            } catch (final Exception e) {
                                output.writeUTF( "\nAdd Response Message:\n"
                                        + "P2P-CI/1.0 400 Bad Request\n"
                                        + "RFC " + rfcString + "\n" );
                                break;
                            }
                            output.writeUTF( "RFC Title: " );
                            final String title = input.readUTF();

                            // Request message
                            output.writeUTF( "\nAdd Request Message:\n"
                                    + "ADD RFC " + rfcNumber + " P2P-Cl/1.0\n"
                                    + "Host: " + client.getHostname() + "\n"
                                    + "Port: " + client.getPortnumber() + "\n"
                                    + "Title: " + title + "\n\n"
                                    + "Add Response Message:\n"
                                    + "P2P-CI/1.0 200 OK\n"
                                    + "RFC " + rfcNumber + " " + title
                                    + " " + client.getPortnumber() + "\n" );

                            //Create RFC and add it to rfcList
                            final RFC rfc = new RFC(rfcNumber, title, client);
                            rfcList.add( rfc );


                            //Add RFC to client on clientList
                            for (int i = 0; i < clientList.size(); i++) {
                                if (clientList.get( i ).getHostname().equals( client.getHostname() )) {
                                    clientList.get( i ).getRfcList().add( rfc );
                                }
                            }

                        } else if (received.equals( "LOOKUP" )) {

                            // Obtain information of the RFC to Lookup
                            output.writeUTF( "RFC Number: " );
                            final String rfcString = input.readUTF();
                            int rfcNumber;
                            try {
                                rfcNumber = Integer.parseInt( rfcString );
                            } catch (final Exception e) {
                                output.writeUTF( "\nLookup Response Message:\n"
                                        + "P2P-CI/1.0 400 Bad Request\n"
                                        + "RFC " + rfcString + "\n" );
                                break;
                            }

                            String title = "";

                            for (int i = 0; i < rfcList.size(); i++) {
                                if (rfcList.get( i ).getRfcNumber() == rfcNumber) {
                                    title = rfcList.get( i ).getRfcTitle();
                                }
                            }

                            output.writeUTF( "\nLookup Request Message:\n"
                                    + "LOOKUP RFC " + rfcNumber + " P2P-Cl/1.0\n"
                                    + "Host: " + client.getHostname() + "\n"
                                    + "Port: " + client.getPortnumber() + "\n"
                                    + "Title: " + title + "\n");

                            // Find peers that have the specified RFC
                            String response = "";

                            // Check if rfc doesnt exist
                            if (title.isEmpty()) {
                                output.writeUTF( "Lookup Response Message:\n"
                                        + "P2P-CI/1.0 404 Not Found\n"
                                        + "RFC " + rfcNumber + " " + title + "\n" );
                                break;
                            }
                            for (int i = 0; i < clientList.size(); i++) {
                                final Client tempClient = clientList.get( i );
                                for (int j = 0; j < tempClient.getRfcList().size(); j++) {
                                    if (tempClient.getRfcList().get( j ).getRfcNumber() == rfcNumber ) {
                                        response = "Lookup Response Message:\n"
                                                + "P2P-CI/1.0 200 OK\n"
                                                + "RFC " + rfcNumber + " " + title
                                                + " " + tempClient.getPortnumber() + "\n";
                                        break;
                                    }
                                }
                            }

                            //System.out.println(response);

                            output.writeUTF( response );

                        } else if (received.equals( "LIST" )) {
                            String tosend = "";

                            for (int i = 0; i < rfcList.size(); i++) {
                                tosend = tosend + "RFC " + rfcList.get( i ).getRfcNumber()
                                        + " " + rfcList.get( i ).getRfcTitle() + " "
                                        + rfcList.get( i ).getClientList().get( 0 ).getHostname() + " "
                                        + rfcList.get( i ).getClientList().get( 0 ).getPortnumber() + "\n";
                            }

                            output.writeUTF( "\nList Request Message:\n"
                                    + "List ALL P2P-Cl/1.0\n"
                                    + "Host: " + client.getHostname() + "\n"
                                    + "Port: " + client.getPortnumber() + "\n\n"
                                    + "List Response Message: \n" + tosend);
                        } else {
                            output.writeUTF( "Invalid input\n" );
                        }
                        break;

                    default:
                        output.writeUTF("Invalid input\n");
                        break;
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // closing resources
            this.input.close();
            this.output.close();

        } catch(final IOException e) {
            e.printStackTrace();
        }
    }
}
package allcode;
// Java implementation for a client
// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Client portion of the peer-to-peer system
 * Each client has a hostname and portnumber
 * Reference for multithreading: https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
 *
 * @author Marcus Lerro
 *
 */
public class Client {

    // Hostname of the Client
    private String hostname;

    // Upload port of the Client
    private int portnumber;

    // Keeps track of the rfc's that a given client has
    private final ArrayList<RFC> rfcList = new ArrayList<RFC>();

    public Client() {

    }
    /**
     * Class constructor to identify a client connected to the server
     */
    public Client( final String hostname, final int portnumber ) {
        setHostname(hostname);
        setPortnumber(portnumber);
    }

    /**
     * Getter for hostname
     * @return the hostname
     */
    public String getHostname () {
        return hostname;
    }

    /**
     * Setter for hostname
     * @param hostname the hostname to set
     */
    public void setHostname ( final String hostname ) {
        this.hostname = hostname;
    }

    /**
     * Getter for portnumber
     * @return the portnumber
     */
    public int getPortnumber () {
        return portnumber;
    }

    /**
     * Setter for portnumber
     * @param portnumber the portnumber to set
     */
    public void setPortnumber ( final int portnumber ) {
        this.portnumber = portnumber;
    }

    /**
     * Adds an RFC to the existing rfc list of the client
     */
    public void addRFC (final RFC rfc) {
        rfcList.add( rfc );
    }

    /**
     * Removes an RFC from the existing rfc list of the client
     * @return rfc that was removed
     */
    public RFC removeRFC ( final int index) {
        return rfcList.remove( index );
    }

    /**
     * Returns the rfcList of the client
     * @return the rfcList
     */
    public ArrayList<RFC> getRfcList () {
        return rfcList;
    }
    public static void main(final String[] args) throws IOException {
        try {
            final Scanner scan = new Scanner(System.in);

            // getting localhost ip
            final InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 7734
            final Socket socket = new Socket(ip, 7734);

            // obtaining input and out streams
            final DataInputStream input = new DataInputStream(socket.getInputStream());
            final DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            String received = "";

            // the following loop performs the exchange of
            // information between client and client handler
            System.out.println(input.readUTF());
            while (true) {

                String tosend = "";
                if (!received.contains( "Lookup" ) && !received.contains( "400 Bad Request" )) {
                    tosend = scan.nextLine();
                    output.writeUTF(tosend);
                }


                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit")) {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                // printing date or time as requested by client
                received = input.readUTF();
                System.out.println(received);

                // Some cases will require this to continue interactions
                if (received.contains( "Invalid input" )) {
                    System.out.println(input.readUTF());
                }
                if (received.contains( "Add Request Message" )) {
                    System.out.println(input.readUTF());
                }
                if (received.contains( "List Request Message" )) {
                    System.out.println(input.readUTF());
                }
                if (received.contains( "Get Request Message" )) {
                    System.out.println(input.readUTF());
                }
            }
            // closing resources
            scan.close();
            input.close();
            output.close();
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
}
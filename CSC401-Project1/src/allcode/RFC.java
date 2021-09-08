package allcode;

import java.util.ArrayList;

/**
 * This class represents a RFC (Request for Comments) object. An RFC has its own
 * number, title, and peer that owns it
 *
 * @author Marcus Lerro
 *
 */
public class RFC {

    /** RFC number */
    private int rfcNumber;

    /** RFC string title */
    private String rfcTitle;

    /** A list of peers that that own this rfc */
    private final ArrayList<Client> clientList = new ArrayList<Client>();

    public RFC (final int rfcNumber, final String rfcTitle, final Client client) {
        setRfcNumber(rfcNumber);
        setRfcTitle(rfcTitle);
        clientList.add( client );
    }

    /**
     * Getter for RFC Number
     * @return the rfcNumber
     */
    public int getRfcNumber () {
        return rfcNumber;
    }

    /**
     * Setter for RFC Number
     * @param rfcNumber the rfcNumber to set
     */
    public void setRfcNumber ( final int rfcNumber ) {
        this.rfcNumber = rfcNumber;
    }

    /**
     * Getter for RFC Title
     * @return the rfcTitle
     */
    public String getRfcTitle () {
        return rfcTitle;
    }

    /**
     * Setter for RFC Title
     * @param rfcTitle the rfcTitle to set
     */
    public void setRfcTitle ( final String rfcTitle ) {
        this.rfcTitle = rfcTitle;
    }

    /**
     * Adds an Client to the existing client list of the RFC
     */
    public void addClient (final Client client) {
        clientList.add( client );
    }

    /**
     * Removes an Client from the existing client list of the rfc
     * @return client that was removed
     */
    public Client removeClient ( final int index ) {
        return clientList.remove( index );
    }

    /**
     * Returns the clientList of the rfc
     * @return the clientList
     */
    public ArrayList<Client> getClientList () {
        return clientList;
    }

}

Project 1 Guide

Marcus Lerro (mjlerro)


For my P2S/P2P system, I have 3 classes. The RFC class is a simple POJO that has 3 fields, a RFC number, a RFC title, and a list of clients that have it downloaded. 

The Server class acts as the median between all interactions. Sitting on port 7734, it will accept client connection and create a new thread for it. 

This allows it to handle multiple clients as each thread will handle the clients separately. 

Each of these threads have 2 static LinkedLists that keep track of currently connected clients and existing rfcs that can be downloaded.

When a client disconnects, the lists will change accordingly and the server will acknowledge the disconnection. 

The Client class represents a single peer and has hostname, portnumber, and list of RFC’s. 

When run, it will connect to the server on port 7734 (produces an error if server is not running) and allow a number of interactions for P2S and P2P. 


Prerequisites:
	
	- Eclipse is recommended, but not required (I did everything on Eclipse)
	
	- Gitbash or Putty installed

Environment Setup:

	1. Download the file onto your Package Explorer on Eclipse
	
	2. Once it’s on your Package Explorer, right click the project -> Show in Local Terminal -> Choose your terminal of choice (GitBash is what I used, Putty would work well also)
	3. Repeat step 2. two more times until you have three separate Terminals open
	
	4. On one of the terminals: (Server)
		
		cd src
		
		cd allcode
		
		javac -cp ../ Server.java
		
		java -cp ../ allcode/Server
	
	5. On the other two terminals: (Clients)
		
		cd src
		
		cd allcode
		
		javac -cp ../ Client.java
		
		java -cp ../ allcode/Client


Now you have one Server running and two Clients running. The Clients perform all of the interactions while the Server acts as a median. 
The Server will also keep track of Clients connecting/disconnecting through its console. 
If you would like to add more Clients, feel free to do so by repeating the above steps.


Navigating the Program:
	
	As for navigation, I created 2 options for the clients, P2P and P2S. 
	
	If P2S (Peer-to-Server) is selected, the client will have the ADD, LOOKUP, and LIST options to select from.
	
	If P2P (Peer-to-Peer) is selected, the client will have the GET option to select from.
	
	Each action shows the request message sent to the server and the response message from the server.
	
	If you put incorrect input or request a non existent RFC, then you should receive an appropriate status code from the server's response 
	
	Once an interaction is finished, it will bring you back to the starting text. The program keeps running until a client type 'Exit'

import java.io.*;
import java.net.*;


public class UDPServerThread extends Thread {
	
	private DatagramSocket udpSocket = null;
	private boolean chat = true;
	
	public UDPServerThread() throws IOException{
		this("UDPServerThread");
	}
	
	public UDPServerThread(String name) throws IOException{
		super(name);
		udpSocket = new DatagramSocket(8804);
	}
	
	public void run(){
		
		String message;
		Log log = Log.getInstance();
		System.out.println("Listening on port 8804... \n");
		try {
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));
			
			while(chat){
				byte[] dataSent = new byte[2048];
				byte[] dataReceived = new byte[2048];
				DatagramPacket packetReceived = new DatagramPacket(dataReceived, dataReceived.length);
				DatagramPacket packetSent = null;
				udpSocket.receive(packetReceived);
				log.writeMessage("Received data from: "+packetReceived.getAddress());				
				message = new String(packetReceived.getData());
				log.writeMessage("Server", message); 
				
				log.replyRequest();
				String replyMessage = bfReader.readLine();
				
				dataSent = replyMessage.getBytes();
				InetAddress iAddress = packetReceived.getAddress();
				int clientPort = packetReceived.getPort();
				packetSent = new DatagramPacket(dataSent, dataSent.length, iAddress, clientPort);
				udpSocket.send(packetSent); // after receiving notification of message sent write message to log
				log.writeMessage("userName", replyMessage);
				if(replyMessage.equals("Shutting down server.")){
					log.writeMessage("Server has been shut down.. \n");
					chat = false;
					udpSocket.close();
				}
			}
			
		}
		catch(BindException be){
			log.writeMessage("Can't use port.");
		}
		catch(IOException ioe){
			System.out.println("IOException: " + ioe.getMessage());
		}
		
	}
	
}

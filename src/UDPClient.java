import java.io.*;
import java.net.*;


public class UDPClient {

	public static void main(String args[]) throws Exception
	  {
		startClient();
	  }

	public static void startClient(){
		String message = null;
		String replyMessage;
		byte[] dataReceived = new byte[2048];
		byte[] dataSent = new byte[2048];
		Log log = Log.getInstance();
		boolean runClient = true;

		try{
			DatagramSocket udpClientSocket = new DatagramSocket();
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));
			DatagramPacket packetToSend;
			DatagramPacket packetToReceive;
			InetAddress iAddress = null;
			String username = null;
			while(runClient){
				if(message == null) {
					iAddress = findInetAddress();
					log.writeMessage("You are connected to: " +iAddress);
					log.writeMessage("Enter a username to talk with: ");
					username = bfReader.readLine();
					log.writeMessage(username+", please enter a message: ");
				}
				else{ log.replyRequest();}
				message = bfReader.readLine();
				dataSent = message.getBytes();
				packetToSend = new DatagramPacket(dataSent, dataSent.length, iAddress, 8804);
				udpClientSocket.send(packetToSend);
				if(message.equals("Logout")){
					log.writeMessage(username+ " is logging off..");
					runClient = false;
					udpClientSocket.close();
				}//end if
				else{
				log.writeMessage(username, message);
				packetToReceive = new DatagramPacket(dataReceived, dataReceived.length);
				udpClientSocket.receive(packetToReceive);
				replyMessage = new String(packetToReceive.getData());
				log.writeMessage("Server", replyMessage);
				log.writeMessage("Your reply is: ");
				}// end else
			}// end while
		}// end try
		catch(Exception e){
			e.printStackTrace();
		}// end catch
	}// end startClient

	public static InetAddress findInetAddress() throws IOException{
		String host = null;
		BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));
		InetAddress iAddress = null;
		boolean hostFound = false;
		while(!hostFound){
		try{
			System.out.print("\n\nEnter a host name: ");
			host = bfReader.readLine();
			iAddress = InetAddress.getByName(host);
			hostFound = true;
		}catch(UnknownHostException uhe) {System.out.println("Could not find host: "+host);}
		}
		return iAddress;
	}
}// end class

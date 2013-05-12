import java.io.*;
import java.net.*;


public class UDPClient {

	public static void main(String args[]) throws Exception
	  {
		startClient();
	  }

	public static void startClient(){
		String message = null;
		String replyMessage = null;
		Log log = Log.getInstance();
		boolean runClient = true;

		try{
			DatagramSocket udpClientSocket = new DatagramSocket();
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));
			DatagramPacket packetToSend;
			DatagramPacket packetToReceive;
			InetAddress iAddress = null;
			String username = null;
			int seqNum = 0;
			while(runClient){
				byte[] dataReceived = new byte[2048];
				byte[] dataSent = new byte[2048];
				if(message == null) {
					iAddress = findInetAddress();
					log.writeMessage("You are connected to: " +iAddress);
					log.writeMessage("Enter a username to identify yourself: ");
					username = bfReader.readLine();
					log.writeMessage(username+", please enter a message: ");
				}
				else{log.replyRequest();}
				
				message = null;
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
				UDPClient.receiveAck(seqNum, packetToSend, udpClientSocket);
				
				packetToReceive = new DatagramPacket(dataReceived, dataReceived.length);
				udpClientSocket.receive(packetToReceive);
				int udpServerPort = packetToReceive.getPort();
				replyMessage = new String(packetToReceive.getData());
				log.writeMessage("User", replyMessage);
				UDPClient.sendAck(seqNum, iAddress, udpServerPort, udpClientSocket);
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
	
	private static void sendAck(int seqNum, InetAddress returnAddress, int port, DatagramSocket udpSocket) throws IOException{
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOutputStream);
		dos.writeShort(seqNum);
		dos.flush();
		byte[] sendArray = byteOutputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(sendArray, sendArray.length, returnAddress, port);
		udpSocket.send(sendPacket);
	}
	
	private static void receiveAck(int seqNum, DatagramPacket sendPacket, DatagramSocket udpSocket) throws IOException {
		byte[] ack = new byte[2];
		boolean received = false;
		int receivedSeqNum = 0;
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(ack);
		DataInputStream dis = new DataInputStream(byteInputStream);
		DatagramPacket receivedPacket = new DatagramPacket(ack, ack.length);
		Log log = Log.getInstance();
		while(!received){
			udpSocket.receive(receivedPacket);
			receivedSeqNum = dis.readShort();
			log.writeMessage(receivedSeqNum+"");
			if(receivedSeqNum == seqNum) received = true; 
			else {
				udpSocket.send(sendPacket);
				log.writeMessage("Did not receive correct packet, trying again");
			}
		}
	}
}// end class

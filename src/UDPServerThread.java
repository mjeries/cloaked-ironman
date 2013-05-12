import java.io.*;
import java.net.*;


public class UDPServerThread extends Thread {
	
	private DatagramSocket udpSocket = null;
	private boolean chat = true;
	String message = null;
	int seqNum = 0;
	
	@Override
	public void run(){
		
		Log log = Log.getInstance();
		System.out.println("Listening on port 8804... \n");
		try {
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(System.in));		
			udpSocket = new DatagramSocket(8804);
			while(chat){
				byte[] dataSent = new byte[2048];
				byte[] dataReceived = new byte[2048];
				DatagramPacket packetReceived = new DatagramPacket(dataReceived, dataReceived.length);
				DatagramPacket packetSent = null;
				String replyMessage = null;
				udpSocket.receive(packetReceived);
				if(message == null) log.writeMessage("Received data from: "+packetReceived.getAddress());		
				UDPServerThread.sendAck(seqNum, packetReceived.getAddress(), packetReceived.getPort(), udpSocket);
				message = new String(packetReceived.getData());
				log.writeMessage("User", message); 
				
				log.replyRequest();
				replyMessage = bfReader.readLine();
				if(replyMessage.equals("Shut down.")){
					replyMessage = "Server is being shut down.";
					dataSent = replyMessage.getBytes();
					InetAddress iAddress = packetReceived.getAddress();
					int clientPort = packetReceived.getPort();
					packetSent = new DatagramPacket(dataSent, dataSent.length, iAddress, clientPort);
					udpSocket.send(packetSent);
					log.writeMessage("Server has been shut down.. \n");
					chat = false;
					udpSocket.close();
				}// end if
				
				dataSent = replyMessage.getBytes();
				InetAddress iAddress = packetReceived.getAddress();
				int clientPort = packetReceived.getPort();
				packetSent = new DatagramPacket(dataSent, dataSent.length, iAddress, clientPort);
				udpSocket.send(packetSent); 
				log.writeMessage("Server", new String(packetSent.getData()));
				UDPServerThread.receiveAck(seqNum, packetSent, udpSocket);
			}
			
		}
		catch(BindException be){
			log.writeMessage("Can't use port.");
		}
		catch(IOException ioe){
			System.out.println("IOException: " + ioe.getMessage());
		}
		catch(NullPointerException npe){
			npe.printStackTrace();
		}
		
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
	
}

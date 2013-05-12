import java.io.IOException;

public class UDPServer {

	public static void main(String[] args) throws IOException{
		UDPServerThread serverThread = new UDPServerThread();
		Thread t = new Thread(serverThread);
		t.start();
	}

}

import java.net.*;
import java.io.*;
public class ClientLoadBalance {

	private static final int TIMEOUT = 100;
	private static final String HOST = "localhost";
	private static final int PORT = 2001;

	public static void main(String[] args) throws IOException {
		int portTcp = 0;
		BufferedReader console = 
				new BufferedReader(new InputStreamReader(System.in));
		byte[] arrayReceive = new byte[1500];
		DatagramPacket packetReceive = 
							new DatagramPacket(arrayReceive, arrayReceive.length);
		
		try(DatagramSocket socket = new DatagramSocket()){
			socket.setSoTimeout(TIMEOUT);
			boolean run = true;
			while (run) {
				byte []arraySend = "get port".getBytes();
				DatagramPacket packetSend = new DatagramPacket(arraySend, arraySend.length,
						InetAddress.getByName(HOST), PORT);
				boolean flag = true;
				while (true) {
					try {
						socket.send(packetSend);
						socket.receive(packetReceive);
						String str = new String(arrayReceive, 0, packetReceive.getLength());
						portTcp = Integer.parseInt(str);
						if(portTcp == 0) continue; 
						else break;
					} catch (IOException e) {
						if(flag){
							System.out.println("Waiting free server - time out " + TIMEOUT);
							flag = false;
						}
					} 
				}
				
				try (	Socket socketTcp = new Socket("localhost",portTcp);
						BufferedReader socketFrom=new BufferedReader(new InputStreamReader(socketTcp.getInputStream()));
						PrintStream socketTo=new PrintStream(socketTcp.getOutputStream())){
					System.out.println("connected to server on port " + portTcp);
					while(true){
						System.out.println("enter operation and tow operands or exit");
						String line=console.readLine();
						if(line == null || line.equals("exit")){
							run = false;
							break;
						}
						socketTo.println(line);
						String outline=socketFrom.readLine();
						if(outline==null)
							break;
						System.out.println(outline);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}

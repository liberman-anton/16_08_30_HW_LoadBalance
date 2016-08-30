import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class LoadBalance {

	private static final int PORT = 2001;
	private static final int MAX_RECEIVE = 1500;
	
	public static HashMap<Integer,Integer> mapOfPorts= new HashMap<Integer,Integer>();

	public static void main(String[] args) {
		
		try(DatagramSocket socket = new DatagramSocket(PORT)){
			System.out.println("LoadBalance started");
			byte arrayReceive[] = new byte[MAX_RECEIVE];
			byte arraySend[];
			DatagramPacket packetReceive = 
					new DatagramPacket(arrayReceive, arrayReceive.length);
//			int lastPort = 0;
			while(true){			
				socket.receive(packetReceive);
//				if(lastPort == packetReceive.getPort())
//					continue;
			/*	lastPort = */packetReceive.getPort();
				String str = new String(arrayReceive,0,packetReceive.getLength());
				
				if("get port".equals(str) && !mapOfPorts.isEmpty()){
					//send port	
					String freePort = getPortTcp();
					arraySend = freePort.getBytes();
					DatagramPacket packetSend = new DatagramPacket(arraySend, arraySend.length, 
						packetReceive.getAddress(), packetReceive.getPort());
					socket.send(packetSend);
					mapOfPorts.put(Integer.parseInt(getPortTcp()), 1);
					continue;
				}
				if(!str.isEmpty() && Integer.parseInt(str) > 0){
					Integer portTcp = Integer.parseInt(str);
					putPort(portTcp);
					System.out.println("received port tcp " + portTcp);
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void putPort(Integer newPort) {
		Integer countOfClients = 0;
		mapOfPorts.put(newPort, countOfClients);
	}

	private static String getPortTcp() {
		Integer resPort = 0;
			for(Entry<Integer, Integer> entry : mapOfPorts.entrySet()){
				if(entry.getValue() == 0){
					resPort = entry.getKey();
				}
			}
		return resPort.toString();
	}
}

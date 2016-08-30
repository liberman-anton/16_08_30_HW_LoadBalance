import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
public class CalculatorServerTcp {
	
	public interface IOperations {
		public double operate(double a,double b);
		}
	
	private static HashMap<String,IOperations> operations = new HashMap<>();
	static{
		operations.put("+", (a,b) -> a + b);
		operations.put("-", (a,b) -> a - b);
		operations.put("*", (a,b) -> a * b);
		operations.put("/", (a,b) -> b == 0 ? 0 : a / b);
	}
	private static final String DELIMETER = " ";
	private static final String WRONG_RESULT = "WRONG_RESULT";

	private static final int PORT = 0;
	private static final int TIMEOUT = 100;
	private static final String HOST = "localhost";
	private static final int PORT_LOAD_BALANCE = 2001;

	public static void main(String[] args) throws Exception{
		ServerSocket serverSocket=new ServerSocket(PORT);
		int localPort = serverSocket.getLocalPort();
		System.out.println("server started on port: " + localPort);
		Socket socket=null;
		while(true){
			sendPortToLoadBalance(localPort);
			socket=serverSocket.accept();
			BufferedReader reader=
		new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream writer=new PrintStream(socket.getOutputStream());
			while (true) {
				String line;
				try {
					line = reader.readLine();
				} catch (Exception e) {
					break;
				}
				if (line == null)
					break;
				writer.println(getResult(line));
			}
		}
		
	}

	private static void sendPortToLoadBalance(int portTcp) throws UnknownHostException {
		try(DatagramSocket socket = new DatagramSocket()){
			socket.setSoTimeout(TIMEOUT);
				byte []arraySend = ((Integer)portTcp).toString().getBytes();
				DatagramPacket packetSend = new DatagramPacket(arraySend, arraySend.length,
						InetAddress.getByName(HOST), PORT_LOAD_BALANCE);
				
				while (true) {
					try {
						socket.send(packetSend);
						break;
					} catch (IOException e) {
						System.out.println("time out " + TIMEOUT);
					} 
				}			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}

	private static String getResult(String line) {
		String[] operands = line.split(DELIMETER);
		String resStr = WRONG_RESULT;
		try {
			double first = Double.parseDouble(operands[1]);
			double second = Double.parseDouble(operands[2]);
			String code = operands[0];
			double res = operations.get(code).operate(first,second);
			resStr = Double.toString(res);
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
		return resStr;
	}
}

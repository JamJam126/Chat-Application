package client;

public class ChatClient {
	
	private static String SERVER_ADDRESS = "192.168.211.1";
	private static int SERVER_PORT = 3001;
	
	public static void main(String[] args){
//		System.out.println("Client");
		Connection con = new Connection(SERVER_ADDRESS, SERVER_PORT);
		con.run();
		
	}
}

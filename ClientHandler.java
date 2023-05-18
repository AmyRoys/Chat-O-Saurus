import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable{
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUsername;
	
	
	public ClientHandler(Socket socket) {
		try{
			this.socket = socket;
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername = bufferedReader.readLine();
			clientHandlers.add(this);
			broadcastMessage("Server: " + this.clientUsername + " has joined the chat.");
			
		}catch(Exception e) {
			closeEverything(socket, bufferedWriter, bufferedReader);
		}
		
	}
	@Override
	public void run(){
		String message;
		while(socket.isConnected()){
			try{
				message = bufferedReader.readLine();
				if(message.equals("exit")){
					closeEverything(socket, bufferedWriter, bufferedReader);
					break;
				}
				broadcastMessage(message);
			}catch(Exception e){
				closeEverything(socket, bufferedWriter, bufferedReader);
				break;
			}
		}
	}
	public void broadcastMessage(String messageToSend){
		for(ClientHandler clientHandler : clientHandlers){
			try{
				clientHandler.bufferedWriter.write(messageToSend);
				clientHandler.bufferedWriter.newLine();
				clientHandler.bufferedWriter.flush();
			}catch(Exception e){
				closeEverything(socket, bufferedWriter, bufferedReader);
			}
		}
	}
	public void removeClientHandler(){
		clientHandlers.remove(this);
		broadcastMessage("Server: " + this.clientUsername + " has left the chat.");
	}
	public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
		try{
			if(bufferedReader != null){
				bufferedReader.close();
			}
			if(bufferedWriter != null){
				bufferedWriter.close();
			}
			if(socket != null){
				socket.close();
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
}

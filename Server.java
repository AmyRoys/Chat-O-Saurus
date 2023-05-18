import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



public class Server {
    private static ServerSocket serverSocket;
    
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    public void start() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for a client to connect");
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Queue<String> messageQueue = new ConcurrentLinkedQueue<String>();
    private static Queue<String> responseQueue = new ConcurrentLinkedQueue<String>();
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6013);
            Server server = new Server(serverSocket);
            server.start();
            
            while (true) {
                Socket socket = new Socket("localhost", 6013);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input = in.readLine(); // Read the input from the client
                //add clients input to queue
                messageQueue.add(input);
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Call the OpenAIApiCaller method with the input parameter
                out.println("Message received by server.");
                
                String response = null;
                try {
                    response = OpenAIApiCaller.callOpenAIApi(input);
                } catch (URISyntaxException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                double score = ClaimBusterAPI.getClaimBusterScore(response);
                String score_string = "";
                String scoreString = (score >= 0.4) ? "%This response may not need to be fact checked." : "%This response may need to be fact checked.";
                String formattedScore = String.format("%.2f", score);
                String formattedResponse = response + "%ClaimBuster score: " + formattedScore + " " + scoreString + "%";
                
                responseQueue.add(formattedResponse);
                String format = String.format("%.2f", score);
                
                format = "%ClaimBuster score: " + format + " " + score_string + "%";
                String new_response = response + format;
                out.println(new_response); // Send the response back to the client
                
                for (String message : messageQueue) {
                    if (!message.equals(formattedResponse)) {
                        out.println("Received message: " + message);
                    }
                }
                for (String responseMessage : responseQueue) {
                    out.println("Received response: " + responseMessage);
                }
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

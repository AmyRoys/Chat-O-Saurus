import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static Queue<String> messageQueue = new ConcurrentLinkedQueue<String>();
    private static Queue<String> responseQueue = new ConcurrentLinkedQueue<String>();
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6013);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Start a new thread for each client connection
                Thread clientThread = new ClientThread(clientSocket);
                clientThread.start();
                System.out.println("Client connected");
                System.out.println(serverSocket.getInetAddress());
                
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String input = in.readLine(); // Read the input from the client
                //add clients input to queue
                messageQueue.add(input);
                
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
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
                
                clientSocket.close();
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
    
    static class ClientThread extends Thread {
        private Socket clientSocket;
        
        public ClientThread(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try {
                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
    }
}

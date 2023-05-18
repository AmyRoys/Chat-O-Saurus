import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static Queue<String> messageQueue = new ConcurrentLinkedQueue<String>();
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
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                double score = ClaimBusterAPI.getClaimBusterScore(response);
                String score_string = "";
                if (score >= .4){
                    score_string = "%This response may not need to be fact checked.";
                } else {
                    score_string = "%This response may need to be fact checked.";
                }
                String format = String.format("%.2f", score);

                format = "%ClaimBuster score: " + format + " " + score_string + "%";
                String new_response = response + format;
                out.println(new_response); // Send the response back to the client
            
                messageQueue.add(new_response);
                
                for (String message : messageQueue) {
                    out.println("Received message: " + message);
                }
                
                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
    }
}

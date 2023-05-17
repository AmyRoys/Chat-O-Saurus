import java.io.*;
import java.net.*;

public class Server {
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

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                // Call the OpenAIApiCaller method with the input parameter
                String response = null;
                try {
                    response = OpenAIApiCaller.callOpenAIApi(input);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                out.println(response); // Send the response back to the client

                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
    }
}

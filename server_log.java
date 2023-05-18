import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class server_log {
    public static void main(String[] args) {
        try {
            final PipedOutputStream output = new PipedOutputStream();
            final PipedInputStream  input  = new PipedInputStream(output);
            ServerSocket serverSocket = new ServerSocket(6013);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Start a new thread for each client connection
                Thread clientThread = new ClientThread(clientSocket, output);
                Thread secondThread = new secondThread(clientSocket, input);
                clientThread.start();
                secondThread.start();
                System.out.println("Client connected");
                System.out.println(serverSocket.getInetAddress());
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    static class ClientThread extends Thread{
        private Socket clientSocket;
        private PipedOutputStream output;

        public ClientThread(Socket socket, PipedOutputStream output)  {
            this.clientSocket = socket;
            this.output = output;

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
                double score = ClaimBusterAPI.getClaimBusterScore(response);

                response = response + " | ClaimBuster score: " + score;
                // Send to second thread here
                try {
                    output.write(response.getBytes());
                } catch (IOException e) {
                }

                out.println(response); // Send the response back to the client

                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }
    }

    static class secondThread extends Thread{
        private Socket clientSocket;
        private PipedInputStream input;

        public secondThread(Socket socket, PipedInputStream input)  {
            this.clientSocket = socket;
            this.input = input;
        }

        @Override
        public void run() {
            final Logger logger = Logger.getLogger(String.valueOf(server_log.class));
            StringBuilder modifiedData = null;
            try {
                int data = input.read();
                modifiedData = new StringBuilder();

                while (data != -1) {
                    char character = (char) data;
                    modifiedData.append(character);
                    data = input.read();
                }

            } catch (IOException e) {
            }

            try (FileWriter f = new FileWriter("log.txt", true);
                 BufferedWriter b = new BufferedWriter(f);
                 PrintWriter p = new PrintWriter(b);) {

                p.println(modifiedData.toString());
                p.println("---------------------------");

            } catch (IOException i) {
                i.printStackTrace();
            }

            }
        }
    }

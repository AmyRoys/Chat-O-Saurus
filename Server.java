import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        try {
            ServerSocket sock = new ServerSocket(6013);
            // now listen for connections
            while (true) {
                Socket client = sock.accept();
                // we have a connection
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String input = in.readLine(); // Read the input from the client

                PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
                // call the OpenAIApiCaller method with the input parameter
                String response = OpenAIApiCaller.callOpenAIApi(input);
                pout.println(response); // Send the response back to the client

                // close the socket and resume listening for more connections
                client.close();
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}

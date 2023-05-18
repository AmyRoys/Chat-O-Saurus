import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("127.0.0.1", 6013);
            OutputStream out = sock.getOutputStream();
            PrintWriter pout = new PrintWriter(out, true);

            // Read the input from the user
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter input: ");
            String input = userInput.readLine();

            // Send the input to the server
            pout.println(input);

            // Read the response from the server
            InputStream in = sock.getInputStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            String response = bin.readLine();
            String result = response.replaceAll(".%", ".\n");
            System.out.println("Server response: \n" + result);

            sock.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
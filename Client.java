import java.net.*;
import java.io.*;
import java.net.Socket;

public class Client {
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String clientUsername;
        
        public Client(Socket socket, String clientUsername){
            try{
                this.socket = socket;
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.clientUsername = clientUsername;
            }catch(Exception e){
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
        public void sendMessage(){
            try{
                bufferedWriter.write(clientUsername);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                
                Scanner scanner = new Scanner(System.in);
                while(socket.isConnected()){
                    String message = scanner.nextLine();
                    bufferedWriter.write(username + ':' + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }catch (Exception e){
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    
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
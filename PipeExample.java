import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Scanner;

public class PipeExample {

    public static void main(String[] args) throws IOException {
        final PipedOutputStream pipeOutput = new PipedOutputStream();
        final PipedInputStream pipeInput = new PipedInputStream(pipeOutput);

        final ServerSocket serverSocket = new ServerSocket(1234);
        final Socket clientSocket = new Socket("localhost", 1234);
        final Socket serverClientSocket = serverSocket.accept();

        Thread pipeThread = new Thread(() -> {
            try {
                int data = pipeInput.read();
                StringBuilder modifiedData = new StringBuilder();

                while (data != -1) {
                    char character = (char) data;
                    modifiedData.append(character);
                    data = pipeInput.read();
                }
                String response = OpenAIApiCaller.callOpenAIApi(modifiedData.toString());
                OutputStream outputStream = serverClientSocket.getOutputStream();
                outputStream.write(response.getBytes());
                outputStream.close();
                serverClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread socketThread = new Thread(() -> {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = pipeOutput;
                Scanner sc = new Scanner(System.in);
                System.out.print("You are in Thread 2 - Enter your prompt: ");
                String prompt = sc.nextLine();
                outputStream.write(prompt.getBytes());
                outputStream.close();
                int data = inputStream.read();
                while (data != -1) {
                    System.out.print((char) data);
                    data = inputStream.read();
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        pipeThread.start();
        socketThread.start();
    }
}

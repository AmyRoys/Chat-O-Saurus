import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

public class PipeExample {

    public static void main(String[] args) throws IOException {

        final PipedOutputStream output1 = new PipedOutputStream();
        final PipedInputStream input1 = new PipedInputStream(output1);
        final PipedOutputStream output2 = new PipedOutputStream();
        final PipedInputStream input2 = new PipedInputStream(output2);

        Thread thread1 = new Thread(() -> {
            try {
                int data = input1.read();
                StringBuilder modifiedData = new StringBuilder();

                while (data != -1) {
                    char character = (char) data;
                    modifiedData.append(character);
                    data = input1.read();
                }
                String response = OpenAIApiCaller.callOpenAIApi(modifiedData.toString());
                output2.write(response.getBytes());
                output2.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("You are in Thread 2 - Enter your prompt: ");
                String prompt = sc.nextLine();
                output1.write(prompt.getBytes());
                output1.close();
                int data = input2.read();
                while (data != -1) {
                    System.out.print((char) data);
                    data = input2.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread2.start();
        thread1.start();
    }
}

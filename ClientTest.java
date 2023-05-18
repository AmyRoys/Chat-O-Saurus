import org.junit.jupiter.api.*;
import java.io.*;
import java.net.Socket;

public class ClientTest {
    private static final int PORT = 6013;
    private static final String HOST = "localhost";

    private static Socket serverSocket;
    private static Client client;

    @BeforeAll
    public static void setUp() throws IOException {
        // Start the server
        serverSocket = new Socket(HOST, PORT);

        // Create a new client instance
        client = new Client(serverSocket, "TestUser");
    }

    @AfterAll
    public static void tearDown() {
        // Close the server socket and cleanup
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCloseEverything() throws IOException {
        // Create a mock Socket, PipedInputStream, PipedOutputStream, BufferedReader, and BufferedWriter
        Socket mockSocket = new Socket();
        PipedOutputStream mockOutputStream = new PipedOutputStream();
        PipedInputStream mockInputStream = new PipedInputStream(mockOutputStream);
        BufferedReader mockBufferedReader = new BufferedReader(new InputStreamReader(mockInputStream));
        BufferedWriter mockBufferedWriter = new BufferedWriter(new OutputStreamWriter(mockOutputStream));

        // Call the closeEverything method
        client.closeEverything(mockSocket, mockBufferedReader, mockBufferedWriter);

        // Verify that the Socket, BufferedReader, and BufferedWriter are closed
        Assertions.assertTrue(mockSocket.isClosed());
        Assertions.assertThrows(IOException.class, mockBufferedReader::readLine); // Ensure BufferedReader is closed
        Assertions.assertThrows(IOException.class, () -> mockBufferedWriter.write("Test Output")); // Ensure BufferedWriter is closed
    }

    @Test
    public void testListenForMessages() throws IOException {
        // Set up input and output streams for testing
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Server Message\n".getBytes());
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));

        // Call the listenForMessages method in a separate thread
        Thread thread = new Thread(() -> {
            client.listenForMessages();
        });
        thread.start();

        // Simulate a server message
        serverSocket.getOutputStream().write("Server Message\n".getBytes());
        serverSocket.getOutputStream().flush();

        // Wait for the thread to finish
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Validate the output
        String expectedOutput = "";
        Assertions.assertEquals(expectedOutput, outputStream.toString().trim());
    }
}

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    public static void main(String[] args) {
        String host = "localhost"; // Replace with the target server IP
        int port = 9090; // Internal TCP port

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server node at port " + port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a test message
            out.println("Hello from another server node!");

            // Read the response
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
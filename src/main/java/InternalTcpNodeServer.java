import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InternalTcpServer {
    public static void main(String[] args) {
        int port = 9090; // Internal TCP port for node communication

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP server listening on port " + port);

            while (true) {
                // Accept incoming connections from other nodes
                Socket socket = serverSocket.accept();
                System.out.println("Connected to a new server node.");

                // Handle communication in a separate thread
                new Thread(new NodeHandler(socket)).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

// Handler for each node connection
class NodeHandler implements Runnable {
    private Socket socket;

    public NodeHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from node: " + message);
                
                // Respond to the node
                out.println("Echo from TCP server: " + message);

                // Break connection on specific command (optional)
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Node disconnected.");
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {

    System.out.println("Logs from your program will appear here!");

    // Try with resources - Java 7 implements AutoCloseable on I/O streams.
     try(ServerSocket serverSocket = new ServerSocket(4221)) {
       serverSocket.setReuseAddress(true);

       // Accepting connections infinitely.
       while(true) {
           Socket socket = serverSocket.accept();
           // Need a logger here, will add that soon.
           System.out.println("New connection from " + socket.getRemoteSocketAddress());
           new Thread(new SocketHandler(socket)).start();
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

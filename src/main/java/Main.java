import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

     try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Makes sure that we dont get any issues about busy ports.
       serverSocket.setReuseAddress(true);

       Socket client = serverSocket.accept();
       client.getOutputStream().write("HTTP/1.1 200 OK\\r\\n\\r\\n".getBytes());
       System.out.println("accepted new connection");
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

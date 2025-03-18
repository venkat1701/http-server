import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {

    System.out.println("Logs from your program will appear here!");

     try {
       ServerSocket serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);

       //Accepting a connection
       Socket client = serverSocket.accept();
       URLParser parser = new URLParser(client.getInputStream());
       var outputStream = client.getOutputStream();
       outputStream.write(parser.respondToClient().getBytes());

       System.out.println("accepted new connection");
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

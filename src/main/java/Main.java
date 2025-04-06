import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {

    System.out.println("Logs from your program will appear here!");

     try {
       ServerSocket serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);

       //Accepting a connection
       while(true) {
           Socket client = serverSocket.accept();
           BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
           String request = in.readLine();
           if (request.split(" ")[1].equals("/")) {
               client.getOutputStream().write(ResponseStatus.ACCEPTED.getResponse().getBytes());
           } else {
               client.getOutputStream().write(ResponseStatus.NOT_FOUND.getResponse().getBytes());
           }

           System.out.println("accepted new connection");
           client.shutdownOutput();
       }
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

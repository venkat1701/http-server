import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        ExecutorService executor = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket client = serverSocket.accept();

                executor.submit(() -> {
                    try {
                        URLParser parser = new URLParser();
                        parser.handleRequest(client, args);
                    } catch (IOException e) {
                        System.out.println("Error handling client: " + e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
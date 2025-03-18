import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketHandler implements Runnable{
    private static final Logger logger = Logger.getLogger("SocketHandler");
    private final Socket client;

    public SocketHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try(InputStream inputStream = client.getInputStream(); OutputStream outputStream = client.getOutputStream()) {
            URLParser parser = new URLParser(inputStream);
            String response = parser.respondToClient();
            outputStream.write(response.getBytes());
            outputStream.flush();
            this.client.close();
        } catch(IOException ioe) {
            logger.warning(ioe.toString());
        }
    }
}

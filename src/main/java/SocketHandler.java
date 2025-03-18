import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * SocketHandler brings out each client connection on a separate kernel thread. Inside of their run method,
 * we parse the request using a URLParser to get the parts of the Http Request.
 * For instance, let's say we type this command in terminal <pre>curl -v http://localhost:4221/abcd</pre>
 * Every HTTP Request is made up of 3 parts: Request line, zero or more headers ending with CRLF and an optional request body
 * <pre>GET /index.html HTTP/1.1\r\nHost: localhost:4221\r\nUser-Agent: curl/7.64.1\r\nAccept: CRLF</pre>
 * The request target specifies the URL path for this request. Here the URL path is <strong>/index.html</strong>
 * This parsing is done by URL Parser.
 * @see URLParser
 * @author Venkat
 */
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

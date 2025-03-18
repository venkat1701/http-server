import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * URLParser is responsible for parsing the request target in an HTTP request. It contains a map of resources that are available with the dummy server.
 * What we do here is simple. We simply fetch the input stream of the connection and extract the request target to check whether it exists in the resource map or not. Well if it is available, then
 * we return a ResponseStatus of it being available otherwise a 404 Not Found.
 * One peculiar thing to note is that the request might or might not end with a CRLF. That means, it might not have an EOF marker. In that case, it's our responsibility to add one from our end in order
 * to ensure better flow of program control.
 * @author Venkat
 * @see ResponseStatus
 */
public class URLParser {
    private final static Logger logger = Logger.getLogger(URLParser.class.getName());
    private final InputStream inputStream;
    private final Map<String, String> resources;

    public URLParser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.resources = new HashMap<>();
        this.resources.put("/", "");
    }

    public String respondToClient() {
        String path = this.getRequestedPath();
        if(path != null && this.resources.containsKey(path)) {
            return ResponseStatus.ACCEPTED.getResponse();
        } else return ResponseStatus.NOT_FOUND.getResponse();
    }

    public String getRequestedPath() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(this.inputStream))) {
            String line = br.readLine();
            if(line == null) return null;

            String[] parts = line.split(" ");
            if(parts.length == 2) {
                line = line + "\r\n";
                parts = line.split(" ");
            }

            return parts[1];
        } catch(IOException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class URLParser {
    private String requestLine;
    private final Map<String, String> resources;

    public URLParser(String requestLine) {
        this.requestLine = requestLine;
        this.resources = new HashMap<String, String>();
        this.resources.put("/", "");
        this.resources.put("/echo", "");
    }

    public boolean checkResourceExistsInServer() {
        if(this.requestLine == null) {
            return false;
        }
        String[] parts = this.requestLine.split(" ");
        if(parts.length < 2) {
            return false;
        } else {
            return this.resources.containsKey(parts[1]) || parts[1].startsWith("/echo");
        }
    }

    public String echoResource() {
        String[] parts = this.requestLine.split(" ");
        if(parts[1].startsWith("/echo")) {
            return parts[1].split("/")[2];
        } else return null;
    }

    public String generateCRLFStringForResource(String resource, ResponseStatus status) {
        if(status == ResponseStatus.ACCEPTED) {
            return ResponseStatus.ACCEPTED.getResponse() + String.format(
                    "Content-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s", resource.length(), resource
            );
        } else return ResponseStatus.NOT_FOUND.getResponse();
    }

    public String respondToClient() {
        if(this.checkResourceExistsInServer()) {
            return ResponseStatus.ACCEPTED.getResponse();
        } else return ResponseStatus.NOT_FOUND.getResponse();
    }

}

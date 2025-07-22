import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    public final String method;
    public final String path;
    public final String version;
    public final Map<String, String> headers;
    public final String body;

    private HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest parseFromSocket(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.trim()
            .isEmpty()) {
            throw new IOException("Empty request line");
        }

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            throw new IOException("Invalid request line format");
        }

        String method = requestParts[0];
        String path = requestParts[1];
        String version = requestParts[2];

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim()
            .isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String headerName = headerLine.substring(0, colonIndex)
                    .trim();
                String headerValue = headerLine.substring(colonIndex + 1)
                    .trim();
                headers.put(headerName, headerValue);
            }
        }

        StringBuilder bodyBuilder = new StringBuilder();
        String contentLengthStr = headers.get("Content-Length");

        if (contentLengthStr != null) {
            try {
                int contentLength = Integer.parseInt(contentLengthStr);
                char[] buffer = new char[contentLength];
                int bytesRead = in.read(buffer, 0, contentLength);
                if (bytesRead > 0) {
                    bodyBuilder.append(buffer, 0, bytesRead);
                }
            } catch (NumberFormatException e) {
            }
        }

        return new HttpRequest(method, path, version, headers, bodyBuilder.toString());
    }

    @Override
    public String toString() {
        return String.format("HttpRequest{method='%s', path='%s', version='%s', headers=%s, body='%s'}", method, path, version, headers, body);
    }
}
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final StatusCode statusCode;
    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(StatusCode statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", "text/plain");
        this.headers.put("Content-Length", String.valueOf(this.body.length()));
    }

    public HttpResponse(StatusCode statusCode, String body, String contentType) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(this.body.length()));
    }

    public HttpResponse addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public void send(BufferedWriter out) throws IOException {
        out.write(String.format("HTTP/1.1 %d %s\r\n", statusCode.getCode(), statusCode.getMessage()));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            out.write(String.format("%s: %s\r\n", header.getKey(), header.getValue()));
        }

        out.write("\r\n");

        if (body != null && !body.isEmpty()) {
            out.write(body);
        }

        out.flush();
    }

    @Override
    public String toString() {
        return String.format("HttpResponse{statusCode=%s, body='%s', headers=%s}", statusCode, body, headers);
    }
}
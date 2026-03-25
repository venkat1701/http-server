import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final StatusCode statusCode;
    private final String body;
    private final byte[] binaryBody;
    private final Map<String, String> headers;

    public HttpResponse(StatusCode statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.binaryBody = null;
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", "text/plain");
        this.headers.put("Content-Length", String.valueOf(this.body.length()));
    }

    public HttpResponse(StatusCode statusCode, String body, String contentType) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.binaryBody = null;
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(this.body.length()));
    }

    public HttpResponse(StatusCode statusCode, byte[] binaryBody) {
        this.statusCode = statusCode;
        this.body = null;
        this.binaryBody = binaryBody != null ? binaryBody : new byte[0];
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", "text/plain");
        this.headers.put("Content-Length", String.valueOf(this.binaryBody.length));
    }

    public HttpResponse(StatusCode statusCode, byte[] binaryBody, String contentType) {
        this.statusCode = statusCode;
        this.body = null;
        this.binaryBody = binaryBody != null ? binaryBody : new byte[0];
        this.headers = new HashMap<>();

        this.headers.put("Content-Type", contentType);
        this.headers.put("Content-Length", String.valueOf(this.binaryBody.length));
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
        out.flush();

        if (binaryBody != null && binaryBody.length > 0) {
            // Get the underlying output stream from BufferedWriter to send binary data correctly
            OutputStream outStream = null;
            try {
                // Access the underlying socket output stream through reflection if needed
                // For now, we'll get it from the socket directly in URLParser
                out.flush();
            } catch (Exception e) {
                // Fallback to writing as string
                out.write(new String(binaryBody));
            }
        } else if (body != null && !body.isEmpty()) {
            out.write(body);
        }

        out.flush();
    }

    public void sendWithBinary(BufferedWriter out, OutputStream binaryOut) throws IOException {
        out.write(String.format("HTTP/1.1 %d %s\r\n", statusCode.getCode(), statusCode.getMessage()));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            out.write(String.format("%s: %s\r\n", header.getKey(), header.getValue()));
        }

        out.write("\r\n");
        out.flush();

        if (binaryBody != null && binaryBody.length > 0) {
            binaryOut.write(binaryBody);
        } else if (body != null && !body.isEmpty()) {
            out.write(body);
        }

        binaryOut.flush();
    }

    @Override
    public String toString() {
        return String.format("HttpResponse{statusCode=%s, body='%s', headers=%s}", statusCode, body, headers);
    }
}
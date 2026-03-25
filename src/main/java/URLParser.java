import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;

public class URLParser {

    public void handleRequest(Socket socket, String[] args) throws IOException {
        try {
            System.out.println("accepted new connection");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            OutputStream binaryOut = socket.getOutputStream();

            // Handle persistent connections - keep processing requests until Connection: close is received
            boolean keepAlive = true;
            while (keepAlive) {
                HttpRequest request = HttpRequest.parseFromSocket(in);

                String connectionHeader = request.headers.getOrDefault("Connection", "");
                boolean shouldClose = "close".equalsIgnoreCase(connectionHeader);

                HttpResponse response = processRequest(request, args, binaryOut, out);

                if (shouldClose) {
                    response.addHeader("Connection", "close");
                    keepAlive = false;
                }
                sendResponse(response, out, binaryOut);
                if (!keepAlive) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private HttpResponse processRequest(HttpRequest request, String[] args, OutputStream binaryOut, BufferedWriter out) {
        return switch (request.path) {
            case "/" -> new HttpResponse(StatusCode.OK, "");
            case "/index.html" -> new HttpResponse(StatusCode.OK, "<html><body>Hello, world!</body></html>");
            case String s when s.startsWith("/echo/") -> processEchoRequest(s, request);
            case String s when s.startsWith("/files/") -> processFilesRequest(s, request, args);
            case "/echo" -> new HttpResponse(StatusCode.OK, request.body);
            case "/user-agent" -> new HttpResponse(StatusCode.OK, request.headers.getOrDefault("User-Agent", ""));
            case "/bobo/bob.txt" -> new HttpResponse(StatusCode.OK, "Bob");
            default -> new HttpResponse(StatusCode.NOT_FOUND, "");
        };
    }

    private HttpResponse processEchoRequest(String path, HttpRequest request) {
        String echoString = path.substring("/echo/".length());
        String acceptEncoding = request.headers.getOrDefault("Accept-Encoding", "");

        if (acceptEncoding.contains("gzip")) {
            try {
                byte[] compressedBody = CompressionUtil.compressGzip(echoString);
                HttpResponse response = new HttpResponse(StatusCode.OK, compressedBody, "text/plain");
                response.addHeader("Content-Encoding", "gzip");
                return response;
            } catch (IOException e) {
                return new HttpResponse(StatusCode.OK, echoString);
            }
        } else {
            return new HttpResponse(StatusCode.OK, echoString);
        }
    }

    private HttpResponse processFilesRequest(String path, HttpRequest request, String[] args) {
        String filename = path.substring("/files/".length());
        File f = new File(args[1], filename);

        if ("POST".equals(request.method)) {
            try {
                Files.write(f.toPath(), request.body.getBytes());
                return new HttpResponse(StatusCode.CREATED, "");
            } catch (IOException e) {
                return new HttpResponse(StatusCode.NOT_FOUND, "");
            }
        } else if ("GET".equals(request.method)) {
            if (f.exists()) {
                try {
                    byte[] content = Files.readAllBytes(f.toPath());
                    return new HttpResponse(StatusCode.OK, new String(content), "application/octet-stream");
                } catch (IOException e) {
                    return new HttpResponse(StatusCode.NOT_FOUND, "");
                }
            } else {
                return new HttpResponse(StatusCode.NOT_FOUND, "");
            }
        }
        return new HttpResponse(StatusCode.NOT_FOUND, "");
    }

    private void sendResponse(HttpResponse response, BufferedWriter out, OutputStream binaryOut) throws IOException {
        if (response.hasBinaryBody()) {
            response.sendWithBinary(out, binaryOut);
        } else {
            response.send(out);
        }
    }
}
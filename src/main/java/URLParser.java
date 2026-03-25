import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;

public class URLParser {

    public void handleRequest(Socket socket, String[] args) throws IOException {
        try {
            System.out.println("accepted new connection");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            HttpRequest request = HttpRequest.parseFromSocket(in);

            switch (request.path) {
                case "/" -> new HttpResponse(StatusCode.OK, "").send(out);
                case "/index.html" -> new HttpResponse(StatusCode.OK, "<html><body>Hello, world!</body></html>").send(out);
                case String s when s.startsWith("/echo/") -> {
                    String echoString = s.substring("/echo/".length());
                    HttpResponse response = new HttpResponse(StatusCode.OK, echoString);
                    String acceptEncoding = request.headers.getOrDefault("Accept-Encoding", "");
                    if (acceptEncoding.contains("gzip")) {
                        response.addHeader("Content-Encoding", "gzip");
                    }

                    response.send(out);
                }
                case String s when s.startsWith("/files/") -> {
                    String filename = s.substring("/files/".length());
                    File f = new File(args[1], filename);
                    if ("POST".equals(request.method)) {
                        try {
                            Files.write(f.toPath(), request.body.getBytes());
                            new HttpResponse(StatusCode.CREATED, "").send(out);
                        } catch (IOException e) {
                            new HttpResponse(StatusCode.NOT_FOUND, "").send(out);
                        }
                    } else if ("GET".equals(request.method)) {
                        if (f.exists()) {
                            byte[] content = Files.readAllBytes(f.toPath());
                            new HttpResponse(StatusCode.OK, new String(content), "application/octet-stream").send(out);
                        } else {
                            new HttpResponse(StatusCode.NOT_FOUND, "").send(out);
                        }
                    }
                }
                case "/echo" -> new HttpResponse(StatusCode.OK, request.body).send(out);
                case "/user-agent" -> new HttpResponse(StatusCode.OK, request.headers.getOrDefault("User-Agent", "")).send(out);
                case "/bobo/bob.txt" -> new HttpResponse(StatusCode.OK, "Bob").send(out);

                default -> new HttpResponse(StatusCode.NOT_FOUND, "").send(out);
            }

            out.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw e;
        }
    }
}
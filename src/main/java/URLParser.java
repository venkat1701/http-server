import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class URLParser {

    public void handleRequest(Socket socket) throws IOException {
        while (!socket.isClosed()) {
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
                        new HttpResponse(StatusCode.OK, echoString).send(out);
                    }
                    case "/echo" -> new HttpResponse(StatusCode.OK, request.body).send(out);
                    case "/user-agent" -> new HttpResponse(StatusCode.OK, request.headers.getOrDefault("User-Agent", "")).send(out);
                    case "/bobo/bob.txt" -> new HttpResponse(StatusCode.OK, "Bob").send(out);
                    default -> new HttpResponse(StatusCode.NOT_FOUND, "").send(out);
                }

                out.flush();
                in.close();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
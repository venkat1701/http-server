import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class URLParser {
    private final InputStream inputStream;
    private final Map<String, String> resources;

    public URLParser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.resources = new HashMap<String, String>();
        this.resources.put("/", "");
    }

    private boolean checkResourceExistsInServer() {
        if(this.inputStream == null) {
            return false;
        }

        try(var reader = new BufferedReader(new InputStreamReader(this.inputStream))) {
            String line = reader.readLine();
            String[] parts = line.split(" ");
            if(parts.length != 2) {
                return false;
            } else {
                return this.resources.containsKey(parts[1]);
            }
        } catch(IOException e) {
            return false;
        }
    }

    public String respondToClient() {
        if(this.checkResourceExistsInServer()) {
            return ResponseStatus.ACCEPTED.getResponse();
        } else return ResponseStatus.NOT_FOUND.getResponse();
    }

}

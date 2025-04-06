public enum ResponseStatus {

    ACCEPTED("HTTP/1.1 200 OK\r\n"),
    NOT_FOUND("HTTP/1.1 404 Not Found\r\n\r\n");

    private final String response;
    ResponseStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }
}

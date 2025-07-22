public enum StatusCode {
    OK(200, "OK"),
    NO_CONTENT(204, "No Content"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%d %s", code, message);
    }
}
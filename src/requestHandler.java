
// This is an interface in whcih an abstract function is there we use handle func to generate responses.
public interface requestHandler {
    public HttpResponse handle(HttpReq request);
}

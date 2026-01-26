
// This is an interface in whcih an abstract function is there we use handle func to generate responses.

import java.net.Socket;

public interface requestHandler {
    public HttpResponse handle(Socket connection,HttpReq request);
}

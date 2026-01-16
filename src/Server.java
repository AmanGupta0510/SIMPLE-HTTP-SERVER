

import java.util.concurrent.Executors;

public class Server {
    
    public static void main(String[] args) throws Exception{

        var server = new serverHandler(8080,Executors.newFixedThreadPool(10),10000); 
        requestHandler reqH = null;
        server.start(reqH);
    } 
}

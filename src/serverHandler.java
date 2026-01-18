

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
// import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
// import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.concurrent.*;
public class serverHandler implements HttpServerApp {
    private final int port;
    private final Executor requestExecutor;
    private final int connectionTimeOut; 
    private ServerSocket serverSocket;

    public serverHandler(int port,Executor executor,int connectionTimeOut){
        this.port = port;
        this.requestExecutor = executor;
        this.connectionTimeOut  = connectionTimeOut; 
    }
    //  This start() func is an abstract method of HttpServerApp interface,
    //  and here in this func we handle the parsed HTTP requests as well as server Connection. 
    @Override
    public void start(requestHandler reqH) {
        if(serverSocket == null ){ // check if server already running.
            try{
                serverSocket = new ServerSocket(port); // initialize the serverSocket on configured port.
            }catch(Exception e){
                throw new RuntimeException("fail to start the server!!!");
            }
            try{
                Thread t1 = new Thread(new serverListener(reqH));// Launch serverlistener thread in seperate background thread and pass reqHandler to severlistener
                t1.start();
            }catch(Exception e){
                e.printStackTrace();
                throw e;
                
            }
        }
        else{
            throw new RuntimeException("Server is Currently Running on Port %d".formatted(port));
        }
    }
    // This serverListener class implements Runnable for the concurrency
    // and since it implements Runnable Interface we need to override run() method
    // and in the run method we accepting the user request and create a seperate thread for each request and handling each request seperately in a concurrent manner.   
    private class serverListener implements Runnable{
        requestHandler reqH ;
        public serverListener(requestHandler reqH){
           this.reqH = reqH;
        }
        @Override
        public void run() {

           try{
                while(true){
                    var connection = serverSocket.accept();//Accepts connections in infinite loop, delegates to thread POOL via requestExecutor
                    connection.setSoTimeout(connectionTimeOut);
                    requestExecutor.execute(()->{
                            try{
                               
                                handleRequest(connection,reqH);// here, we handle the request by uses  Executor for efficient connection pooling.(no new thread() is creaing for each request , after completing the task thread return to the pool and get ready for other connection )
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        });
                }
            }catch(SocketException e){System.out.println("Closing Server!!!");} 
            catch(Exception e){
                stop();
                throw new RuntimeException("fail to accept next Connection!",e);
            }

        }
        // This handleRequest() method parse the raw HTTP Request 
        // print it and send the response of corresponding request to the client.
        private void handleRequest(Socket connection,requestHandler reqH) throws Exception{
            
            try{
                var parsedReq = RequestParser.parseReq(connection); // parse the raw HTTP request
                if(parsedReq.isEmpty()){
                    closeConnection(connection);
                    return;
                }
                ResponseHandler.printHeader(parsedReq); // print the header Line and request in the terminal.
                ResponseHandler.respondToreq(connection, parsedReq.get(),reqH); // send the response of corresponding request to the clinet with porper and correct headers + body. 

                if(shouldReUseConnection(parsedReq.get().header())){ // here , just reusing the established connection.  
                    System.out.println("Connection Reusing !!!");
                    handleRequest(connection,reqH); 
                }
                else{
                    closeConnection(connection);
                }
            }
        catch(SocketTimeoutException se ){
            System.out.println("Socket TimeOut");
           closeConnection(connection);

        }
        catch(Exception e){
            System.out.println("Problem While handling the request");
            e.printStackTrace();
            closeConnection(connection);
        }
            
        }
        // This function shouldReUseConnection() finds out whether we can reuse established  TCP connection or not.
        // Connection: keep-alive means the TCP connection stays open after the HTTP response, 
        // allowing multiple requests/responses over the same socket instead of closing/reopening for each one.
        private boolean shouldReUseConnection(Map<String,List<String>> header) {
           
            if(header.containsKey("connection")){
                return header.get("connection").get(0).equals("keep-alive");
            }
            return false;
        }
       
    }

    @Override
    public void stop() {
        if(serverSocket!=null){
            try{
                serverSocket.close();
            }
            catch(Exception e){
                throw new RuntimeException("Fail to close the server", e );
            }
            finally{
                serverSocket = null;
            }
        }
    }
    private void closeConnection(Socket connection){
        try{
            connection.close();
        }
        catch(Exception e){}
    }
    
   
    
}



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

    @Override
    public void start(requestHandler reqH) {
        if(serverSocket == null ){
            try{
                serverSocket = new ServerSocket(port);
            }catch(Exception e){
                throw new RuntimeException("fail to start the server!!!");
            }
            try{
                Thread t1 = new Thread(new serverListener(reqH));
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

    private class serverListener implements Runnable{
        requestHandler reqH ;
        public serverListener(requestHandler reqH){
           this.reqH = reqH;
        }
        @Override
        public void run() {

           try{
                while(true){
                    var connection = serverSocket.accept();
                    // connection.setSoTimeout(connectionTimeOut);
                    requestExecutor.execute(()->{
                            try{
                               
                                handleRequest(connection,reqH);
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

        private void handleRequest(Socket connection,requestHandler reqH) throws Exception{
            
            try{
                var parsedReq = RequestParser.parseReq(connection); 
                if(parsedReq.isEmpty()){
                    connection.close();
                    return;
                }
                ResponseHandler.printHeader(parsedReq);
                ResponseHandler.respondToreq(connection, parsedReq.get(),reqH);

                if(shouldReUseConnection(parsedReq.get().header())){
                    System.out.println("Connection Reusing !!!");
                    handleRequest(connection,reqH);
                }
            }
        catch(SocketTimeoutException se ){
            System.out.println("Socket TimeOut");
            connection.close();

        }
        catch(Exception e){
            System.out.println("Problem While handling the request");
            e.printStackTrace();
            connection.close();
        }
            
        }
        private boolean shouldReUseConnection(Map<String,List<String>> header) {
           
            if(header.containsKey("connection")){
                return header.get("connection").get(0).equals("keep-alive");
            }
            return false;
        }
       
    }

    @Override
    public void stop() {
        
    }

    
   
    
}

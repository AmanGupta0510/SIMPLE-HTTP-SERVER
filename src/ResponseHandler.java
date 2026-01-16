


import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResponseHandler implements requestHandler {
    
    // This printHeader Func is printing the Appropriate header and Body in the terminal.  
    public static void printHeader(Optional<HttpReq> request){
        if(request.isPresent()){
            printHeaderLine(request);
            if(request.get().body().length > 0){
                System.out.println(new String(request.get().body(),StandardCharsets.UTF_8));
            }
            else System.out.println("Empty Body!!!");
        }
        else{
            System.out.println("request is Null");
        }
        
        
    }
    private static void printHeaderLine(Optional<HttpReq> request){

        System.out.println("Method: "+request.get().method());
        System.out.println("Url/Path: "+request.get().url());
        System.out.println("Version: "+request.get().version());

        System.out.println("Headers:");
        var header = request.get().header();
        for(Map.Entry<String,List<String>> m:header.entrySet()){
            var key = m.getKey();
            var value = m.getValue(); 
            System.out.println("%s - %s".formatted(key,value));
        }


    }

    /* 
     Actual Response:-

        HTTP/1.1 200 OK\r\n
        Content-Type: text/html; charset=UTF-8\r\n
        Content-Length: 245\r\n
        Connection: close\r\n
        \r\n
        Body...
    */
    // This respondToreq Func basically try to create above response with the correct status code And body   

    public static void respondToreq(Socket connection, HttpReq req, requestHandler reqHandler) throws IOException  {
            if (reqHandler == null) {
               System.err.println("reqHandler is null! Creating default...");
               reqHandler = new ResponseHandler();  // Default handler
            }
            final String HTTP_NEW_LINE_SEPERATOR = "\r\n";
            final String HTTP_HEAD_BODY_SEPERATOR = HTTP_NEW_LINE_SEPERATOR+HTTP_NEW_LINE_SEPERATOR;
            var res = reqHandler.handle(req);
            var os = connection.getOutputStream(); // Get the stream to send data to the client.
            var responseHead = new StringBuilder("HTTP/1.1 %d".formatted(res.statusCode()));

            res.header().forEach((k,vs)->{
               vs.forEach(v->{
                 responseHead.append(HTTP_NEW_LINE_SEPERATOR)
                        .append(k)
                        .append(": ")
                        .append(v);
               });
            });
            
            responseHead.append(HTTP_HEAD_BODY_SEPERATOR);
            os.write(responseHead.toString().getBytes(StandardCharsets.US_ASCII)); // write the response in the byte[] to the client.  
            if(res.body().length >0){
                os.write(res.body());
            }

			
			
            os.flush(); //  flush() forces all buffered data to be sent immediately as only write() doesn't send the data it remains in the buffer(temporary memory stack where data stored before sending/receiving).
          

        }
        public HttpResponse handle(HttpReq request) {
				var body ="""
                {
                  "id":1,
                  "url":"%s",
                  "bodyLength":%d
                }
                """.formatted(request.url(),request.body().length).getBytes(StandardCharsets.UTF_8);

        var responseHeader =  Map.of("Content-Type", List.of(" application/json" ),
                                "Content-Length", List.of(String.valueOf(body.length))); 
        return new HttpResponse(200,responseHeader,body);   
			} 



   

}

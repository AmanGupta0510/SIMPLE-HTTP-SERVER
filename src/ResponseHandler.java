


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
            var res = reqHandler.handle(req);// handle the request of the client , provide response  with correct header and correct body.res contains the response of client request.
            var os = connection.getOutputStream(); // Get the stream to send data to the client.
            var responseHead = new StringBuilder("HTTP/1.1 %d".formatted(res.statusCode()));

            res.header().forEach((k,vs)->{ // just traversing through the response header and added it in the string builder.
               vs.forEach(v->{
                 responseHead.append(HTTP_NEW_LINE_SEPERATOR)
                        .append(k)
                        .append(": ")
                        .append(v);
               });
            });
            
            responseHead.append(HTTP_HEAD_BODY_SEPERATOR);
            try{
                    os.write(responseHead.toString().getBytes(StandardCharsets.US_ASCII)); // write the response in the byte[] to the client.  
                    if(res.body().length >0){
                        os.write(res.body()); // if there is a body for the request then also write it to send the data to client.
                    }
                    os.flush(); //  flush() forces all buffered data to be sent immediately as only write() doesn't send the data it remains in the buffer(temporary memory stack where data stored before sending/receiving).
            }
            catch (SocketException e) {
                System.out.println("Client disconnected normally: " + e.getMessage());
                // DON'T rethrow - expected!
            } catch (Exception e) {
                System.err.println("Write error: " + e);  // Log others
            }
            // finally{
            //     os.close();
            // }




           

			
			
           
          

        }
        public HttpResponse handle(HttpReq request) {
		
            var url  = request.url();
            var statusCode = 200;
            if(url.equals("/"))url+="index.html";
           
           
            var fileName = url.split("\\.",2); // why \\. bcz "." is considered as a special Character so that's why we use escaped key  Outer \ escapes for Java string literal → single \ in string Inner \ escapes . for regex → literal dot matcher.
            // ex:- fileName -> ["/index","html"] or ["/"]
           
            FileServer fileManipulation = new FileServer();

            var filePath = fileManipulation.findPath(fileName[0]);
            File file = new File(filePath);
            if(filePath.equals("public/error.html") || !file.exists()){
                statusCode = 404;
            }
            if(request.method().equalsIgnoreCase("HEAD")){
                var responseHeader = Map.of("Content-Type",List.of(fileManipulation.getMIME(filePath)),
                                      "Content-Length",List.of(String.valueOf(file.length())),"cache-Control",List.of("public, max-age=3600"));
                return new HttpResponse(statusCode,responseHeader,new byte[0]);
            }
            var body = fileManipulation.readFileContext(filePath,url,request.body(),request.method());
           
            // var responseBody = body.getBytes(StandardCharsets.UTF_8);

            

            
            var responseHeader =  Map.of("Content-Type", List.of(fileManipulation.getMIME(filePath)),
                                     "Content-Length", List.of(String.valueOf(body.length)) ); 
            return new HttpResponse(statusCode,responseHeader,body); 








        } 

            // 		var body ="""
        //         {
        //           "id":1,
        //           "url":"%s",
        //           "bodyLength":%d
        //         }
        //         """.formatted(request.url(),request.body().length).getBytes(StandardCharsets.UTF_8);

        // var responseHeader =  Map.of("Content-Type", List.of(" application/json" ),
        //                         "Content-Length", List.of(String.valueOf(body.length))); 
        // return new HttpResponse(200,responseHeader,body);   



   

}

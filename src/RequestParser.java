

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;





public class RequestParser {
    
    private static final int DEFAULT_PACKET_SIZE = 10000;

    // This method/function parse the raw HTTP request into proper HTTP req with Header,Body,Method,URL And ProtocolVersion.

    public static Optional<HttpReq> parseReq(Socket connection) throws Exception{
       String url = "/";
       String protocolVersion = "0.0.0.0";
       String method = "GET";
       var stream = connection.getInputStream();// take the raw HTTP request from the client.
       var rawReqHead = readRawRequestHead(stream); // convert the raw HTTP request into byte[] , which basically convert the actual req into byte[] array.ex:- GET HTTP/1.1 ... -> [73,43,12,32,...].
       if(rawReqHead.length<=0)return Optional.empty(); // inCase there is no request.

       var reqHead = new String(rawReqHead,StandardCharsets.US_ASCII); // here,decode the byte[HTTP request] into human readable format (String) using ASCII.
       var headerLine = reqHead.split("\r\n"); // since HTTP req contains delimeter as a seperator between contents , So here just split the requestHead into the String[] using "\r\n".ex:- "GET / HTTP/1.1 \r\n Host: locolHost:8080 ..." -> ["GET / HTTP/1.1","Host: locolhost:8080",...]
       
       
       if(headerLine.length>1){
        var line = headerLine[0].split(" ");// again split the headerLine into String[] ex: ["GET / HTTP/1.1"] -> ["GET","/","HTTP/1.1"].
         method = line[0];// extract the 0th element as a method ex:-GET,POST,etc. 
         url = line[1]; // extract the 1st element as an url ex:- /,/index.html,etc.
         protocolVersion = line[2]; // extract the protocolversion.

       }
        
        var header = readHeader(headerLine); // this readHeader function convert the headers into the Key:Value pair.
        var bodyLength = getExpectedBodylength(header);

        byte[] body;
        if(bodyLength>0){
            var bodyStartingIndex = reqHead.indexOf("\r\n\r\n");
            if(bodyStartingIndex>0){
                var readBody = Arrays.copyOfRange(rawReqHead,bodyStartingIndex+"\r\n\r\n".getBytes(StandardCharsets.US_ASCII).length,rawReqHead.length);
                body = readRawBody(stream,readBody,bodyLength); 
            } 
            else body = new byte[0];
        }
        else body = new byte[0];

        return Optional.of(new HttpReq(method,url,protocolVersion,header,body));
    
    }

    // This function basically convert the actual raw HTTP Request into the Byte Array.

    private static byte[] readRawRequestHead(InputStream stream) throws Exception {
       var toRead = stream.available();// this tell how many bytes of request are availiable currently(give what is ready now doesn't wait for full request).

        if(toRead == 0)toRead = DEFAULT_PACKET_SIZE;
       
        var buffer = new byte[toRead];// a byte array is created of size toRead.

        var read = stream.read(buffer); // read number of bytes from the stream and stores them into buffer array.

        if(read<=0)return new byte[0];
        
        // if(read == toRead)return buffer; // if the number of bytes that is actually readable is equal to the number of bytes availiable in the stream.

        // else{
        //     var newBuffer = new byte[read];
        //     for(int i = 0;i<read;i++){
        //         newBuffer[i] = buffer[i];
        //     }
        //     return newBuffer;
        //     /*
        //     here,else condition arises bcz it may be possible
        //     that in inputStream only less bytes are readable
        //     
        // }  

       
        return read == toRead?buffer:Arrays.copyOf(buffer,read);
        // ex:
        //     buffer[100]->created
        //     read() = 75 (may be only 75 bytes are actually readable)
        //     buffer[0...74] contains bytes/data.
        //     buffer[75...99] -> garbage , so this part is of no need.
        //     newBuffer[75]-> created,so that only readable bytes get stored.that's why we copy the bytes of buffer into newBuffer bcz newBuffer is of accurate size.  
        //     */ 
    }
    
    // This readRawBody Func reads actual body of the HTTP Req and store it in a byte[] .
    private static byte[] readRawBody(InputStream streamReq, byte[] readBody, int bodyLength) throws Exception{
       
        if(readBody.length == bodyLength)return readBody;

        var result = new ByteArrayOutputStream(bodyLength);//created a outputstream array of size expectedBody Length.
        result.write(readBody);// write the current body into result.
        var readBytes = readBody.length;
        var buffer = new byte[10000];
        while(readBytes<bodyLength){ // if inCase the current body size is less than the expectedBody Length,then it may be possible some body is still there to read,Bcz in a TCP connection request comes in a packet. 
            var read  = streamReq.read(buffer);//each iteration gets fresh data from stream.read(buffer) 
            if(read>0){
                result.write(buffer,0,read);// writes availiable bytes from the buffer into result byteArray
                readBytes+=read;
            } 
            else break;
            /*
            Loop 1: stream.read(buffer) → buffer = [A,B,C,D,E...]  (new TCP packet)
            result.write(buffer,0,5)    → result = [A,B,C,D,E]

            Loop 2: stream.read(buffer) → buffer = [F,G,H,I,J...]  (next TCP packet) 
            result.write(buffer,0,5)    → result = [A,B,C,D,E,F,G,H,I,J]

            Loop 3: stream.read(buffer) → buffer = [K,L,M,N,O...]  (more data)
            ...
            */
        }
        return result.toByteArray();

    }

    // This readHeader Func convert header into the Key:Value Pair by iterating through the request headers.
    
    private static HashMap<String,List<String>> readHeader(String[] headerLine ){
        HashMap<String,List<String>> header = new HashMap<>();
        for(int i = 1;i<headerLine.length;i++){
            if(headerLine[i].isEmpty())break;
            var keyValue = headerLine[i].split(":",2);
            var key = keyValue[0].strip().toLowerCase();
            var value = keyValue[1].strip();
            if(header.containsKey(key))header.get(key).add(value);
            else{
                List<String> str = new ArrayList<>();
                str.add(value);
                header.put(key,str);

            } 
        }
        return header;

    }
    // This get getExpectedBodylength Func used to give just the exact length of availiable body from HTTP request header.
    private static int getExpectedBodylength(HashMap<String,List<String>> header){
        try{

            if(header.containsKey("content-length")){
                return Integer.parseInt(header.get("content-length").get(0));
            }
            return 0;// if content-length is not present in the header.
        }
        catch(NumberFormatException ne){
            return 0;
        }
        catch(Exception e){
            return 0;
        }
    }
}

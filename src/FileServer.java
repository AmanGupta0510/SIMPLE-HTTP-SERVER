import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class FileServer {
    private final static  HashMap<String,String> urlPathMapping = new HashMap<>();
    private final static HashMap<String,String> MIMEmapping = new HashMap<>();
    public FileServer(){

        urlPathMapping.put("/index","public/index.html");
        urlPathMapping.put("/login","public/login.html");
        urlPathMapping.put("/logout","public/logout.html");
        urlPathMapping.put("/about","public/about.html");
        urlPathMapping.put("/style-index","public/style-index.css");
        urlPathMapping.put("/style-login","public/style-login.css");
        urlPathMapping.put("/bg","public/bg.jpg");

        
        
        MIMEmapping.put("html","text/html; charSet=utf-8");
        MIMEmapping.put("json","application/json");
        MIMEmapping.put("txt","text/plain");
        MIMEmapping.put("png","image/png");
        MIMEmapping.put("jpeg","image/jpeg");
        MIMEmapping.put("css","text/css; charSet=utf-8");
        MIMEmapping.put("jpg","image/jpeg");

        
        

    }


    public  String findPath(String url){
        if(url.startsWith("/public/")){
            url = url.substring(7);
        }
        // System.out.println(url+" "+url.length());
        if(urlPathMapping.containsKey(url)){
            return urlPathMapping.get(url);
        }
        else{
            return "public/error.html"; 
        }
       
    }

   
    public  byte[] readFileContext(String filePath,String url,byte[] body,String method){
       
        byte[] readFile = readFileHelper(filePath);

        if(method.equalsIgnoreCase("GET")){
            return readFile;
        }

        else if(method.equalsIgnoreCase("POST")){
           String postFile = new String(readFile,StandardCharsets.UTF_8);
           var data = formToData(body); 
        //    System.out.println(postFile);
          
           postFile = postFile.replace("{{username}}",data.get("username"));
           postFile = postFile.replace("{{password}}",data.get("password"));

           return postFile.getBytes(StandardCharsets.UTF_8); 

        } 
        else return new byte[0];
    }

    public  String getMIME(String path){
       var extract = path.split("\\.",2);
       return MIMEmapping.getOrDefault(extract[extract.length-1], "application/octet-stream");  
 
    }

    private byte[] readFileHelper(String filePath){

        Path path = Path.of(filePath).toAbsolutePath();// 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var buffer = new byte[8192];
        if(Files.exists(path)){
                
            try(FileInputStream fis = new FileInputStream(path.toFile())){
                int bytesRead;
                    
                while((bytesRead=fis.read(buffer))!=-1){
                    baos.write(buffer,0,bytesRead);
                }
            }
            
            catch(Exception e){
                e.printStackTrace();
                return "Error 404".getBytes(StandardCharsets.UTF_8);
            }
            return baos.toByteArray();
        }
        return "Error 404".getBytes(StandardCharsets.UTF_8);
    }

    private Map<String,String> formToData(byte[] body){

        var data = new HashMap<String,String>();
        var str = new String(body,StandardCharsets.UTF_8);
        var dataList = str.split("&");

        for(String s:dataList){
           var keyValue = s.split("=");
           if(keyValue.length == 2){
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            data.put(key, value);
           }
        }
        return data;
    }
       
}



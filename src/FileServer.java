import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;


public class FileServer {
    private final static  HashMap<String,String> urlPathMapping = new HashMap<>();
    private final static HashMap<String,String> MIMEmapping = new HashMap<>();
    public FileServer(){

        urlPathMapping.put("/index","public/index.html");
        urlPathMapping.put("/login","public/login.html");
        urlPathMapping.put("/logout","public/logout.html");
        urlPathMapping.put("/about","public/about.html");
        urlPathMapping.put("/style-index","public/style-index.css");
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
        System.out.println(url+" "+url.length());
        if(urlPathMapping.containsKey(url)){
            return urlPathMapping.get(url);
        }
        else{
            return "public/error.html"; 
        }
       
    }

   
    public  byte[] readFileContext(String filePath,String url){
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        var buffer = new byte[8192];

        Path path = Path.of(filePath).toAbsolutePath();// 
       
        if(Files.exists(path)){
            // System.out.println("img file exist");
            // StringBuilder html = new StringBuilder();

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
            // System.out.println(html);
            return baos.toByteArray();
        }
        return "Error 404".getBytes(StandardCharsets.UTF_8);
    }

    public  String getMIME(String path){
       var extract = path.split("\\.",2);
       return MIMEmapping.getOrDefault(extract[extract.length-1], "application/octet-stream");  
 
    }

}

import java.io.BufferedReader;
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
        
        
        MIMEmapping.put("html","text/html");
        MIMEmapping.put("json","application/json");
        MIMEmapping.put("txt","text/plain");
        MIMEmapping.put("png","image/png");
        MIMEmapping.put("jpeg","image/jpeg");
        
        

    }


    public  String findPath(String url){
        
        if(urlPathMapping.containsKey(url)){
            return urlPathMapping.get(url);
        }
        else{
            return "public/error.html"; 
        }
       
    }

   
    public  byte[] readFileContext(String filePath,String url){
        
      
        Path path = Path.of(filePath);// 
        System.out.println(path);
        if(Files.exists(path)){
          
            StringBuilder html = new StringBuilder();

            try(BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))){
                String read;
                while((read=reader.readLine())!=null){
                    read = read.replace("{{url}}", url);
                    html.append(read).append("\n");
                }
            }
            
            catch(Exception e){
                e.printStackTrace();
                return "Error 404".getBytes(StandardCharsets.UTF_8);
            }
            return html.toString().getBytes(StandardCharsets.UTF_8);
        }
        return "Error 404".getBytes(StandardCharsets.UTF_8);
    }

    public  String getMIME(String path){
       var extract = path.split("\\.",2);
       return MIMEmapping.getOrDefault(extract[extract.length-1], "application/octet-stream");  
 
    }

}

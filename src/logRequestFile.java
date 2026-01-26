import java.util.Date;
import java.text.SimpleDateFormat;

public  class logRequestFile {
    
    private static final SimpleDateFormat Date_Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void logRequest(String method,String path,int statusCode,String clientIP){
        String timestamp = Date_Format.format(new Date());
        System.out.print("Logs:- ");
        System.out.printf("%s %s %s %d %s%n", 
        timestamp, method, path, statusCode, clientIP);
    }
}

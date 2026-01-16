



import java.util.List;
import java.util.Map;

 record HttpReq(String method,String url,String version,Map<String,List<String>>header,byte[] body){

}





import java.util.List;
import java.util.Map;
// just for the getter and setter of HttpReq class..
 record HttpReq(String method,String url,String version,Map<String,List<String>>header,byte[] body){

}



import java.util.List;
import java.util.Map;
// Just for the getter and setter of HttpResponse class.
 record HttpResponse(int statusCode,Map<String,List<String>> header,byte[] body){
    
}



import java.util.List;
import java.util.Map;

 record HttpResponse(int statusCode,Map<String,List<String>> header,byte[] body){
    
}

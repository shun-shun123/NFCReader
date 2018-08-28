package suika.jp.nfcreader.HttpMethod.Post

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

class JsonSerializer {
    public fun serializer(obj: Any): String? {
        try{
            val mapper: ObjectMapper = ObjectMapper()
            val jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
            return jsonString
        }catch(e : JsonProcessingException){
            e.printStackTrace()
            return null
        }
    }
}
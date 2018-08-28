package suika.jp.nfcreader.HttpMethod.Post

import com.fasterxml.jackson.annotation.JsonProperty

class SuicaDataModel {
    public var results: List<PostResponse>? = null
}

//open class Result {
//    //端末：String
//    var tarminal: String? = null
//    //処理：String
//    var process: String? = null
//    //入場駅地区コード：String
//    var entranceDistrict: String? = null
//    //入場駅線区コード：String
//    var entranceRoute: String? = null
//    //入場駅駅区コード：String
//    var entranceStation: String? = null
//    //出場駅地区コード：String
//    var exitDistrict: String? = null
//    //出場駅線区コード：String
//    var exitRoute: String? = null
//    //出場駅駅区コード：String
//    var exitStation: String? = null
//    //日付：String
//    var dateProcess: String? = null
//    //残金：int
//    var balance: String? = null
//}

/**
 * POSTレスポンスのモデル
 */
data class PostResponse(
        //端末：String
        @field:JsonProperty("tarminal") var tarminal: String = "",
        //処理：String
        @field:JsonProperty("process") var process: String = "",
        //入場駅地区コード：String
        @field:JsonProperty("entranceDistrict") var entranceDistrict: String = "",
        //入場駅線区コード：String
        @field:JsonProperty("entranceRoute") var entranceRoute: String = "",
        //入場駅駅区コード：String
        @field:JsonProperty("entranceStation") var entranceStation: String = "",
        //出場駅地区コード：String
        @field:JsonProperty("exitDistrict") var exitDistrict: String = "",
        //出場駅線区コード：String
        @field:JsonProperty("exitRoute") var exitRoute: String = "",
        //出場駅駅区コード：String
        @field:JsonProperty("exitStation") var exitStation: String = "",
        //日付：String
        @field:JsonProperty("dateProcess") var dateProcess: String = "",
        //残金：int
        @field:JsonProperty("balance") var balance: Int = 0
){
    override fun toString(): String =
            "PostResponse(tarminal='$tarminal' process='$process' entranceDistrict='$entranceDistrict' exitStation='$entranceRoute' " +
                    "entranceStation='$entranceStation' exitDistrict='$exitDistrict' exitRoute='$exitRoute' exitStation='$exitStation' " +
                    "dateProcess='$dateProcess' balance=$balance )"
}
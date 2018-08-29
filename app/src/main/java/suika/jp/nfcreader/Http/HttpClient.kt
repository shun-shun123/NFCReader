package suika.jp.nfcreader.Http

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

class HttpClient {
    private lateinit var url: String
    private val TAG: String = "Http"

    constructor(url: String) {
        this.url = url
        Log.d(TAG, "Constructor: " + this.url)
    }

    public fun post(data: ByteArray) {
        Fuel.post(this.url).body(data).response { request, response, result ->
            Log.d(TAG, "request: " + request.toString())
            Log.d(TAG, "resonse: " + response.toString())
            Log.d(TAG, "result: " + result.toString())
        }
    }

    public fun get() {
        this.url.httpGet().response() { request, response, result ->
            Log.d(TAG, "result: " + result.toString())
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "request: " + request.toString())
                    Log.d(TAG, "response: " + response.toString())
                }
                is Result.Failure -> {
                    Log.d(TAG, "Failed to Connect")
                }
            }
        }
    }
}
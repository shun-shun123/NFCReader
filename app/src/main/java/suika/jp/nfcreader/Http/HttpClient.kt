package suika.jp.nfcreader.Http

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.io.ByteArrayOutputStream

class HttpClient {
    private lateinit var url: String
    private val TAG: String = "Http"

    constructor(url: String) {
        this.url = url
        Log.d(TAG, "Constructor: " + this.url)
    }

    public fun post() {
        val data = ByteArrayOutputStream()
        data.write(0x01)
        data.write(0x02)
        data.write(0x03)
        val body = data.toByteArray()
        Fuel.post(this.url).body(body).response { request, response, result ->
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
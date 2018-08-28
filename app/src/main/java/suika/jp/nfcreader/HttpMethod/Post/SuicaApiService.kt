package suika.jp.nfcreader.HttpMethod.Post

import io.reactivex.Observable
import retrofit2.http.POST


interface SuicaApiService {
    @POST("macros/s/AKfycbymy6K0KVO_OqSkv6TNFxBqmon9g_jCfPPfNXRH7lwOciR4ETY/exec")
    fun post(): Observable<PostResponse>
}
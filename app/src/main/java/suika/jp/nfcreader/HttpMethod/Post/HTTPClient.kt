package suika.jp.nfcreader.HttpMethod.Post

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


class HTTPClient: SuicaApiService{

    var apiService: SuicaApiService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://try-now-kplvmhijbn.now.sh")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapper()))
                .build()
        apiService = retrofit.create(SuicaApiService::class.java)
    }

    val httpBuilder: OkHttpClient.Builder get() {
        // create http client
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original = chain.request()

                    //header
                    val request = original.newBuilder()
                            .header("Accept", "suicadata/json")
                            .method(original.method(), original.body())
                            .build()

                    return@Interceptor chain.proceed(request)
                })
                .readTimeout(30, TimeUnit.SECONDS)

        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)

        return httpClient
    }

//    // core for controller
//    val service: SuicaApiService = create(SuicaApiService::class.java)

    lateinit var retrofit: Retrofit

    fun <S> create(serviceClass: Class<S>): S {
        val gson = GsonBuilder()
                .serializeNulls()
                .create()

        // create retrofit
        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://script.google.com/") // Put your base URL
                .client(httpBuilder.build())
                .build()

        return retrofit.create(serviceClass)
    }

    override fun post(): Observable<PostResponse> = apiService.post()
}
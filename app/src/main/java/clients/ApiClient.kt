package clients

import utils.RequestLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL: String = "https://pokeapi.co/api/v2/";

    private val client: OkHttpClient
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient.Builder()
                .addInterceptor(RequestLogger())
                .addInterceptor(logging)
                .build()

        }

     val retrofit: Retrofit
         get() {
            return Retrofit.Builder()
                 .baseUrl(BASE_URL)
                 .client(client)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build()
         }
}
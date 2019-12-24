package com.anurag.mycoroutines.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by Anurag on 08,November,2019
 */
class Api {

    companion object{

        //Creating an instance of APIMethods class by lazy.
        val apiMethods: ApiMethods by lazy {

            //Creating a logging interceptor for retrofit.
            val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            //Creating an OkHttpClient for retrofit.
            val okHttpClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()

            //Initializing and setting client, interceptor for retrofit.
            val retrofit = Retrofit.Builder().baseUrl(ApiEndPoints.BASE_URL).
                addConverterFactory(MoshiConverterFactory.create()).client(okHttpClient).build()

            //Returning retrofit instance as lazy.
            return@lazy retrofit.create(ApiMethods::class.java)

        }

    }

}

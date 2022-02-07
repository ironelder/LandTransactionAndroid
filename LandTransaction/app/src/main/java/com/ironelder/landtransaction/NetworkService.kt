package com.ironelder.landtransaction

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface Api {
//q9LoIwTYvighs4U9QBprl+y6AB5mXcK2jpe5QJeuiJGXd0y01eQh+Ri20dCCH4KSUs4dW7OK+6PPEGM6+UMqaQ==
//q9LoIwTYvighs4U9QBprl%2By6AB5mXcK2jpe5QJeuiJGXd0y01eQh%2BRi20dCCH4KSUs4dW7OK%2B6PPEGM6%2BUMqaQ%3D%3D
    @GET("OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTrade")
//    @GET("assets/test.json")
    suspend fun getApartRealDealData(
        @Query("serviceKey") serviceKey: String = "q9LoIwTYvighs4U9QBprl+y6AB5mXcK2jpe5QJeuiJGXd0y01eQh+Ri20dCCH4KSUs4dW7OK+6PPEGM6+UMqaQ==",
        @Query("LAWD_CD") landCode: Int = 41173,
        @Query("DEAL_YMD") dealDate: Int = 202201
    ): String
}

class NetworkService {

    private val service:Api

    init {
        val baseUrl = "http://openapi.molit.go.kr:8081/"
        val okHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(30L, TimeUnit.SECONDS)
            readTimeout(30L, TimeUnit.SECONDS)
            writeTimeout(30L, TimeUnit.SECONDS)
        }
        val retrofit = Retrofit.Builder().apply {
            baseUrl(baseUrl)
            addConverterFactory(ScalarsConverterFactory.create())
            client(okHttpClient.build())
        }.build()
        service = retrofit.create(Api::class.java)
    }

    fun getService():Api {
        return service
    }
}
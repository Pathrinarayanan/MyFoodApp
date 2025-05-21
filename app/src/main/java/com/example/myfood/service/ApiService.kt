package com.example.myfood.service

import com.example.myfood.HomeData
import retrofit2.http.GET

interface ApiService {
    @GET("/menu")
    suspend  fun getHomeData(): HomeData
}
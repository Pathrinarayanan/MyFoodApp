package com.example.myfood

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfood.service.ApiService
import com.example.myfood.service.RetrofitHelper
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val service = RetrofitHelper.getInstance().create(ApiService::class.java)
    val data : MutableState<HomeData?>? = mutableStateOf<HomeData?>(null)
    fun fetchHomeData(){
        viewModelScope.launch {
            data?.value = service.getHomeData()
        }
    }
}
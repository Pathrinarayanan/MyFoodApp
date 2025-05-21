package com.example.myfood

sealed class FlowData {
    data class Toast(val message :String) : FlowData()
}
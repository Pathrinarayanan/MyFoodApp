package com.example.myfood

data class HomeData(
    val status :String?,
    val base_image_url :String?,
    val data : RestaurantData
)
data class RestaurantData(
    val restaurant : ResData
)

data class ResData(
    val id : Int?,
    val name : String?,
    val rating : Float?,
    val cuisine : String?,
    val image_url :String?,
    val delivery_time : String?,
    val menu : HomeMenu
)
data class HomeMenu(
    val best_sellers : List<HomeMenuItem>,
    val recommended : List<HomeMenuItem>,
    val advertise : List<AdvertiseItem>
)
data class  HomeMenuItem(
    val id : Int?,
    val name : String?,
    val price : Float?,
    val currency :String?,
    val image_url :String?,
    val delivery_time : String?,
    val description : String?,
    val is_veg : Boolean?=false
)
data class  AdvertiseItem(
    val id : Int?,
    val name : String?,
    val tagline :String?,
    val image_url :String?,
    val offer : String?,

)
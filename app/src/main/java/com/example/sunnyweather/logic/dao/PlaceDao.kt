package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    fun savePlace(place: Place) {
        sharedPreferences().edit {
            //先通过GSON将Place对象转换成一个Json字符串,然后就可以用字符串存储的方式来保存数据了
            putString("place", Gson().toJson(place))
        }

    }

    fun getSavedPlace(): Place {
        //先将json字符串取出来，然后再通过GSON将JSON字符串解析成Place对象并返回
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}
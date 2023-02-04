package com.example.sunnyweather.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Location

class WeatherViewModel : ViewModel() {

    private val locationLiveData= MutableLiveData<Location>()

    var locationLng=0.0

    var locationLat=0.0

    var placeName=""

    val weatherLiveData=Transformations.switchMap(locationLiveData){

        location->
        Log.d("WeatherViewModel", location.toString())
        Repository.refreshWeather(location.lat,location.lng)
    }
    fun refreshWeather(lng:Double,lat:Double){
        Log.d("WeatherViewModel", "lng"+lng+"lat"+lat)
        locationLiveData.value= Location(lng,lat)
    }



}
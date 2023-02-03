package com.example.sunnyweather.ui.place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place

//ViewModel层相当于逻辑层和UI层之间的一个桥梁
class PlaceViewModel :ViewModel() {
    private val searchLiveData= MutableLiveData<String>()

    val placeList=ArrayList<Place>()

    //如果ViewModel中的某个LiveData对象是调用另外的方法获取的，那么我们就可以借助switchMap()方法
    //否则仓库返回的LiveData对象将无法进行观察
    val placeLiveData=Transformations.switchMap(searchLiveData){
        query->
        //Log.d("PlaceViewModel", "PlaceViewModel"+query)
        Repository.searchPlaces(query)//获取真正的数据
    //将仓库层返回的LiveData对象转换成一个可供Activity观察的LiveData对象
    }
    //每当searchPlaces()函数被调用时，switchMap()方法所对应的转换函数就会执行
    fun searchPlaces(query:String){
        searchLiveData.value=query
    }
}
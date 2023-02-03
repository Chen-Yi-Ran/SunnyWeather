package com.example.sunnyweather.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

object Repository {

    //一般在仓库层中定义的方法，为了将异步(Retrofit的enqueue会切换到子线程执行网络操作)获取的数据以响应式编程的方式
    //通知给上一层，通常会返回一个LiveData对象。
    //liveData()函数会自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文
    //线程参数指定为Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中
    fun searchPlaces(query:String)=liveData(Dispatchers.IO){

        val result=try {
            val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status=="ok"){
                val places=placeResponse.places
                Result.success(places)
            }else{
                 Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
        }catch (e:Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)//将包装的结果发射出去，类似于调用LiveData的setValue()方法通知数据变化
    }
}
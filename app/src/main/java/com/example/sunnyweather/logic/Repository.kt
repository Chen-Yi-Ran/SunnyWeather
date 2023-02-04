package com.example.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    //一般在仓库层中定义的方法，为了将异步(Retrofit的enqueue会切换到子线程执行网络操作)获取的数据以响应式编程的方式
    //通知给上一层，通常会返回一个LiveData对象。
    //liveData()函数会自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文
    //线程参数指定为Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中
    //获取搜索地点
    fun searchPlaces(query:String)= fire(Dispatchers.IO){

      //  val result=try {
            val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
            if(placeResponse.status=="ok"){
                val places=placeResponse.places
                Result.success(places)
            }else{
                 Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
//        }catch (e:Exception){
//            Result.failure<List<Place>>(e)
//        }
//        emit(result)//将包装的结果发射出去，类似于调用LiveData的setValue()方法通知数据变化
    }

    //对获取实时天气和未来天气信息用统一的封装
    fun refreshWeather(lng:Double,lat:Double)= fire(Dispatchers.IO){
//        val result=try {
            //通过coroutineScope函数创建一个可以给挂起函数提供的协程作用域
                coroutineScope {
                //通过async函数中发起网络请求
                val deferredRealtime=async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily=async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }
                //分别调用它们的await()方法，就可以保证只有两个网络请求都成功之后，才会进一步执行程序
                val realtimeResponse=deferredRealtime.await()
                val dailyResponse=deferredDaily.await()
                    Log.d("Repository", realtimeResponse.toString())
                if(realtimeResponse.status=="ok"&&dailyResponse.status=="ok"){
                    //如果读取成功就将Realtime和Daily对象封装到一个Weather对象
                    val weather=Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}"
                    +"daily response status is ${dailyResponse.status}"
                    ))
                }
            }
//        }catch (e:Exception){
//            Result.failure<Weather>(e)
//        }
//        emit(result)
    }

    //新增一个fire()函数，使得每次只需要进行一次try-catch处理
    //lambda表达式要在挂起函数中运行，所以在高阶函数的函数类型中声明一个suspend关键字，这样传入的
    //lambda表达式也是拥有挂起函数的上下文
    private fun <T> fire(context:CoroutineContext,block:suspend()->Result<T>)=
        liveData<Result<T>>(context) {
            val result=try {
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place)=PlaceDao.savePlace(place)

    fun getSharedPlace()=PlaceDao.getSavedPlace()

    fun isPlaceSaved()=PlaceDao.isPlaceSaved()
}
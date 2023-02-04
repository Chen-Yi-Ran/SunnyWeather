package com.example.sunnyweather.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装
object SunnyWeatherNetwork  {

    private val placeService=ServiceCreator.create(PlaceService::class.java)

    suspend fun searchPlaces(query:String)= placeService.searchPlaces(query).await()

    private val weatherService=ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng:Double,lat:Double)= weatherService.getDailyWeather(lng,lat).await()

    suspend fun getRealtimeWeather(lng: Double,lat: Double)= weatherService.getRealtimeWeather(lng, lat).await()

    //将await()函数定义成了Call<T>的扩展函数，这样所有返回值是Call类型的Retrofit网络请求接口
    //都可以直接调用await()函数了。
    //await()函数中使用了suspendCoroutine函数来挂起当前协程，并且由于扩展函数的原因，我们现在拥有了
    //Call对象的上下文，那么这里就可以直接调用enqueue()方法让Retrofit发起网络请求。
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {
            continuation ->
            enqueue(object :Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body=response.body()
                    Log.d("SunnyWeatherNetwork", "--->body: "+response)
                    if(body!=null){
                        continuation.resume(body)
                    }else{
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
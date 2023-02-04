package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

//我们将数据模型类都定义在了RealtimeResponse的内部,这样可以防止出现和其他接口的数据模型类有同名冲突的情况
data class RealtimeResponse(
    val api_status: String,
    val api_version: String,
    val lang: String,
    val location: List<Double>,
    val result: Result,
    val server_time: Int,
    val status: String,
    val timezone: String,
    val tzshift: Int,
    val unit: String
){
    data class Result(
        val primary: Int,
        val realtime: Realtime
    )

    data class Realtime(
        //当后端接口和我们命名规则不一样可以使用@SerializedName这个注解,括号里(填写具体接口对应的值)， 后面编写你映射的值
        val  air_quality: AirQuality,
        val apparent_temperature: Double,
        val cloudrate: Double,
        val dswrf: Double,
        val humidity: Double,
        val life_index: LifeIndex,
        val precipitation: Precipitation,
        val pressure: Double,
        val skycon: String,
        val status: String,
        val temperature: Double,
        val visibility: Double,
        val wind: Wind
    )

    data class AirQuality(
        val aqi: Aqi,
        val co: Double,
        val description: Description,
        val no2: Int,
        val o3: Int,
        val pm10: Int,
        val pm25: Int,
        val so2: Int
    )

    data class LifeIndex(
        val comfort: Comfort,
        val ultraviolet: Ultraviolet
    )

    data class Precipitation(
        val local: Local,
        val nearest: Nearest
    )

    data class Wind(
        val direction: Double,
        val speed: Double
    )

    data class Aqi(
        val chn: Int,
        val usa: Int
    )

    data class Description(
        val chn: String,
        val usa: String
    )

    data class Comfort(
        val desc: String,
        val index: Int
    )

    data class Ultraviolet(
        val desc: String,
        val index: Double
    )

    data class Local(
        val datasource: String,
        val intensity: Double,
        val status: String
    )

    data class Nearest(
        val distance: Double,
        val intensity: Double,
        val status: String
    )

}


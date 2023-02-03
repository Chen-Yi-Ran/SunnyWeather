package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
     //Application不会被销毁，整个应用程序的生命周期内都不会回收 ，所以可以放心的持有这个引用context
    companion object{
         //相当于public static final String
        const val TOKEN="Z0QRZh6AGuYQlacM"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }
}
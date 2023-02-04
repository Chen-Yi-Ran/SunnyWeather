package com.example.sunnyweather.ui.weather

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.example.sunnyweather.logic.util.StatusBarUtils
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(WeatherViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
//        val decorView=window.decorView
//        decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor= Color.TRANSPARENT
        //initStatusBar()
        makeStatusBarTransparent(this)
        if(viewModel.locationLng==0.0){
            viewModel.locationLng=intent.getDoubleExtra("location_lng",0.0)
        }
        if(viewModel.locationLat==0.0){
            viewModel.locationLat=intent.getDoubleExtra("location_lat",0.0)
        }
        if(viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name")?:""
        }
        viewModel.weatherLiveData.observe(this, Observer {
            result->
            val weather=result.getOrNull()
            if(weather!=null){
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_LONG).show()
                 result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text=viewModel.placeName.toString()
        val realtime=weather.realtime
        val daily=weather.daily
        //填充now.xml布局中的数据
        val currentTempText="${realtime.temperature.toInt()}℃"
        currentTemp.text=currentTempText
        currentSky.text= getSky(realtime.skycon).info
        val currentPM25Text="空气指数${realtime.air_quality.aqi.chn.toInt()}"
        currentAQI.text=currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days=daily.skycon.size
        for(i in 0 until days){
            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
            val dateInfo=view.findViewById(R.id.dateInfo) as TextView
            val skyIcon=view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo=view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo=view.findViewById(R.id.temperatureInfo) as TextView
//            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Log.d("WeatherActivity", skycon.date.subSequence(0,10).toString())
        val skyDate = skycon.date.subSequence(0, 10).toString()
//            val date1 = Date()
//            val date = simpleDateFormat.format(date1)
//            Log.d("WeatherActivity", "date"+date.javaClass)
            dateInfo.text=skyDate
            val sky= getSky(skycon.value)
            Log.d("WeatherActivity", sky.info)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText="${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text=tempText
            forecastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        val lifeIndex=daily.life_index
        //生活质数很多，但只需要当天的数据
        coldRiskText.text=lifeIndex.coldRisk[0].desc
        dressingText.text=lifeIndex.dressing[0].desc
        ultravioletText.text=lifeIndex.ultraviolet[0].desc
        carWashingText.text=lifeIndex.carWashing[0].desc
        //将ScrollView设置为可见
        weatherLayout.visibility=View.VISIBLE

    }

    /**
     * 说明：
     * 1. SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖。
     * 2. SYSTEM_UI_FLAG_LIGHT_STATUS_BAR：设置状态栏图标为黑色或者白色
     * 3. StatusBarUtil 工具类是修改状态栏的颜色为白色。
     */
    private fun initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
           StatusBarUtils.setStatusBarColor(this,Color.WHITE)
        }
    }

    //显示透明栏·
    fun makeStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}
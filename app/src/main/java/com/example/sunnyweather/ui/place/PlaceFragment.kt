package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_places.*

class PlaceFragment :Fragment() {

    //lazy懒加载技术，当我们调用viewModel的时候才会执行lambda表达式的内容，并将最后的语句作为返回值
    val viewModel by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(PlaceViewModel::class.java)
    }
    private lateinit var adapter: PlaceAdapter

    //为Fragment创建视图(加载布局)时调用
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places,container,false)
    }

    //与Fragment相关联的Activity已经创建完毕时调用
    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //如果已经有存储的城市数据，那么就获取已存储的数据并解析成Place对象
        if(activity is MainActivity&&viewModel.isPlaceSaved()){
            val place=viewModel.getSavedPlace()
            val intent= Intent(context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            startActivity(intent)
            Log.d("PlaceFragment", "跳转成功 ")
            activity?.finish()

        }
        val layoutManager=LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        adapter= PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter=adapter
        searchPlaceEdit.addTextChangedListener {
            //监听搜索框内容的变化情况
            editable->
            val content=editable.toString()
            if(content.isNotEmpty()){
                viewModel.searchPlaces(content)
            }else{
                recyclerView.visibility=View.GONE
                bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }

        }
        viewModel.placeLiveData.observe(this, Observer {
            result->//接收真正的数据
            val places=result.getOrNull()
            if(places!=null){
                recyclerView.visibility=View.VISIBLE
                bgImageView.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"未能查询到任何地点",Toast.LENGTH_LONG).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

    }

}
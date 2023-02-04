package com.example.sunnyweather.ui.place

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment:PlaceFragment, private val placeList: List<Place>):
 RecyclerView.Adapter<PlaceAdapter.ViewHolder>()
{

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val placeName:TextView=view.findViewById(R.id.placeName)
        val placeAddress:TextView=view.findViewById(R.id.placeAddress)

       fun setData(place:Place){
           placeName.text=place.name
           placeAddress.text=place.formatted_address
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position=holder.adapterPosition
            val place=placeList[position]
            val intent=Intent(parent.context,WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            //Log.d("PlaceAdapter", place.location.lng+"-->"+place.location.lat)
            //在跳转到WeatherActivity之前，先存储选中的城市
            fragment.viewModel.savePlace(place)
            //启动weatherActivity
            fragment.startActivity(intent)
        }



        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place=placeList[position]
//        holder.placeName.text=place.name
//        holder.placeAddress.text=place.formatted_address
        holder.setData(place)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }


}
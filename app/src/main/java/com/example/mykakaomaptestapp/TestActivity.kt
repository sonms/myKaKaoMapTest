package com.example.mykakaomaptestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mykakaomaptestapp.databinding.ActivityTestBinding
import net.daum.mf.map.api.MapView

class TestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestBinding
    private var map : MapView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()

    }

    private fun initMap() {
        Log.d("TestActivity", "지도 세팅")
        map = MapView(this)
        binding.mapMvMapcontainer.addView(map)
    }



    override fun onStop() {
        super.onStop()
        if (map != null) {
            binding.mapMvMapcontainer.removeAllViews()
            binding.mapMvMapcontainer.removeAllViewsInLayout()
        }
    }
}
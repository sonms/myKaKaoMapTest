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
        Log.d("TestActivity1", "지도 세팅")
        map = MapView(this)
        binding.mapMvMapcontainer.addView(map)
    }

    override fun onStart() {
        super.onStart()
        Log.d("TestActivity1", "start")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TestActivity1", "pause")
        if (map != null) {
            binding.mapMvMapcontainer.removeAllViews()
            binding.mapMvMapcontainer.removeAllViewsInLayout()
            map = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("TestActivity1", "resume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("TestActivity1", "onstop")
        /*if (map != null) {
            binding.mapMvMapcontainer.removeAllViews()
            binding.mapMvMapcontainer.removeAllViewsInLayout()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TestActivity1", "onDestroy")
    }
}
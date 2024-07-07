package com.example.mykakaomaptestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mykakaomaptestapp.databinding.ActivityTestBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import net.daum.mf.map.api.MapView

class TestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestBinding
    private var map : com.kakao.vectormap.MapView? = null
    private var kakaoMapValue : KakaoMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()

    }

    private fun initMap() {
        Log.d("BlankFragment", "지도 세팅")
        map = binding.mapView
        //binding.mapMvMapcontainer.addView(map)
    }

    override fun onStart() {
        super.onStart()
        Log.d("TestActivity1", "start")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TestActivity1", "pause")
        if (map != null) {
            binding.mapView.removeAllViews()
            binding.mapView.removeAllViewsInLayout()
            map = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("TestActivity1", "resume")
        map?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e("BlankFragment", "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e("BlankFragment", "onMApError", error)

            }

        }, object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {
                //startPosition = super.getPosition()
                return super.getPosition()
            }

            override fun getZoomLevel(): Int {
                return 17
            }

            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.e("BlankFragment", "onMapReady")
                kakaoMapValue = kakaoMap
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Log.d("TestActivity1", "onstop")
        if (map != null) {
            binding.mapView.removeAllViews()
            binding.mapView.removeAllViewsInLayout()
            map = null
            initMap()
        } else {
            initMap()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TestActivity1", "onDestroy")
    }
}
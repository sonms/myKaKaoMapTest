package com.example.mykakaomaptestapp

import android.content.Intent
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.example.mykakaomaptestapp.databinding.ActivityTestBinding
import com.kakao.vectormap.*
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import net.daum.mf.map.api.MapView

class TestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestBinding
    private var map : com.kakao.vectormap.MapView? = null
    private var kakaoMapValue : KakaoMap? = null
    private var latLngString : String? = null
    private var location : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()

        binding.poiInfoText.setOnClickListener {
            val intent = Intent().apply {
                putExtra("location", location)
                putExtra("latLng", latLngString)
                putExtra("flag", 100)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initMap() {
        Log.d("TestActivity", "지도 세팅")
        map = binding.mapView
        //binding.mapMvMapcontainer.addView(map)
        map?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e("TestActivity", "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e("TestActivity", "onMApError", error)

            }

        }, object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {
                //startPosition = super.getPosition()
                return super.getPosition()
            }

            override fun getZoomLevel(): Int {
                return 17
            }

            override fun isVisible(): Boolean {
                return super.isVisible()
            }

            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.e("TestActivity", "onMapReady")
                kakaoMapValue = kakaoMap
                // 지도 클릭 리스너 설정
                kakaoMapValue!!.setOnMapClickListener { kakaoMap, latLng, pointF, poi ->
                    // POI 정보창 표시
                    showInfoWindow(poi, pointF)
                    location = poi.name
                    latLngString = "${latLng.latitude}"+" "+"${latLng.longitude}"
                }

            }
        })
    }

    private fun showInfoWindow(poi: Poi?, pointF: PointF) {
        if (poi != null) {
            // TextView에 POI 정보를 설정
            binding.poiInfoText.text = poi.name

            // dp 단위를 픽셀로 변환
            val offsetY = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics
            ).toInt()

            // TextView의 위치를 화면 좌표로 설정
            binding.poiInfoText.x = pointF.x
            binding.poiInfoText.y = pointF.y - binding.poiInfoText.height - offsetY

            // TextView를 화면에 표시
            binding.poiInfoText.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("TestActivity", "start")
    }

    override fun onPause() {
        super.onPause()
        Log.d("TestActivity", "pause")
        /*if (map != null) {
            binding.mapView.removeAllViews()
            binding.mapView.removeAllViewsInLayout()
            map = null
        }*/
    }

    override fun onResume() {
        super.onResume()
        Log.d("TestActivity", "resume")

    }

    override fun onStop() {
        super.onStop()
        Log.d("TestActivity", "onstop")
       /* if (map != null) {
            binding.mapView.removeAllViews()
            binding.mapView.removeAllViewsInLayout()
            map = null
            initMap()
        } else {
            initMap()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TestActivity", "onDestroy")
    }
}
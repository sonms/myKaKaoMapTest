package com.example.mykakaomaptestapp

import android.content.Intent
import android.graphics.Color
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mykakaomaptestapp.data.ResultSearchKeyword
import com.example.mykakaomaptestapp.databinding.FragmentBlankBinding
import com.kakao.vectormap.*
import com.kakao.vectormap.label.*
import com.kakao.vectormap.mapwidget.InfoWindow
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiImage
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding : FragmentBlankBinding
    private var map : MapView? = null
    private var kakaoMapValue : KakaoMap? = null
    private var infoWindow: InfoWindow? = null
    private var centerLabel: Label? = null
    private val requestingLocationUpdates = false
    private val locationRequest: LocationRequest? = null
    private var startPosition: LatLng? = null
    private var labelLayer: LabelLayer? = null
    private var latLngList = ArrayList<LatLng>()
    private var labelLatLng : LatLng? = null

    private var pendingData: String? = null
    private var pendingLatLng: String? = null
    private var PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlankBinding.inflate(inflater, container, false)
        //initMap()
        multiplePermissionsLauncher.launch(PERMISSIONS)
        binding.testTv.setOnClickListener {
            searchKeyword("은행")
            val intent = Intent(requireActivity(), TestActivity::class.java)
            requestActivity.launch(intent)
        }

        searchKeyword("은행")
        return binding.root
    }

    private fun initMap() {
        Log.d("BlankFragment", "지도 세팅")
        latLngList.apply {
            add(LatLng.from(37.593875, 127.051833))
            add(LatLng.from(37.59368107000002, 127.05285294))
        }
        map = binding.mapView
        //binding.mapMvMapcontainer.addView(map)
    }

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data?.getStringExtra("location") as String?
            val latLng = result.data?.getStringExtra("latLng")
            val flag = result.data?.getIntExtra("flag", -1)
            if (flag == 100) {
                Log.d("requestActivity couroutine", "initMapView")
                if (data != null && latLng != null) {
                    //loadNearbyPostData(data, latLng)
                    pendingData = data
                    pendingLatLng = latLng
                }
                /*sharedViewModel.selectedLocation.observe(viewLifecycleOwner) { location ->
                    Log.d("DEBUG", "LiveData observed: $location")
                    location?.let {

                        loadNearbyPostData(location.postId)

                    }
                }*/
            }
        }
    }

    private fun initializeMapIfNeeded() {
        if (map == null) {
            try {
                Log.e("SearchFragment", "try")
                initMap()
            } catch (re: RuntimeException) {
                Log.e("SearchFragment", re.toString())
            }
        } else {
            Log.e("SearchFragment", "map is not null")
            map = null
            initMap()
        }
    }

    private fun requestDeniedPermissionsAgain(deniedPermissions: Set<String>) {
        // 권한 재요청을 위한 대화상자 또는 로직 구현
        AlertDialog.Builder(requireContext())
            .setTitle("권한 요청")
            .setMessage("이 기능을 사용하려면 권한이 필요합니다. 권한을 승인하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                multiplePermissionsLauncher.launch(deniedPermissions.toTypedArray())
            }
            .setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
                handlePermissionDenied()
            }
            .create()
            .show()
    }
    private fun handlePermissionDenied() {
        // 권한이 거부된 경우 지도 뷰를 숨기거나 처리하는 코드 추가
        binding.mapView.visibility = View.GONE
        Toast.makeText(requireContext(), "권한이 거부되어 지도를 표시할 수 없습니다.", Toast.LENGTH_SHORT).show()
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }
    private fun startMapLifeCycle() {
        map?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e("BlankFragment", "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e("BlankFragment", "onMapError", error)
            }

        }, object : KakaoMapReadyCallback() {
            override fun getPosition(): LatLng {
                return super.getPosition()
            }

            override fun getZoomLevel(): Int {
                return 17
            }

            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.e("BlankFragment", "onMapReady")
                kakaoMapValue = kakaoMap
                labelLayer = kakaoMap.labelManager!!.layer
                val trackingManager = kakaoMap.trackingManager

                if (pendingData != null && pendingLatLng != null) {
                    val target = pendingLatLng!!.split(" ").map { it.toDouble() }
                    startPosition = LatLng.from(target.first(), target.last())
                    centerLabel = labelLayer!!.addLabel(
                        LabelOptions.from("centerLabel", startPosition)
                            .setStyles(LabelStyle.from(R.drawable.map_poi_icon).setAnchorPoint(0.5f, 0.5f))
                            .setRank(1)
                    )

                    trackingManager!!.startTracking(centerLabel)
                }

                kakaoMapValue!!.setOnMapClickListener { _, latLng, _, poi ->
                    showInfoWindow(position, poi)
                    trackingManager?.stopTracking()

                    if (poi.name.isNotEmpty()) {
                        labelLatLng = LatLng.from(latLng.latitude, latLng.longitude)
                        val style = kakaoMap.labelManager?.addLabelStyles(
                            LabelStyles.from(
                                LabelStyle.from(R.drawable.map_poi_icon).apply {
                                    setAnchorPoint(0.5f, 0.5f)
                                    isApplyDpScale = false
                                }
                            )
                        )
                        val options = LabelOptions.from(labelLatLng).setStyles(style).setRank(1)
                        val label = labelLayer?.addLabel(options)
                        label?.let { trackingManager?.startTracking(it) }
                    }
                }

                kakaoMapValue!!.setOnLabelClickListener { _, _, label ->
                    trackingManager?.startTracking(label)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        map?.resume()
        startMapLifeCycle()
       /* map?.start(object : MapLifeCycleCallback() {
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
                labelLayer = kakaoMap.labelManager!!.layer
                val trackingManager = kakaoMap.trackingManager

                if (pendingData != null && pendingLatLng != null) {
                    Log.e("pendingTest", pendingData.toString())
                    Log.e("pendingTest", pendingLatLng.toString())
                    // 현재 위치를 나타낼 label를 그리기 위해 kakaomap 인스턴스에서 LabelLayer를 가져옵니다.
                    val layer = kakaoMap.labelManager!!.layer
                    val target = pendingLatLng!!.split(" ").map { it.toDouble() }
                    Log.e("pedingTest", target.toString())
                    startPosition = LatLng.from(target.first(), target.last())
                    // LabelLayer에 라벨을 추가합니다. 카카오 지도 API 공식 문서에 지도에서 사용하는 이미지는 drawable-nodpi/ 에 넣는 것을 권장합니다.
                    //Label 을 생성하기 위해 초기화 값을 설정하는 클래스.
                    centerLabel = layer!!.addLabel(
                        LabelOptions.from("centerLabel", startPosition)
                            .setStyles(
                                LabelStyle.from(R.drawable.map_poi_icon).setAnchorPoint(0.5f, 0.5f)
                            )
                            .setRank(1) //우선순위
                    )

                    trackingManager!!.startTracking(centerLabel)
                    //trackingManager.stopTracking()
                }
                //trackingManager.stopTracking()
                //startLocationUpdates()
                *//*for (i in latLngList.indices) {
                    // 스타일 지정. LabelStyle.from()안에 원하는 이미지 넣기
                    val style = kakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.map_poi_icon)))
                    // 라벨 옵션 지정. 위경도와 스타일 넣기
                    val options = LabelOptions.from(LatLng.from(latLngList[i].latitude, latLngList[i].longitude)).setStyles(style)
                    // 레이어 가져오기
                    val layer = kakaoMap.labelManager?.layer
                    // 레이어에 라벨 추가
                    layer?.addLabel(options)
                }*//*
                kakaoMapValue!!.setOnMapClickListener { kakaoMap, latLng, pointF, poi ->
                    // POI 정보창 표시
                    showInfoWindow(position, poi)

                    // 현재 트래킹 중지
                    trackingManager?.stopTracking()

                    Log.e("kakaoMapValue", "$latLng $pointF ${poi.name}")

                    if (poi.name.isNotEmpty()) {
                        labelLatLng = LatLng.from(latLng.latitude, latLng.longitude)
                        Log.e("labelLatLng", "$labelLatLng")

                        // 레이어 가져오기
                        val labelLayer = kakaoMap.labelManager?.layer

                        // 스타일 지정
                        val style = kakaoMap.labelManager?.addLabelStyles(
                            LabelStyles.from(
                                LabelStyle.from(R.drawable.map_poi_icon).apply {
                                    setAnchorPoint(0.5f, 0.5f)
                                    isApplyDpScale = false
                                }
                            )
                        )

                        // 라벨 옵션 지정
                        val options = LabelOptions.from(labelLatLng).setStyles(style).setRank(1)

                        // 라벨 추가
                        val label = labelLayer?.addLabel(options)

                        // 라벨로 트래킹 시작
                        if (label != null) {
                            trackingManager?.startTracking(label)
                        } else {
                            Log.e("kakaoMapValue", "Label is null, tracking cannot be started.")
                        }
                    }
                }

                kakaoMapValue!!.setOnLabelClickListener { kakaoMap, labelLayer, label ->
                    //trackingManager.stopTracking()
                    trackingManager?.startTracking(label)
                }
            }
        })*/
    }
/*
    private fun setupMapView() {
        val location = LatLng(37.5946, 127.0527)
        kakaoMapValue.setMapCenterPointAndZoomLevel(location, 3, true)
        val markerOptions = MarkerOptions()
        markerOptions.position(location)
        markerOptions.title("경희대학교")
        markerOptions.snippet("서울특별시 동대문구 경희대로6길 7-13")
        mapView.addMarker(markerOptions)
    }*/

private fun updateLabel(labelId: String) { //poi icon변경
    //labelLayer!!.getLabel(labelId).ch(rank.toLong())
    labelLayer!!.getLabel(labelId).changeStyles(
        LabelStyles.from(
            LabelStyle.from(R.drawable.window_tail).setApplyDpScale(false)
        )
    )
}
    private fun showInfoWindow(position: LatLng, poi: Poi) {
        infoWindow?.remove()
        val body = GuiLayout(Orientation.Vertical)
        body.setPadding(15, 15, 15, 13)
        val image = GuiImage(R.drawable.window_body, true)
        image.setFixedArea(7, 7, 7, 7)
        body.setBackground(image)
        var text = GuiText("isPoi= " + poi.isPoi())
        text.setTextSize(23)
        text.paddingRight = 13
        body.addView(text)
        if (poi.isPoi()) {
            text = GuiText("LayerId=" + poi.getLayerId())
            text.setTextSize(23)
            text.paddingTop = 8
            text.setTextColor(Color.parseColor("#003F63"))
            body.addView(text)
            text = GuiText("PoiId=" + poi.getPoiId())
            text.setTextSize(23)
            text.paddingTop = 8
            text.setTextColor(Color.parseColor("#003F63"))
            body.addView(text)
            text = GuiText("Name=" + poi.getName())
            text.setTextSize(23)
            text.paddingTop = 8
            text.setTextColor(Color.parseColor("#003F63"))
            body.addView(text)
        }
        val options = InfoWindowOptions.from(position)
        options.setBody(body)
        options.setBodyOffset(0f, -4f)
        options.setTail(GuiImage(R.drawable.window_tail, false))
        infoWindow = kakaoMapValue?.mapWidgetManager?.infoWindowLayer?.addInfoWindow(options)
    }

    fun onCheckBoxClicked(view: View) {
        val isChecked = (view as CheckBox).isChecked
        when (view.getId()) {
            R.id.ck_poi_visible -> kakaoMapValue!!.setPoiVisible(isChecked)
            R.id.ck_poi_clickable -> kakaoMapValue!!.setPoiClickable(isChecked)
        }
    }

    fun onScaleClicked(view: View) {
        when (view.id) {
            R.id.btn_poi_small -> kakaoMapValue!!.setPoiScale(PoiScale.SMALL)
            R.id.btn_poi_regular -> kakaoMapValue!!.setPoiScale(PoiScale.REGULAR)
            R.id.btn_poi_large -> kakaoMapValue!!.setPoiScale(PoiScale.LARGE)
            R.id.btn_poi_xlarge -> kakaoMapValue!!.setPoiScale(PoiScale.XLARGE)
        }
    }

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filter { !it.value }.keys
            val grantedPermissions = permissions.filter { it.value }.keys

            if (grantedPermissions.isNotEmpty()) {
                Toast.makeText(requireContext(), "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                initializeMapIfNeeded()
            }

            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(requireContext(), "${deniedPermissions.joinToString(", ")} 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                // 권한 재요청 코드
                requestDeniedPermissionsAgain(deniedPermissions)
            }
        }
    override fun onPause() {
        super.onPause()
        Log.d("Blank", "pause")
        map?.pause()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Blank", "start")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Blank", "destory")
        map?.finish()
        map = null
    }
    /*override fun onResume() {
        super.onResume()
        if (map != null) {
            binding.mapMvMapcontainer.removeAllViews()
            binding.mapMvMapcontainer.removeAllViewsInLayout()
        }
    }*/

    override fun onStop() {
        super.onStop()
        Log.d("Blank", "stop")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            initMap()
        } else {
            map?.resume()
        }
    }

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 176b61538805434e8f2bec528b6b44cf"  // REST API 키
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
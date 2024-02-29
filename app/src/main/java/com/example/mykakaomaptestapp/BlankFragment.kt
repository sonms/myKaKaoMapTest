package com.example.mykakaomaptestapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mykakaomaptestapp.data.ResultSearchKeyword
import com.example.mykakaomaptestapp.databinding.FragmentBlankBinding
import net.daum.mf.map.api.MapView
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


        binding.testTv.setOnClickListener {
            searchKeyword("은행")
            val intent = Intent(requireActivity(), TestActivity::class.java)
            startActivity(intent)
        }

        searchKeyword("은행")
        return binding.root
    }

    private fun initMap() {
        Log.d("BlankFragment", "지도 세팅")
        map = MapView(requireActivity())
        binding.mapMvMapcontainer.addView(map)
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

    override fun onPause() {
        super.onPause()
        Log.d("Blank", "pause")
        if (map != null) {
            binding.mapMvMapcontainer.removeAllViews()
            binding.mapMvMapcontainer.removeAllViewsInLayout()
            map = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Blank", "resume")
        /*if (map == null) {
            initMap()
        }*/
    }

    override fun onStart() {
        super.onStart()
        Log.d("Blank", "start")
        if (map == null) {
            initMap()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Blank", "destory")
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
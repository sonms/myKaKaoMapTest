package com.example.mykakaomaptestapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mykakaomaptestapp.databinding.ActivityMainBinding
import com.kakao.vectormap.KakaoMapSdk
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG_HOME = "home_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getAppKeyHash()
        KakaoMapSdk.init(this@MainActivity, "12236149bbd31cb11ee5867aaa8fb79e")


        setFragment(TAG_HOME, BlankFragment())

    }

    /*fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for(i in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(i.toByteArray())

                val something = String(Base64.encode(md.digest(), 0)!!)
                Log.e("Debug key", something)
            }
        } catch(e: Exception) {
            Log.e("Not found", e.toString())
        }
    }*/

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val bt = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            bt.add(R.id.fragment_content, fragment, tag).addToBackStack(null)
        }

        val home = manager.findFragmentByTag(TAG_HOME)

        if (home != null) {
            bt.hide(home)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                bt.show(home)
            }
        }

        bt.commitAllowingStateLoss()
    }

}
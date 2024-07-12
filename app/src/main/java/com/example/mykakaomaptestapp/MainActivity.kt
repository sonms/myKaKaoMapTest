package com.example.mykakaomaptestapp

import android.content.Intent
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
    private val TAG_SEARCH = "search_fragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getAppKeyHash()
        KakaoMapSdk.init(this@MainActivity, "12236149bbd31cb11ee5867aaa8fb79e")

        initNavigationBar()
        setFragment(TAG_HOME, HomeFragment())

    }

    private fun initNavigationBar() {
        binding.bottomNavigationView.
        setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    //setToolbarView(TAG_HOME, oldTAG)
                    //setFragment(TAG_HOME, BlankFragment())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_content, HomeFragment())
                        .commit()
                }

                R.id.navigation_search -> {
                    //setToolbarView(TAG_HOME, oldTAG)
                    //setFragment(TAG_SEARCH, BlankFragment())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_content, BlankFragment())
                        .commit()
                }

                else -> {

                }

            }
            true
        }
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
        val search = manager.findFragmentByTag(TAG_SEARCH)

        if (home != null) {
            bt.hide(home)
        }
        if (search != null) {
            bt.hide(search)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                bt.show(home)
            }
        } else if (tag == TAG_SEARCH) {
            if (search != null) {
                bt.show(search)
            }
        }

        bt.commitAllowingStateLoss()
    }

}
package com.example.movie.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.movie.R
import com.example.movie.adapter.MyViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var mBottomNavigationView: BottomNavigationView? = null
    private var mViewPager2: ViewPager2? = null
    private var tvTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTitle = findViewById<TextView?>(R.id.tv_title)
        mBottomNavigationView = findViewById<BottomNavigationView?>(R.id.bottom_navigation)
        mViewPager2 = findViewById<ViewPager2?>(R.id.viewpager_2)
        val myViewPagerAdapter = MyViewPagerAdapter(this)
        mViewPager2?.adapter = myViewPagerAdapter
        mViewPager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        mBottomNavigationView?.menu?.findItem(R.id.nav_home)?.isChecked = true
                        tvTitle?.text = getString(R.string.nav_home)
                    }
                    1 -> {
                        mBottomNavigationView?.menu?.findItem(R.id.nav_favorite)?.isChecked = true
                        tvTitle?.text = getString(R.string.nav_favorite)
                    }
                    2 -> {
                        mBottomNavigationView?.menu?.findItem(R.id.nav_history)?.isChecked = true
                        tvTitle?.text = getString(R.string.nav_history)
                    }
                }
            }
        })
        mBottomNavigationView?.setOnItemSelectedListener { item ->
            val id = item.itemId
            if (id == R.id.nav_home) {
                mViewPager2?.currentItem = 0
                tvTitle?.text = getString(R.string.nav_home)
            } else if (id == R.id.nav_favorite) {
                mViewPager2?.currentItem = 1
                tvTitle?.text = getString(R.string.nav_favorite)
            } else if (id == R.id.nav_history) {
                mViewPager2?.currentItem = 2
                tvTitle?.text = getString(R.string.nav_history)
            }
            true
        }
    }
}
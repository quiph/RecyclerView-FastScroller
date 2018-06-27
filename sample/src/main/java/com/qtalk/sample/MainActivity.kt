package com.qtalk.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.qtalk.sample.fragments.BasicFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mViewPager : ViewPager
    private lateinit var mViewPagerAdapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewPager = findViewById(R.id.view_pager)
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mViewPagerAdapter
    }

    class ViewPagerAdapter internal  constructor(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        //internal ViewPagerAdapter class, with 3 fragments.
        private val COUNT = 3

        override fun getItem(position: Int): Fragment? {
            lateinit var fragment : Fragment
            when (position){
                0 -> fragment = BasicFragment()
                1 -> fragment = BasicFragment()
                2 -> fragment = BasicFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return COUNT
        }
        // for the pageTitleStrip View at the top of the viewpager
        override fun getPageTitle(position: Int): CharSequence? {
            return "Tab " + (position + 1)
        }
    }
}

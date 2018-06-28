package com.qtalk.sample

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.qtalk.sample.fragments.AdvancedFragment
import com.qtalk.sample.fragments.BasicFragment
import com.qtalk.sample.fragments.ContactsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val VIEWPAGER_COUNT = 3
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager, this)
    }

   private class ViewPagerAdapter internal  constructor(fm: FragmentManager?,  val mContext: Context?) : FragmentPagerAdapter(fm) {
        //internal ViewPagerAdapter class, with 3 fragments.

        override fun getItem(position: Int): Fragment? {
            lateinit var fragment : Fragment
            when (position){
                0 -> fragment = BasicFragment()
                1 -> fragment = AdvancedFragment()
                2 -> fragment = ContactsFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return VIEWPAGER_COUNT
        }
        // for the pageTitleStrip View at the top of the viewpager
        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return  mContext?.resources?.getString(R.string.basic_fragment)
                1 -> return  mContext?.resources?.getString(R.string.advanced_fragment)
                2 -> return  mContext?.resources?.getString(R.string.contacts_fragment)
            }
            return ""
        }
    }
}

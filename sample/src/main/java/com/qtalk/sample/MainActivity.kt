package com.qtalk.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.qtalk.sample.fragments.AdvancedFragment
import com.qtalk.sample.fragments.BasicFragment
import com.qtalk.sample.fragments.ContactsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val VIEWPAGER_COUNT = 3
        private const val PAGE_INDEX_BASIC = 0
        private const val PAGE_INDEX_ADVANCE = 1
        private const val PAGE_INDEX_CONTACTS = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.adapter = ViewPagerAdapter(supportFragmentManager, this)
    }

   private class ViewPagerAdapter internal constructor(fm: FragmentManager, val mContext: Context?) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        //internal ViewPagerAdapter class, with 3 fragments.
        override fun getItem(position: Int): Fragment {
            return when (position){
                PAGE_INDEX_BASIC -> BasicFragment()
                PAGE_INDEX_ADVANCE -> AdvancedFragment()
                PAGE_INDEX_CONTACTS -> ContactsFragment()
                else -> throw IllegalArgumentException("Not expecting $position.")
            }
        }

        override fun getCount(): Int {
            return VIEWPAGER_COUNT
        }
        // for the pageTitleStrip View at the top of the viewpager
        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                PAGE_INDEX_BASIC -> return  mContext?.resources?.getString(R.string.basic_fragment)
                PAGE_INDEX_ADVANCE -> return  mContext?.resources?.getString(R.string.advanced_fragment)
                PAGE_INDEX_CONTACTS -> return  mContext?.resources?.getString(R.string.contacts_fragment)
            }
            return ""
        }
    }
}

package com.qtalk.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.qtalk.sample.fragments.AdvancedFragment
import com.qtalk.sample.fragments.BasicFragment
import com.qtalk.sample.fragments.ContactsFragment
import com.qtalk.sample.fragments.ProgrammingLanguagesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val VIEWPAGER_COUNT = 4
        private const val PAGE_INDEX_BASIC = 0
        private const val PAGE_INDEX_ADVANCE = 1
        private const val PAGE_INDEX_LANGUAGES = 2
        private const val PAGE_INDEX_CONTACTS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return VIEWPAGER_COUNT
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    PAGE_INDEX_BASIC -> BasicFragment()
                    PAGE_INDEX_ADVANCE -> AdvancedFragment()
                    PAGE_INDEX_LANGUAGES -> ProgrammingLanguagesFragment()
                    PAGE_INDEX_CONTACTS -> ContactsFragment()
                    else -> throw IllegalArgumentException("Not expecting $position.")
                }
            }
        }

        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        TabLayoutMediator(
            tabLayout, view_pager, true, true
        ) { tab, position ->
            tab.text = when (position) {
                PAGE_INDEX_BASIC -> resources?.getString(R.string.basic_fragment)
                PAGE_INDEX_ADVANCE -> resources?.getString(R.string.advanced_fragment)
                PAGE_INDEX_LANGUAGES -> resources?.getString(R.string.programming_languages_fragment)
                PAGE_INDEX_CONTACTS -> resources?.getString(R.string.contacts_fragment)
                else -> throw IndexOutOfBoundsException("View pager size is $VIEWPAGER_COUNT, can't access $position")
            }
        }.attach()
    }
}

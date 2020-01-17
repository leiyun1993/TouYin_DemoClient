package com.yunlei.douyinlike.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yunlei.douyinlike.fragment.InfoFragment
import com.yunlei.douyinlike.fragment.VideoFragment

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class ItemViewPagerAdapter(fragment: Fragment, private val url: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> VideoFragment.getNewInstance(url)
        else -> InfoFragment.getNewInstance(url)
    }
}
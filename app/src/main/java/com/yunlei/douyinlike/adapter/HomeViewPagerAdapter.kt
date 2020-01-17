package com.yunlei.douyinlike.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yunlei.douyinlike.fragment.ItemFragment

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class HomeViewPagerAdapter(fragment: Fragment, private val list:MutableList<String>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment = ItemFragment.getNewInstance(list[position])
}
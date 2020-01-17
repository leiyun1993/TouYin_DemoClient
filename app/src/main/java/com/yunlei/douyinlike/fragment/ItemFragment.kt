package com.yunlei.douyinlike.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.yunlei.douyinlike.R
import com.yunlei.douyinlike.adapter.ItemViewPagerAdapter
import com.yunlei.douyinlike.entity.ChangePageEvent
import kotlinx.android.synthetic.main.fragment_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.bundleOf

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class ItemFragment : Fragment() {

    companion object {
        @JvmStatic
        fun getNewInstance(url: String): ItemFragment {
            val fragment = ItemFragment()
            fragment.arguments = bundleOf("url" to url)
            return fragment
        }
    }

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        url = arguments?.getString("url") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemViewPager.adapter = ItemViewPagerAdapter(this, url)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventChangePage(event:ChangePageEvent){
        itemViewPager.currentItem = event.position
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

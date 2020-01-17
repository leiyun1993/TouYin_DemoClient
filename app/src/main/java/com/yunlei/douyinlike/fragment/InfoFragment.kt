package com.yunlei.douyinlike.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.yunlei.douyinlike.R
import com.yunlei.douyinlike.entity.ChangePageEvent
import com.yunlei.douyinlike.entity.ToolbarStateEvent
import kotlinx.android.synthetic.main.fragment_info.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.bundleOf

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class InfoFragment : Fragment() {
    companion object {
        @JvmStatic
        fun getNewInstance(url: String): InfoFragment {
            val fragment = InfoFragment()
            fragment.arguments = bundleOf("url" to url)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backBtn.setOnClickListener {
            EventBus.getDefault().post(ChangePageEvent(0))
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(ToolbarStateEvent(false))
    }

}

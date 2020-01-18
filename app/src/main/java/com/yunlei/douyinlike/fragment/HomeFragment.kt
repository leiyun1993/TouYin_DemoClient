package com.yunlei.douyinlike.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.gyf.immersionbar.ImmersionBar
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yunlei.douyinlike.R
import com.yunlei.douyinlike.adapter.HomeViewPagerAdapter
import com.yunlei.douyinlike.entity.ClearPositionEvent
import com.yunlei.douyinlike.entity.ToolbarStateEvent
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class HomeFragment : Fragment() {

    companion object {
        @JvmStatic
        fun getNewInstance(): HomeFragment = HomeFragment()
    }

    private val urlList = mutableListOf(
            "https://chengdu-1259068866.cos.ap-chengdu.myqcloud.com/3f4a65314ae666962dd1870d4d390d56.mp4",
            "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4",
            "https://chengdu-1259068866.cos.ap-chengdu.myqcloud.com/b92d429b70dab52b830d99124d908f73.mp4",
//            "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
            "https://chengdu-1259068866.cos.ap-chengdu.myqcloud.com/f2cd4ce84bc6c8948198c93d74856ffb.mp4",
//            "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
            "https://chengdu-1259068866.cos.ap-chengdu.myqcloud.com/177d7bcff446ba6670089d0793a5a8df.mp4"
    )
    private lateinit var mPagerAdapter: HomeViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this).titleBar(toolBar).init()
        mPagerAdapter = HomeViewPagerAdapter(this, urlList)
        viewPager.adapter = mPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                EventBus.getDefault().post(ClearPositionEvent(true))
                if (position==urlList.size-1){
                    urlList.add(urlList[0])
                    mPagerAdapter.notifyDataSetChanged()
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
//        GSYVideoManager.onResume()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventToolbarState(event:ToolbarStateEvent){
        toolBar.visibility = if (event.isShow){
            View.VISIBLE
        }else{
            View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }
}

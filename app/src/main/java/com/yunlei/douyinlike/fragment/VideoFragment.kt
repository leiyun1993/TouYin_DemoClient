package com.yunlei.douyinlike.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.shuyu.gsyvideoplayer.GSYVideoManager

import com.yunlei.douyinlike.R
import com.yunlei.douyinlike.entity.ClearPositionEvent
import com.yunlei.douyinlike.entity.ToolbarStateEvent
import com.yunlei.douyinlike.utils.log
import kotlinx.android.synthetic.main.fragment_video.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.bundleOf

/**
 * @author Yun.Lei
 * @email waitshan@163.com
 * @date 2020/1/18
 */
class VideoFragment : Fragment() {


    companion object {
        @JvmStatic
        fun getNewInstance(url: String): VideoFragment {
            val fragment = VideoFragment()
            fragment.arguments = bundleOf("url" to url)
            return fragment
        }
    }

    private var url = ""
    private var mCurrentPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        url = arguments?.getString("url") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mCurrentPosition = 0
        return inflater.inflate(R.layout.fragment_video, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoPlayer?.apply {
            backButton.visibility = GONE
            titleTextView.visibility = GONE
            fullscreenButton.visibility = GONE
            isNeedShowWifiTip = false
            isLooping = true
            dismissControlTime = 0
        }
        videoPlayer.setUpLazy(url, false, null, null, "")
//        likeLayout.visibility = View.GONE
        likeLayout.onPauseListener = {
            if (videoPlayer.gsyVideoManager.isPlaying) {
                videoPlayer?.onVideoPause()
            } else {
                videoPlayer?.onVideoResume(false)
            }
//            videoPlayer.startButton.performClick()
            log("performClick")
        }
        likeLayout.onLikeListener = {
            if (!likeBtn.isLiked)
                likeBtn.performClick()
        }

    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(ToolbarStateEvent(true))
        if (mCurrentPosition > 0) {
            videoPlayer?.onVideoResume(false)
        } else {
            videoPlayer?.postDelayed({
                videoPlayer?.startPlayLogic()
            }, 200)
        }

        log("onResume->mCurrentPosition:$mCurrentPosition")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventClearPosition(event: ClearPositionEvent) {
        if (event.isClear) {
            mCurrentPosition = 0
        }
    }

    override fun onPause() {
        super.onPause()
        likeLayout?.onPause()
        videoPlayer?.onVideoPause()
        mCurrentPosition = videoPlayer?.gsyVideoManager?.currentPosition ?: 0

        log("onPause")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}

package com.yunlei.douyinlike.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import com.yunlei.douyinlike.R
import java.lang.ref.WeakReference
import java.util.*

/**
 * 类名：LikeLayout
 * 作者：Yun.Lei
 * 功能：
 * 创建日期：2017-12-20 14:33
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
class LikeLayout : FrameLayout {

    var onLikeListener: () -> Unit = {}     //屏幕点赞后，点赞按钮需同步点赞
    var onPauseListener: () -> Unit = {}     //暂停 或者 继续播放

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var icon: Drawable = resources.getDrawable(R.mipmap.ic_heart)
    private var mClickCount = 0     //点击一次是暂停，多次是点赞
    private val mHandler = LikeLayoutHandler(this)

    init {
        clipChildren = false        //避免旋转时红心被遮挡
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {     //按下时在Layout中生成红心
            val x = event.x
            val y = event.y
            mClickCount++
            mHandler.removeCallbacksAndMessages(null)
            if (mClickCount >= 2) {
                addHeartView(x, y)
                onLikeListener()
                mHandler.sendEmptyMessageDelayed(1, 500)
            } else {
                mHandler.sendEmptyMessageDelayed(0, 500)
            }

        }
        return true
    }


    private fun pauseClick() {
        if (mClickCount == 1) {
            onPauseListener()
        }
        mClickCount = 0
    }

    fun onPause() {
        mClickCount = 0
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 在Layout中添加红心并，播放消失动画
     */
    private fun addHeartView(x: Float, y: Float) {
        val lp = LayoutParams(icon.intrinsicWidth, icon.intrinsicHeight)    //计算点击的点位红心的下部中间
        lp.leftMargin = (x - icon.intrinsicWidth / 2).toInt()
        lp.topMargin = (y - icon.intrinsicHeight).toInt()
        val img = ImageView(context)
        img.scaleType = ImageView.ScaleType.MATRIX
        val matrix = Matrix()
        matrix.postRotate(getRandomRotate())       //设置红心的微量偏移
        img.imageMatrix = matrix
        img.setImageDrawable(icon)
        img.layoutParams = lp
        addView(img)
        val animSet = getShowAnimSet(img)
        val hideSet = getHideAnimSet(img)
        animSet.start()
        animSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                hideSet.start()
            }
        })
        hideSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                removeView(img)     //动画结束移除红心
            }
        })
    }

    /**
     * 刚点击的时候的一个缩放效果
     */
    private fun getShowAnimSet(view: ImageView): AnimatorSet {
        // 缩放动画
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f)
        val animSet = AnimatorSet()
        animSet.playTogether(scaleX, scaleY)
        animSet.duration = 100
        return animSet
    }

    /**
     * 缩放结束后到红心消失的效果
     */
    private fun getHideAnimSet(view: ImageView): AnimatorSet {
        // 1.alpha动画
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.1f)
        // 2.缩放动画
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f)
        // 3.translation动画
        val translation = ObjectAnimator.ofFloat(view, "translationY", 0f, -150f)
        val animSet = AnimatorSet()
        animSet.playTogether(alpha, scaleX, scaleY, translation)
        animSet.duration = 500
        return animSet
    }

    /**
     * 生成一个随机的左右偏移量
     */
    private fun getRandomRotate(): Float = (Random().nextInt(20) - 10).toFloat()

    companion object {
        private class LikeLayoutHandler(view: LikeLayout) : Handler() {
            private val mView = WeakReference(view)
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when(msg?.what){
                    0-> mView.get()?.pauseClick()
                    1-> mView.get()?.onPause()
                }

            }
        }
    }
}
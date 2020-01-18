# TouYin 仿抖音APP视频切换和点赞效果

### 1、ViewPager2

网上很多仿抖音视频切换的很多都是使用自定义竖方向的ViewPager或者使用RecyclerView+PagerSnapHelper实现。
但是这两种方式其实都有一定的缺陷：

1、但是ViewPager实现，生命周期有一定问题(ps:FragmentPagerAdapter在新版本中提供了BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT来解决这个问题)，notifyDataSetChanged支持很不好。关于生命周期的更新可以参考[androidx中的Fragment懒加载方案](https://blog.csdn.net/qq_36486247/article/details/102531304)

2、而使用RecyclerView+PagerSnapHelper实现，需要自己处理生命周期。

其实我们可以使用androidx中提供的ViewPage2来实现这个功能。
```
dependencies {
	implementation "androidx.viewpager2:viewpager2:1.0.0"
}
```
查看源码，ViewPager2继承自ViewGroup，其中发现了三个比较重要的成员变量：
```java
private LinearLayoutManager mLayoutManager;
RecyclerView mRecyclerView;
private PagerSnapHelper mPagerSnapHelper;
```
明眼人一看就知道了，ViewPager2的核心实现就是**RecyclerView+LinearLayoutManager+PagerSnapHelper**了，因为LinearLayoutManager本身就支持竖向和横向两种布局方式，所以ViewPager2也能很容易地支持这两种滚动方向了，而几乎不需要添加任何多余的代码。

使用上和老的ViewPager基本没啥却别，下面ViewPager2的几个新东西：
>* 支持RTL布局
>* 支持竖向滚动
>* 完整支持notifyDataSetChanged
>* FragmentStateAdapter替换了原来的 FragmentStatePagerAdapter
>* RecyclerView.Adapter替换了原来的 PagerAdapter
>* registerOnPageChangeCallback替换了原来的 addPageChangeListener (刚开始使用没看完源码，按照之前的set或者add找了半天，囧)
>* public void setUserInputEnabled(boolean enabled)禁止滑动
>* ...

关于更多ViewPager2的资料大家可以自行搜索。

### 2、视频播放器

本demo中使用的是[GSYVideoPlayer](https://github.com/CarGuo/GSYVideoPlayer)，实际项目中可自行选择封装。demo中没有对其进行过多的处理，只是为了看效果，实际的抖音中有更多复杂的东西。

### 3、demo结构

![](https://github.com/leiyun1993/DouYinLike/raw/master/screenshot/5.png)

看最后实现的效果
| 视频切换 | 点赞和视频控制 |
| ------ | ------ |
| ![](https://github.com/leiyun1993/DouYinLike/raw/master/screenshot/3.gif) | ![](https://github.com/leiyun1993/DouYinLike/raw/master/screenshot/4.gif) |

### 4、无限上滑

借助ViewPager2的监听和adapter的notifyDataSetChanged即可实现
```kotlin
viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
	override fun onPageSelected(position: Int) {
		super.onPageSelected(position)
		if (position==urlList.size-1){
			urlList.add(urlList[0])
			mPagerAdapter.notifyDataSetChanged()
		}
	}
})
```
### 5、视频和作者主页切换控制
```kotlin
override fun onResume() {
	super.onResume()
	if (mCurrentPosition > 0) {
		videoPlayer?.onVideoResume(false)
	} else {
		videoPlayer?.postDelayed({
			videoPlayer?.startPlayLogic()
		}, 200)
	}
}
override fun onPause() {
	super.onPause()
	likeLayout?.onPause()
	videoPlayer?.onVideoPause()
	mCurrentPosition = videoPlayer?.gsyVideoManager?.currentPosition ?: 0
}
```


### 6、点赞效果

抖音的点赞效果是由右侧的桃心点赞和屏幕的点击构成，右侧的点击后为点赞状态并有点赞动画，再次点击取消点赞。

先来看看效果：

![](https://github.com/leiyun1993/DouYinLike/raw/master/screenshot/2.gif)

此处我们观察效果基本和此前用过的一个三方库比较相似，此处就先又此代替，后面有时间再进行完善。
该库为[Like Button](https://github.com/jd-alexander/LikeButton),使用方式很简单，如下：
```xml
<com.github.like.LikeButton
    android:id="@+id/likeBtn"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentRight="true"
    android:layout_centerVertical="true"
    app:icon_size="40dp"
    app:icon_type="heart"
    app:like_drawable="@mipmap/ic_heart_on"
    app:unlike_drawable="@mipmap/ic_heart_off" />
```
**LikeButton**具有以下属性,使用时可自行去查看
```xml
<com.github.like.LikeButton
    app:icon_type="Star"
    app:circle_start_color="@color/colorPrimary"
    app:like_drawable="@drawable/thumb_on"
    app:unlike_drawable="@drawable/thumb_off"
    app:dots_primary_color="@color/colorAccent"
    app:dots_secondary_color="@color/colorPrimary"
    app:circle_end_color="@color/colorAccent"
    app:icon_size="25dp"
    app:liked="true"
    app:anim_scale_factor="2"
    app:is_enabled="false"/>
```
设置**LikeButton**的监听事件：
```kotlin
likeBtn.setOnLikeListener(object : OnLikeListener {
    override fun liked(p0: LikeButton?) {
        toast("已点赞~~")
    }

    override fun unLiked(p0: LikeButton?) {
        toast("取消点赞~~")
    }

})
```

我们可以看一下点赞按钮的点赞效果

![](https://github.com/leiyun1993/DouYinLike/raw/master/screenshot/1.gif)

和抖音的还是有点区别的，后续完善

### 7、屏幕点赞

分析抖音的屏幕点赞由几个部分组合而成（红心可自绘，也可以直接使用图片）
> 1、刚开始显示的时候，有个由大到小缩放动画

> 2、透明度变化

> 3、向上平移 

> 4、由小到大的缩放

> 5、刚开始显示的时候有个小小的偏移，避免每个红心在同一个位置

#### 8、实现过程
获取红心
```kotlin
private var icon: Drawable = resources.getDrawable(R.mipmap.ic_heart)
```
监听Touch事件，并在按下位置添加View
```kotlin
override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event?.action == MotionEvent.ACTION_DOWN) {     //按下时在Layout中生成红心
        val x = event.x
        val y = event.y
        addHeartView(x, y)
        onLikeListener()
    }
    return super.onTouchEvent(event)
}
```
为红心添加一个随机的偏移（此处为-10~10）
```kotlin
img.scaleType = ImageView.ScaleType.MATRIX
val matrix = Matrix()
matrix.postRotate(getRandomRotate())       //设置红心的微量偏移
```
设置一开始的缩放动画
```kotlin
private fun getShowAnimSet(view: ImageView): AnimatorSet {
    // 缩放动画
    val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f)
    val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f)
    val animSet = AnimatorSet()
    animSet.playTogether(scaleX, scaleY)
    animSet.duration = 100
    return animSet
}
```
设置慢慢消失时的动画
```kotlin
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
```
设置动画关系，并且在动画结束后Remove该红心
```kotlin
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
```
区分单击和多次点击，单击的时候控制视频的暂停和播放，多次点击的时候实现点赞功能
```kotlin
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
```



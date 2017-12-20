package com.yunlei.douyinlike

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.like.LikeButton
import com.like.OnLikeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val likeLayout = findViewById<LikeLayout>(R.id.likeLayout)
        val likeBtn = findViewById<LikeButton>(R.id.likeBtn)

        likeLayout.onLikeListener = {
            if (!likeBtn.isLiked) {
                likeBtn.isLiked = true
            }
        }
        likeBtn.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                toast("已点赞~~")
            }

            override fun unLiked(p0: LikeButton?) {
                toast("取消点赞~~")
            }

        })

    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

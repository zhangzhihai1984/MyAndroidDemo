package com.usher.demo

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import com.usher.demo.base.BaseActivity
import com.usher.demo.main.DemoListActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity(R.layout.activity_splash) {
    companion object {
        private const val DURATION = 600L
    }

    private val mTranslateSet by lazy {
        val translateAnimator by lazy {
            ValueAnimator.ofFloat(-SystemUtil.dip2px(this@SplashActivity, 300f).toFloat(), 0f).apply {
                duration = DURATION
                interpolator = OvershootInterpolator()
                addUpdateListener {
                    icon_imageview.translationY = animatedValue as Float
                }
            }
        }

        val alphaAnimator by lazy {
            ValueAnimator.ofFloat(0f, 1.0f).apply {
                duration = DURATION
                interpolator = LinearInterpolator()
                addUpdateListener {
                    icon_imageview.alpha = animatedValue as Float
                }
            }
        }

        AnimatorSet().apply {
            playTogether(translateAnimator, alphaAnimator)
        }
    }

    private val mScaleSet by lazy {
        val alphaAnimator by lazy {
            ValueAnimator.ofFloat(1.0f, 0f).apply {
                duration = DURATION
                interpolator = AccelerateInterpolator()
                addUpdateListener {
                    icon_imageview.alpha = animatedValue as Float
                }
            }
        }

        AnimatorSet().apply {
            startDelay = DURATION
            playTogether(alphaAnimator)
        }
    }

    private val mSplashSet by lazy {
        AnimatorSet().apply {
            playSequentially(mTranslateSet, mScaleSet)
            doOnStart { icon_imageview.visibility = View.VISIBLE }
            doOnEnd {
                startActivity(Intent(this@SplashActivity, DemoListActivity::class.java))
                finish()
            }
        }
    }

    override fun initView() {
        Observable.timer(DURATION, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    mSplashSet.start()
                }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = true
}
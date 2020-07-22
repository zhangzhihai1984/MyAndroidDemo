package com.twigcodes.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.twigcodes.ui.util.RxUtil
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseLazyPagerFragment(private var layoutResId: Int) : Fragment() {
    private val mResumeSubject = PublishSubject.create<Unit>()
    private var initializd = false

    init {
        mResumeSubject.take(1)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe {
                    initializd = true
                    init()
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(layoutResId, container, false)

    override fun onResume() {
        super.onResume()
        mResumeSubject.onNext(Unit)
    }

    /**
     * 如果在init中注册了广播需要在onDestroy中注销掉.
     * 但是由于是"懒加载", 在页面退出的时候其他Fragment可能并没有展现, 也就是说没有进行init, 这时会引发异常:
     * java.lang.IllegalArgumentException: Receiver not registered
     *
     * 这里给出的解决方案是, 索性不让用户"接触"onDestroy, 基类在onDestroy时调用一个带Boolean参数的onDestroy.
     * 用户可以重写这个方法, 根据initialized结合具体的业务逻辑进行处理.
     */
    final override fun onDestroy() {
        super.onDestroy()
        onDestroy(initializd)
    }

    open fun onDestroy(initialized: Boolean) {}

    abstract fun init()
}
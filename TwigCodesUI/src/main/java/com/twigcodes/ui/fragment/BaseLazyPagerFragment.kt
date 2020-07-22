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

    init {
        mResumeSubject.take(1)
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe { init() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(layoutResId, container, false)

    override fun onResume() {
        super.onResume()
        mResumeSubject.onNext(Unit)
    }

    abstract fun init()
}
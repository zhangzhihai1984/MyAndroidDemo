package com.twigcodes.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BasePagerFragment(private var layoutRes: Int) : Fragment() {
    private var mFragmentView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentView?.run {
            parent?.let {
                (it as ViewGroup).removeView(this)
            }
        } ?: run {
            mFragmentView = inflater.inflate(layoutRes, container, false)
        }

        return mFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = init()

    abstract fun init()
}
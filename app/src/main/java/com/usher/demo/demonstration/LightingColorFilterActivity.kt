package com.usher.demo.demonstration

import android.graphics.Color
import android.graphics.LightingColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.jakewharton.rxbinding4.viewpager.pageSelections
import com.twigcodes.ui.fragment.BasePagerFragment
import com.twigcodes.ui.util.RxUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import io.reactivex.rxjava3.kotlin.Observables
import kotlinx.android.synthetic.main.activity_colorfilter_lighting.*
import kotlinx.android.synthetic.main.fragment_colorfilter_lighting.*

class LightingColorFilterActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colorfilter_lighting)
        initView()
    }

    private fun initView() {
        val resIds = listOf(R.drawable.picasso_girl_with_mandolin, R.drawable.picasso_portrait_of_dora_maar, R.drawable.picasso_dora_maar_au_chat)
        val adapter = ColorFilterFragmentAdapter(supportFragmentManager, resIds)

        viewpager.adapter = adapter
        viewpager.pageSelections()
                .to(RxUtil.autoDispose(this))
                .subscribe { position ->
                    original_imageview.setImageResource(resIds[position])
                }

        indicatorview.setViewPager(viewpager)

        Observables.combineLatest(mul_color_seeker_view.colorSeeks(), add_color_seeker_view.colorSeeks()) { mul, add ->
            adapter.updateColorFilter(mul, add)
        }
                .compose(RxUtil.getSchedulerComposer())
                .to(RxUtil.autoDispose(this))
                .subscribe()
    }

    private class ColorFilterFragmentAdapter(fm: FragmentManager, private val resIds: List<Int>, private var mul: Int = Color.WHITE, private var add: Int = Color.BLACK) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = ColorFilterFragment.newInstance(resIds[position], mul, add)

        override fun getCount(): Int = resIds.size

        override fun getItemPosition(obj: Any): Int = PagerAdapter.POSITION_NONE

        fun updateColorFilter(mul: Int, add: Int) {
            this.mul = mul
            this.add = add
            notifyDataSetChanged()
        }
    }

    class ColorFilterFragment : BasePagerFragment(R.layout.fragment_colorfilter_lighting) {
        companion object {
            private const val RESID = "RESID"
            private const val MUL = "MUL"
            private const val ADD = "ADD"

            fun newInstance(resId: Int, mul: Int, add: Int) =
                    ColorFilterFragment().apply {
                        arguments = Bundle().apply {
                            putInt(RESID, resId)
                            putInt(MUL, mul)
                            putInt(ADD, add)
                        }
                    }
        }

        override fun init() {
            arguments?.run {
                val resId = getInt(RESID)
                val mul = getInt(MUL)
                val add = getInt(ADD)

                colorfilter_imageview.setImageResource(resId)
                colorfilter_imageview.colorFilter = LightingColorFilter(mul, add)
            }
        }

    }
}
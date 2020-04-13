package com.usher.demo.view.pager

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.twigcodes.ui.fragment.BasePagerFragment
import com.usher.demo.R
import com.usher.demo.utils.Constants
import kotlinx.android.synthetic.main.fragment_pager.*
import java.util.*

class PagerFragment(layoutRes: Int) : BasePagerFragment(layoutRes) {
    companion object {
        fun newInstance(num: Int): PagerFragment =
                PagerFragment(R.layout.fragment_pager).apply {
                    arguments = Bundle().apply {
                        putInt(Constants.TAG_DATA, num)
                    }
                }
    }

    override fun init() {
        arguments?.run {
            textview.text = "${getInt(Constants.TAG_DATA)}"
        }

        val color = Color.argb(255, Random().nextInt(256), Random().nextInt(256), Random().nextInt(256))

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
//        hsv[1] = hsv[1] + 0.2f
        hsv[2] = hsv[2] - 0.2f
        val dark = Color.HSVToColor(hsv)

//        view1.setBackgroundColor(dark)
//        view2.setBackgroundColor(color)
        root_layout.setBackgroundColor(color)

        Color.colorToHSV(Color.RED, hsv)
        Log.i("zzh", "red: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
        Color.colorToHSV(Color.argb(128, 255, 0, 0), hsv)
        Log.i("zzh", "red: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
        Color.colorToHSV(Color.GREEN, hsv)
        Log.i("zzh", "green: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
        Color.colorToHSV(Color.BLUE, hsv)
        Log.i("zzh", "blue: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
        Color.colorToHSV(Color.WHITE, hsv)
        Log.i("zzh", "white: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
        Color.colorToHSV(Color.BLACK, hsv)
        Log.i("zzh", "black: ${hsv[0]} ${hsv[1]} ${hsv[2]}")
    }
}
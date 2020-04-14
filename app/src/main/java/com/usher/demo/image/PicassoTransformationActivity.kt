package com.usher.demo.image

import android.os.Bundle
import com.squareup.picasso.Picasso
import com.twigcodes.ui.util.PicassoUtil.getCircleTransformation
import com.twigcodes.ui.util.PicassoUtil.getRoundTransformation
import com.twigcodes.ui.util.PicassoUtil.getSquareTransformation
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_picasso_transformation.*

class PicassoTransformationActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picasso_transformation)
        initView()
    }

    private fun initView() {
        Picasso.get().load(R.drawable.demo_hardworking).transform(getSquareTransformation()).into(square_imageview)
        Picasso.get().load(R.drawable.demo_hardworking).transform(getCircleTransformation()).into(circle_imageview)
        Picasso.get().load(R.drawable.demo_hardworking).transform(getRoundTransformation()).into(round_imageview)
    }
}
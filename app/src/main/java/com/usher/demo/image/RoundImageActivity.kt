package com.usher.demo.image

import android.os.Bundle
import com.squareup.picasso.Picasso
import com.twigcodes.ui.util.PicassoUtil.getCircleTransformation
import com.twigcodes.ui.util.PicassoUtil.getRoundTransformation
import com.twigcodes.ui.util.PicassoUtil.getSquareTransformation
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_round_image.*

class RoundImageActivity : BaseActivity(Theme.LIGHT) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_round_image)
        initView()
    }

    private fun initView() {
        Picasso.get().load(R.mipmap.homework).transform(getSquareTransformation()).into(square_imageview)
        Picasso.get().load(R.mipmap.homework).transform(getCircleTransformation()).into(circle_imageview)
        Picasso.get().load(R.mipmap.homework).transform(getRoundTransformation()).into(round_imageview)
    }
}
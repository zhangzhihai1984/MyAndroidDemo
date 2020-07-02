package com.usher.demo.image

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.twigcodes.ui.adapter.RxBaseQuickAdapter
import com.twigcodes.ui.util.ImageUtil
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_picasso_transformation.*

class PicassoTransformationActivity : BaseActivity(Theme.LIGHT_AUTO) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picasso_transformation)
        initView()
    }

    private fun initView() {
        val transformations = listOf(
                null,
                ImageUtil.getBlurTransformation(this),
                ImageUtil.getSquareTransformation(),
                ImageUtil.getCircleTransformation(),
                ImageUtil.getRoundTransformation(200f)
        )

        recyclerview.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        recyclerview.adapter = PicassoAdapter(transformations)
    }

    private class PicassoAdapter(data: List<Transformation?>) : RxBaseQuickAdapter<Transformation?, BaseViewHolder>(R.layout.item_picasso_transformation, data) {
        override fun convert(helper: BaseViewHolder, transformation: Transformation?) {
            Picasso.get().load(R.drawable.demo_hardworking)
                    .transform(transformation ?: object : Transformation {
                        override fun key(): String = "NoBlurTransformation"

                        override fun transform(source: Bitmap): Bitmap = source
                    }).into(helper.getView<ImageView>(R.id.picasso_imageview))
        }
    }
}
package com.twigcodes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.twigcodes.ui.util.RxUtil
import com.twigcodes.ui.util.SystemUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.zipcode_layout.view.*
import kotlin.math.min

/**
 * 初始zipcode值设为"6个空格", 为了方便解释, 下面以"ABCDEF"为例.
 * 控件有6个"盒子", 用于显示zipcode. 后面隐藏一个EditText, 用于编辑zipcode.
 * 点击某个盒子后该盒子高亮, 唤起输入法, 输入后, 右侧的盒子高亮, 直至最后一个盒子, 如果是删除的话, 左侧的盒子高亮,
 * 直至第一个盒子.
 *
 * 以点击第二个盒子为例, 此时为"ABCDEF", 盒子的index为1, 我需要将光标移至index+1处, 输入3, 这时候EditText的值为
 * "AB3CDEF", 此时它的长度变为7, 与zipcode的长度6不符, 因此需要处理一下:
 * 我们需要保留的是index前面的"A"以及index+1后面的"3DEFG",也就是
 * text.substring(0, index) + text.substring(index + 1, 7)
 * 关于后面的"7"需要说一下, 用户输入的不一定是一个字母或数字, 由于输入法的原因, 可能一次性输入的是一个单词, 这样的话,
 * 我们需要保证最终截取的字符串的长度为6, 这个处理后的字符串就是我们需要的zipcode.
 *
 * 这时我们需要做几件事情:
 * 1. 将EditText的text设为处理后的zipcode, 由于处理后的zipcode长度为6, 因此不会引发zipcode的再次处理.
 * 2. 将zipcode填充至6个盒子中
 * 3. 取消所有盒子高亮.
 * 2. 如果当前为最后一个盒子, 即index为5, 收起键盘, 否则将index的值加1, 让下一个盒子高亮.
 * 3. 由于对EditText的text进行了设置, 因此需要重新将光标移至index+1处.
 */
class ZipCodeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val mZipCodeSubject = BehaviorSubject.create<String>()
    private val mImm by lazy { context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    private val mBoxViews = arrayListOf<TextView>()

    private var mZipCode = "      "

    //    private var mZipCode = "000000"
    private var mCurrentBoxIndex = 0

    init {
        inflate(context, R.layout.zipcode_layout, this)

        IntRange(0, 5).forEach { index ->
            val view = LayoutInflater.from(context).inflate(R.layout.zipcode_box_layout, this, false) as TextView
            val margin = when (index) {
                0 -> 0
                4 -> SystemUtil.dip2px(context, 30f)
                else -> SystemUtil.dip2px(context, 10f)
            }
            zicode_container.addView(view, LinearLayout.LayoutParams(view.layoutParams).apply { marginStart = margin })
            mBoxViews.add(view)
        }

        mBoxViews.forEachIndexed { index, view ->
            view.clicks()
                    .compose(RxUtil.singleClick())
                    .to(RxUtil.autoDispose(context as LifecycleOwner))
                    .subscribe {
                        mCurrentBoxIndex = index

                        /**
                         * EditText获取焦点, 将光标移至index+1, 同时唤起输入法.
                         */
                        zipcode_edittext.requestFocus()
                        zipcode_edittext.setSelection(mCurrentBoxIndex + 1)
                        mImm.showSoftInput(zipcode_edittext, 0)

                        /**
                         * 取消所有盒子高亮, 将当前盒子高亮.
                         */
                        mBoxViews.forEach { view -> view.background = ContextCompat.getDrawable(context, R.drawable.zipcode_default_background) }
                        view.background = ContextCompat.getDrawable(context, R.drawable.zipcode_selected_background)
                    }
        }

        zipcode_edittext.textChanges()
                .skipInitialValue()
                .filter { it.length != 6 }
                .to(RxUtil.autoDispose(context as LifecycleOwner))
                .subscribe {
                    update(it.toString())
//                    mZipCodeSubject.onNext(it.toString())
                }

        zipcode_edittext.setText(mZipCode)
        zipcode_edittext.setSelection(0)
    }

    fun update(zipCode: String) {

        /**
         * 取消所有盒子高亮
         */
        mBoxViews.forEach { view -> view.background = ContextCompat.getDrawable(context, R.drawable.zipcode_default_background) }

        if (zipCode.length > 6) {
            mZipCode = zipCode.substring(0, mCurrentBoxIndex) + zipCode.substring(mCurrentBoxIndex + 1, 7)

            /**
             * 更新EditText的text
             */
            zipcode_edittext.setText(mZipCode)

            /**
             * 将zipcode填充至6个盒子中
             */
            mBoxViews.zip(mZipCode.toList()).forEach { it.first.text = "${it.second}" }

            /**
             * 将index的值加1, 让下一个盒子高亮.
             * 如果index为6, 说明最后一个盒子已经输入结束, 此时收起键盘.
             * 为了防止在某种极端情况下用户可以继续输入, 我们将index的最大值固定在6, 光标也放在6的位置上, 这样即使继续
             * 输入也不会引发数组越界, 同时也不会有什么效果.
             */
            mCurrentBoxIndex = min(++mCurrentBoxIndex, mBoxViews.size)

            if (mCurrentBoxIndex < mBoxViews.size) {
                mBoxViews[mCurrentBoxIndex].background = ContextCompat.getDrawable(context, R.drawable.zipcode_selected_background)
                zipcode_edittext.setSelection(mCurrentBoxIndex + 1)
            } else {
                zipcode_edittext.setSelection(mBoxViews.size)
                mImm.hideSoftInputFromWindow(zipcode_edittext.windowToken, 0)
            }
        }
    }

    fun zipCodeChanges(): Observable<String> = mZipCodeSubject
}
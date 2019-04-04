package com.usher.kotlin

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar2)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            doSth()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.i("zzh", "")

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initFun(i: Int): Int {
        return i * 2
    }

    var text: String? = null

    private fun doSth() {
        val s = "Hello"
        val ss = """
            s is $s
        """.trim()
        var a = ""
        a += "World"
//        val array : Int[] = [1, 2]
        var sbs = ArrayList<Int>()
//        sbs.forEach(i -> {})

        var length: Int = text?.length ?: -1
        text?.let {
            Log.i(TAG, "")
        }

        val arr: ArrayList<Int> = arrayListOf(1, 2, 3)
        val arr2 = Array(3) { i -> (i * 2) }
        val arr3 = Array(3) { i -> initFun(i) }

        for (value in arr3) {

        }

        for (i in arr3.indices) {

        }

        for ((i, value) in arr3.withIndex()) {

        }

        arr3.forEach(fun(value: Int) {
            Log.i(TAG, value.toString())
        })

        arr3.forEach {
            Log.i(TAG, it.toString())
        }

        val whenCondition = 4;
        when (whenCondition) {
            1 -> Log.i(TAG, "")
            2 -> {
                Log.i(TAG, "")
            }
            else -> Log.i(TAG, "DEFAULT")
        }
        return
    }

    val hello = ""
}

const val TAG = ""
val FUN: (Int, Int) -> Int = { x, y ->
    x + y
}
//var FUN2 = (a: Int, b: Int) -> a+b

val modified: String
    get() {
        return ""
    }

val modified2
    get() = "Hello"
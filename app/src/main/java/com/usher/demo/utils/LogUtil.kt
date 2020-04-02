package com.usher.demo.utils

import android.util.Log
import io.reactivex.subjects.PublishSubject
import java.io.BufferedReader
import java.io.InputStream
import kotlin.concurrent.thread

object LogUtil {
    private var flag = false
    private var reader: BufferedReader? = null
    private var `in`: InputStream? = null
    val logSubject = PublishSubject.create<String>()

    private fun startLog() = thread {
        Runtime.getRuntime().exec("logcat -c")
        `in` = Runtime.getRuntime().exec("logcat -v long").inputStream
        reader = `in`?.bufferedReader()

        try {
            reader?.run {
                while (true) {
                    readLine()?.let { log ->
                        logSubject.onNext(log)
                    } ?: Log.i("zzh", "LOG END")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("zzh", "LOG ERROR: ${e.message}")
        }
    }

    fun startLogIfNeeded() {
        if (flag.not()) {
            flag = true
            startLog()
        } else {
            Log.i("zzh", "Log Has Been Started")
        }
    }

    fun stopLog() {
//        reader?.close()
//        `in`?.close()
    }
}
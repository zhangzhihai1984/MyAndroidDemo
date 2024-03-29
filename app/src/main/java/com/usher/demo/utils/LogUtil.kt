package com.usher.demo.utils

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.BufferedReader
import java.io.InputStream
import kotlin.concurrent.thread

object LogUtil {
    private const val TAG = "zzh"
    private var readLogFlag = false
    private var reader: BufferedReader? = null
    private var `in`: InputStream? = null
    private val readLogSubject = PublishSubject.create<String>()

    fun log(log: String) {
        Log.i(TAG, log)
    }

    private fun startReadLog() = thread {
        Runtime.getRuntime().exec("logcat -c")
        `in` = Runtime.getRuntime().exec("logcat -v long").inputStream
        reader = `in`?.bufferedReader()

        try {
            reader?.run {
                while (true) {
                    readLine()?.let { log ->
                        readLogSubject.onNext(log)
                    } ?: log("read log END")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            readLogFlag = false
            log("read log error: ${e.message}")
        }
    }

    fun readLog(): Observable<String> {
        if (readLogFlag.not()) {
            readLogFlag = true
            startReadLog()
        }
        return readLogSubject
    }

//    fun stopLog() {
//        reader?.close()
//        `in`?.close()
//    }
}
package com.usher.demo.kotlin

import android.os.Bundle
import android.util.Log
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

class KotlinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        init()
    }

    private fun init() {
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
//                .`as`<ObservableSubscribeProxy<Long>>(RxUtil.autoDispose(this))
                .`as`(RxUtil.autoDispose(this))
                .subscribe { v ->
                    Log.i("zzh", "haha")
                }
    }

    data class Person(val name: String, val age: Int)

    private fun lambda() {
        val people = listOf(Person("A", 10), Person("B", 12))

        val p1 = Observable.just(Person("A", 10))
        val p2 = Observable.just(Person("B", 12))

        //
        var max1 = people.maxBy({ it.age })
        Observables.combineLatest(p1, p2, { a, b -> a.age + b.age })
                .`as`(RxUtil.autoDispose(this))
                .subscribe {}

        //如果lambda是函数的最后一个实参, 它可以放到括号的外面
        var max2 = people.maxBy() { it.age }
        Observables.combineLatest(p1, p2) { a, b -> a.age + b.age }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {}


        //当lambda是函数的唯一实参, 可以去掉括号
        var max3 = people.maxBy { it.age }



        people.mapIndexed { index, person -> "${index + person.age}" }


        fun Person.isAdult() = age >= 18

        val predicate = Person::isAdult
        val predicate2: (p: Person) -> Boolean = { it.isAdult() }

        val func3Value = func3 { x, y -> x + y }

    }

    val valFunc1: (Int, Int) -> Int = { x, y -> x + y }
    val valFunc2 = { x: Int, y: Int -> x + y }

    fun func1(x: Int, y: Int): Int = x + y
    fun func2(x: Int, y: Int): Int {
        return x + y
    }

    private fun func3(f: ((Int, Int) -> Int)): Int {
        return f(1, 2)
    }

}
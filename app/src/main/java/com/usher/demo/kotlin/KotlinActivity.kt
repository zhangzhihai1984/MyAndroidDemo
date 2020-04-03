package com.usher.demo.kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import com.usher.demo.R
import com.usher.demo.base.BaseActivity
import com.usher.demo.utils.RxUtil
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_kotlin.*
import java.util.concurrent.TimeUnit

class KotlinActivity : BaseActivity() {
    companion object Constants {
        const val name = "HAHA"

        val func21 = { x: Int, y: Int -> x + y }

        fun func31(x: Int, y: Int): Int {
            return x + y
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

//        init()
        lambda()
    }

    private fun init() {
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(RxUtil.getSchedulerComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe { v ->
                    Log.i("zzh", "haha")
                }
    }

    data class Person(var name: String, val age: Int)

    //    class Generic<T>(private val key: T) {
//        fun a2(): T = key
//        fun getKey2(): T = key
//    }
//
    class View2 {
        interface Click {
            fun onClick(tag: Long): String
        }

        fun setClick1(c: Click) = c.onClick(System.currentTimeMillis())
        fun setClick2(c: (tag: Long) -> String): String = c(System.currentTimeMillis())
    }

    interface IGeneric<T> {
        fun next(): T?
    }

    class GenericImpl<T>(private val t: T) : IGeneric<T> {
        override fun next(): T = t
    }

    private fun <T> genT(t: T): GenericImpl<T> = GenericImpl(t)

    private fun <T, V> genV(t: T, v: V): GenericImpl<V> = GenericImpl(v)


    private fun <T> printMsg(vararg args: T, pre: String = "arg") = args.forEach { Log.i("zzh", "$pre: $it") }

    class Animal

    abstract class Fruit {
        abstract val desc: String
    }

    open class Apple(override val desc: String = "Apple") : Fruit()
    class RedApple(override val desc: String = "Red Apple") : Apple(desc)
    class Banana(override val desc: String = "Banana") : Fruit()

    class Generic2<T> {
        fun add(t: T) {}
        fun addG(g: Generic2<out T>) {} //<? extends T>
        fun addG2(g: Generic2<in T>) {} //<? super T>
    }

    interface IFruit<T> {
        fun next(): T?
    }

    private fun <T : Fruit> printFruit(fruit: T) = Log.i("zzh", "Fruit: ${fruit.desc}")

    private fun getProducerFruits(): ArrayList<out Fruit> = arrayListOf<Apple>().apply { add(Apple()) }

    private fun getConsumerFruits(): ArrayList<in Apple> = arrayListOf<Apple>().apply { add(Apple()) }

    private fun cutFruits(fruits: ArrayList<out Fruit>) {}

    open class BaseEntity(val error: String?)
    class ResultEntity<T>(val t: T?, error: String?) : BaseEntity(error)

    private fun <T> getBaseComposer(): ObservableTransformer<T, BaseEntity> =
            ObservableTransformer { upstream ->
                upstream.map { resp -> ResultEntity<T>(resp, null) }
//                        .onErrorReturn { err -> ResultEntity(null, null) }
            }

    private fun lambda() {
        Observable.just(Apple())
                .onErrorReturn { RedApple() }
//                .compose(getBaseComposer())
                .`as`(RxUtil.autoDispose(this))
                .subscribe {  }


        val g = Generic2<Apple>()
        g.add(Apple())
        g.add(RedApple())
        g.addG(Generic2<RedApple>())
        g.addG2(Generic2<Fruit>())
        printMsg("a", "b", "c", "d")
        val array = arrayOf("a", "b", "c", "d")
        printMsg(*array)
        printMsg(pre = "Hi", args = *array)
        printMsg(1, 2, 3, 4, 5)

        printFruit(Apple())
        printFruit((Banana()))

        val producerFruits: ArrayList<out Fruit> = getProducerFruits()
        val producerFruit = producerFruits[0]
//        producerFruits.add(Apple()) //Required: nothing

        val consumerFruits: ArrayList<in Apple> = getConsumerFruits()
//        val consumerFruit: Apple = consumerFruits[0] //Found: Any
        consumerFruits.add(RedApple())

        cutFruits(arrayListOf<Apple>())

        val view2 = View2()
        view2.setClick1(object : View2.Click {
            override fun onClick(tag: Long): String = "$tag"

        })

        view2.setClick2 { tag -> "$tag" }

        val genTStr = genT("Hello").next()
        val genVStr = genV("Hello", 1).next()

        val list = emptyList<Int>()
        val result = list.isEmpty()

        val p = Person("A", 10).apply { name = "B" }
        Log.i("zzh", "name: " + p.name)

        val pp = Person("A", 10).also { it.name = "B" }
        Log.i("zzh", "name2: " + pp.name)

        val people = listOf(Person("A", 10), Person("B", 12))

        val p1 = Observable.just(Person("A", 10))
        val p2 = Observable.just(Person("B", 12))

        //
        var max1 = people.maxBy({ it.age })
        Observables.combineLatest(p1, p2, { a, b -> a.age + b.age })
                .`as`(RxUtil.autoDispose(this))
                .subscribe {}

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
            }
        })
        button.setOnClickListener(View.OnClickListener { })
        button.setOnClickListener({})

        //如果lambda是函数的最后一个实参, 它可以放到括号的外面
        var max2 = people.maxBy() { it.age }
        Observables.combineLatest(p1, p2) { a, b -> a.age + b.age }
                .`as`(RxUtil.autoDispose(this))
                .subscribe {}

        button.setOnClickListener() {}

        //当lambda是函数的唯一实参, 可以去掉括号
        var max3 = people.maxBy { it.age }

        button.setOnClickListener { }


        people.mapIndexed { index, person -> "${index + person.age}" }


        fun Person.isAdult() = age >= 18

        val predicate = Person::isAdult
        val predicate2: (p: Person) -> Boolean = { it.isAdult() }

        val func3Value = func3 { x, y -> x + y }

        val student = object {
            var name: String = "a"
            var age = 10
        }

        val test = Test()
        test.setInterface(object : TestInterface {
            override fun test2() {
            }

            override fun test() {
            }
        })

        test.setAB(object : ABTest() {
            override fun test() {
            }
        })


        with(Person("A", 10)) {
            val n = name
            val a = age
        }

        Observable.just(1)
                .withLatestFrom(Observable.just(2), BiFunction<Int, Int, Int> { t1, t2 -> t1 + t2 })
                .`as`(RxUtil.autoDispose<Int>(this))
                .subscribe()

        val a = arrayOf(arrayOf(1, 2), arrayOf(3, 4)).flatMap { it.asIterable() }
//
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

    interface TestInterface {
        fun test()

        fun test2()
    }

    open class Test {
        fun setInterface(t: TestInterface) {
            t.test()
        }

        fun setAB(a: ABTest) {
            a.test()
        }
    }

    class Test2 constructor(value: Int = 1) : Test() {
        constructor(s: String) : this(2) {
            val s1 = s
        }

        init {
            val v = value + 1
        }
    }

    class Test3 private constructor() {
        constructor(s: String) : this() {

        }
    }

    abstract class ABTest {

        abstract fun test()
    }

    class FiledClass() {
        var name: String = "AAA"
            set(value) {
                if (value.isNotEmpty())
                    field = value
            }
    }
}
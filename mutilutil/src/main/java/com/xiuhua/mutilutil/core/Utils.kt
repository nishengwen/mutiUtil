package com.xiuhua.mutilutil.core

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.net.URI
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.text.StringBuilder

//核心的工具类
fun String?.toast(context: Context) {
    this.apply {
        if (!TextUtils.isEmpty(this)) {
            Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
        }
    }
}

var data: String by Delegates.notNullSingle()


//var androidId: String = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

/**
 * 防止fragment重复添加的问题
 */
fun <T : Fragment> FragmentManager.addOrShow(fragmentTag: String, newFragment: T, resId: Int = -1): T {
    val transaction = beginTransaction();
    val fragment: Fragment? = findFragmentByTag(fragmentTag)
    if (fragment != null) {
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
        return fragment as T
    } else {
        if (resId != -1) {
            transaction.add(resId, newFragment, fragmentTag)
        } else {
            transaction.add(newFragment, fragmentTag)
        }
        transaction.commitAllowingStateLoss()
        return newFragment
    }
}


//一些常用的lambda
typealias  OutTAction<T> = T.() -> Unit
typealias  OutData<T> = (T) -> Unit
typealias  OutAction = () -> Unit
typealias  Filter<T> = (T) -> Boolean

//测试用数据
fun getSimpleTextList(size: Int): MutableList<NotifyText> {
    return MutableList(size) { index ->
        NotifyText("${index}-${index}-${index}-${index}-${index}-${index}-${index}-${index}")
    }
}


//
///**
// * 全局的第一个路由 由于反射机制的原因会耗时20ms
// * 1、首次出现的一条路由，会耗时0.1~0.4ms
// * 2、重复的路由获取,没有耗时就是读取字符串 0.008ms
// */
//abstract class Page{
//    //缓存
//    private var uri:String=""
//    private var path:String=""
//    private fun getPath(){
//        val clzs = javaClass.name.split("$")
//        val clzName=StringBuilder()
//        val result=StringBuilder()
//        val head=StringBuilder()
//        for((index,clz) in clzs.withIndex()){
//            if (index==0){
//                clzName.append(clz)
//                val content=Class.forName(clzName.toString()).getAnnotation(RouteDesc::class.java)?.content
//                head.append(content)
//            }else{
//                clzName.append("$$clz")
//                val content=Class.forName(clzName.toString()).getAnnotation(RouteDesc::class.java)?.content
//                result.append(content)
//            }
//        }
//        uri="{$head$result}"
//        path= "{$result}"
//    }
//
//    val URI:String
//        get() {
//            if(uri!=""){
//                return uri
//            }
//            val startTime=System.nanoTime()
//            getPath()
//            println((System.nanoTime()-startTime))
//            return uri
//        }
//
//    val PATH:String
//        get() {
//            if(path!=""){
//                return path
//            }
//            //10ms
//            val startTime=System.nanoTime()
//            getPath()
//            println((System.nanoTime()-startTime))
//            return path
//        }
//}
//
//@RouteDesc("rrc:/")
//class RRC {
//    //im分组
//    @RouteDesc("/im")
//    class IM {
//        //私聊页
//        @RouteDesc("/private")
//        object PRIVATE : Page()
//
//        //直播页
//        @RouteDesc("/live")
//        object LIVE : Page()
//
//        //直播页
//        @RouteDesc("/set")
//        object SET : Page()
//
//        //直播页
//        @RouteDesc("/get")
//        object GET : Page()
//    }
//    //im分组
//    @RouteDesc("/live")
//    class LIVE {
//        //私聊页
//        @RouteDesc("/private")
//        object PRIVATE : Page()
//
//        //直播页
//        @RouteDesc("/live")
//        object LIVE : Page()
//
//        //直播页
//        @RouteDesc("/set")
//        object SET : Page()
//
//        //直播页
//        @RouteDesc("/get")
//        object GET : Page()
//    }
//}

fun main(args: Array<String>) {
//   val uri= URI.create("scheme://user:pass@www.example.com:80/home/index.html?age=11&name=22#fragment")
   val uri= URI.create("rrc://webview/home/index.html?age=11&name=22#fragment")
    println("uri:${uri}")
    println(uri.scheme)
    println(uri.authority)
    println(uri.host)
    println(uri.port)
    println(uri.path)
    println(uri.query)
    println(uri.rawQuery)
    println(uri.fragment)
//
//    val PRIVATE = RRC.IM.PRIVATE.URI
//    val PRIVATE2 = RRC.IM.SET.URI
//    val PRIVATE3 = RRC.IM.GET.URI
//    val PRIVATE4 = RRC.IM.LIVE.URI
//
//    val startTime=System.nanoTime()
//    val PRIVATE5 = RRC.IM.PRIVATE.URI
//    val PRIVATE6 = RRC.IM.SET.URI
//    val PRIVATE7 = RRC.IM.GET.URI
//    val PRIVATE8 = RRC.IM.LIVE.URI
//
//    val PRIVATE9 = RRC.IM.PRIVATE.PATH
//    val PRIVATE10 = RRC.IM.SET.PATH
//    val PRIVATE11 = RRC.IM.GET.PATH
//    val PRIVATE12 = RRC.IM.LIVE.PATH
//
//    println("4合1----"+(System.nanoTime()-startTime))
//    val LIVE1 = RRC.LIVE.PRIVATE.URI
//    val LIVE2 = RRC.LIVE.SET.URI
//    val LIVE3 = RRC.LIVE.GET.URI
//    val LIVE4 = RRC.LIVE.LIVE.URI
//
//    println(PRIVATE)
//

}

fun <T : Any> Delegates.notNullSingle(): ReadWriteProperty<Any?, T> = NotNullSingleVar()

private class NotNullSingleVar<T : Any>() : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value
        else throw IllegalStateException("Property ${property.name}  already initialized")
    }
}





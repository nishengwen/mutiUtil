package com.xiuhua.mutilutil.core
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.net.URI
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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
fun <T: Fragment> FragmentManager.addOrShow(fragmentTag:String, newFragment: T, resId: Int=-1): T{
    val transaction=beginTransaction();
    val fragment: Fragment?=findFragmentByTag(fragmentTag)
    if(fragment!=null){
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
        return fragment as T
    }else{
        if(resId!=-1){
            transaction.add(resId,newFragment,fragmentTag)
        }else{
            transaction.add(newFragment,fragmentTag)
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
fun getSimpleTextList(size: Int):MutableList<NotifyText>{
    return MutableList(size){
            index ->
        NotifyText("${index}-${index}-${index}-${index}-${index}-${index}-${index}-${index}")
    }
}


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
}


fun <T: Any> Delegates.notNullSingle(): ReadWriteProperty<Any?, T> = NotNullSingleVar()

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





package com.xiuhua.mutilutil.core.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.xiuhua.mutilutil.core.OutData

/**
 * 主线程的Handler
 */
class MainHandler : Handler(Looper.getMainLooper()) {
    private var messageAction: OutData<Message>? = null

    fun onReceive(messageAction: OutData<Message>) {
        this.messageAction = messageAction
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        messageAction?.invoke(msg)
    }
}

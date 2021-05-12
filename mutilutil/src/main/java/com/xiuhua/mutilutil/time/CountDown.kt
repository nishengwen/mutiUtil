package com.xiuhua.mutilutil.time

import android.os.Handler
import android.os.SystemClock
import com.xiuhua.mutilutil.core.thread.MainHandler
import com.xiuhua.mutilutil.core.OutAction
import com.xiuhua.mutilutil.core.OutData

/**
 * 返回多少整数秒的计时器
 *
 * @param intervalMillis Long 间隔多少毫秒
 * @return CountDownTimerImpl 生成计时器
 * @see  Long.tick
 */
infix fun Int.tick(intervalMillis: Long) = this * 1000L tick intervalMillis

/**
 * 返回多少Long毫秒的计时器
 *
 * @param intervalMillis Long 间隔多少毫秒
 * @return CountDownTimerImpl 生成计时器
 * @see  Int.tick
 */
infix fun Long.tick(intervalMillis: Long) = CountDownTimerImpl(this, intervalMillis)

private const val MSG = 1

/**
 * 通过 Handler 实现计时
 */
abstract class CountDownTimer(
    /**
     * Millis since epoch when alarm should stop.
     */
    private val mMillisInFuture: Long,
    /**
     * The interval in millis that the user receives callbacks
     */
    private val mCountdownInterval: Long
) {

    private var mStopTimeInFuture: Long = 0

    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false

    /**
     * Cancel the countdown.
     */
    @Synchronized
    fun cancel() {
        mCancelled = true
        mHandler.removeMessages(MSG)
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CountDownTimer {
        mCancelled = false
        if (mMillisInFuture <= 0) {
            onFinish()
            return this@CountDownTimer
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        return this@CountDownTimer
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    // handles counting down
    private val mHandler: Handler = MainHandler().apply {
        onReceive {
            synchronized(this@CountDownTimer) {
                if (mCancelled) {
                    return@onReceive
                }
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    onFinish()
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onTick(millisLeft)

                    // take into account user's onTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long
                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) delay = 0
                    } else {
                        delay = mCountdownInterval - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }
}


/**
 * 计时器实现类
 * @param mMillisInFuture Long 一共计时多少毫秒
 * @param mCountdownInterval Long 间隔多少毫秒
 * @see  CountDownTimer
 */
class CountDownTimerImpl(mMillisInFuture: Long, mCountdownInterval: Long) :
    CountDownTimer(mMillisInFuture, mCountdownInterval) {
    private var outData: OutData<Long>? = null
    private var outAction: OutAction? = null
    fun onTick(outData: OutData<Long>) {
        this.outData = outData
    }

    fun onFinish(outAction: OutAction) {
        this.outAction = outAction
    }

    override fun onTick(millisUntilFinished: Long) {
        outData?.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        outAction?.invoke()
    }
}


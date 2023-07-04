package dev.pattabiraman.utils.callback

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import dev.pattabiraman.utils.PluginAppConstant
import dev.pattabiraman.utils.PluginAppUtils
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException

class HandlerUtils {
    var handler = Handler(Looper.getMainLooper())
    fun handleAsyncOperation(
        activity: AppCompatActivity?, handleBackgroundThread: HandleBackgroundThread
    ) {
//        AppController.getInstance().traceLog("MAXIMUM_POOL_SIZE", "------------->" + MAXIMUM_POOL_SIZE);
        try {
            /*do in background*/
            executor!!.execute {
                handleBackgroundThread.handleDoInBackground()

                /*on post execute*/
                handler.post { handleBackgroundThread.handlePostExecute() }
            }
        } catch (e: RejectedExecutionException) {
            e.printStackTrace()
            //do nothing because thread is rejected as the activity is destroyed
            // (or) no longer accessible to throw callback to calling function
        } catch (e: Exception) {
            e.printStackTrace()
            val message = if (TextUtils.isEmpty(e.message)) "unknown exception" else e.message!!
            PluginAppUtils.getInstance(activity).showToast(activity, message)
            handleBackgroundThread.handleException(message)
        }
    }

    fun cancelPendingOperation() {
        if (executor != null && !executor!!.isShutdown) {
            executor!!.shutdown()
        }
    }

    companion object {
        private var mInstance: HandlerUtils? = null
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private var executor = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE)

        @JvmStatic
        val instance: HandlerUtils
            get() {
                if (executor == null) {
                    executor = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE)
                }
                if (executor!!.isShutdown) {
                    executor = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE)
                }
                return if (mInstance == null) HandlerUtils().also {
                    mInstance = it
                } else mInstance!!
            }
    }
}
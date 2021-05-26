package com.bita.smscontrol.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Handler
import android.provider.Telephony
import android.util.Log
import com.bita.smscontrol.activity.MainActivity
import com.bita.smscontrol.event.NewSms
import org.greenrobot.eventbus.EventBus


class SMSReceiver : BroadcastReceiver() {
    companion object {
        private val TAG by lazy { SMSReceiver::class.java.simpleName }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) return
        val extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (SmsMessage in extractMessages){
            val message =SmsMessage.displayMessageBody.trim()
            if (message.startsWith("on_time", true)){
                if(isForeground(context, "com.bita.smscontrol")){
                    EventBus.getDefault().post(
                        NewSms(
                            SmsMessage.displayOriginatingAddress,
                            message
                        )
                    )
                }else{
                    Log.e("SMSReceiver", "start main activity ........")
                    val i = Intent()
                    i.setClass(context!!,MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(i)

                    Handler().postDelayed(object : Runnable {
                        override fun run() {
                            EventBus.getDefault().post(
                                NewSms(
                                    SmsMessage.displayOriginatingAddress,
                                    message
                                )
                            )
                        }
                    }, 4000)
                }
            }
        }
    }

    fun isForeground(context: Context?, myPackage: String): Boolean {
        val manager = context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        val runningTaskInfo = manager!!.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return componentInfo!!.packageName == myPackage
    }
}
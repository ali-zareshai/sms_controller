package com.bita.smscontrol.receiver

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Handler
import android.provider.Telephony
import androidx.core.app.NotificationCompat
import com.bita.smscontrol.R
import com.bita.smscontrol.Utility.SaveItem
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
//                    getNotification(context!!)
                    counter(context = context!!)
                    val i = Intent(context, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(i)

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

    private fun counter(context: Context) {
        val old:Int =SaveItem.getItem(context, SaveItem.COUNT_UNREAD_SMS, "0").toInt()
        SaveItem.setItem(context, SaveItem.COUNT_UNREAD_SMS, old.plus(1).toString())
    }

    private fun isForeground(context: Context?, myPackage: String): Boolean {
        try {
            val manager = context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
            val runningTaskInfo = manager!!.getRunningTasks(1)
            val componentInfo = runningTaskInfo[0].topActivity
            return componentInfo!!.packageName == myPackage
        }catch (e: Exception){
            return false
        }
    }

    private fun getNotification(context: Context) {
        val contentIntent = PendingIntent.getActivity(context, 0,
                Intent(context, MainActivity::class.java), 0)

        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.new_sms))
                .setContentText("Hello World!")
        mBuilder.setContentIntent(contentIntent)
        mBuilder.setDefaults(Notification.DEFAULT_SOUND)
        mBuilder.setAutoCancel(true)
        val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(13456, mBuilder.build())
    }

}
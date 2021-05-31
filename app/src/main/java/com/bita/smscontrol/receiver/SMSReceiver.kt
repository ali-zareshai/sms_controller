package com.bita.smscontrol.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
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
                    counter(context = context!!)
                    createNotification(context = context,aMessage = context.getString(R.string.new_sms))
                    SaveItem.setItem(context,SaveItem.LAST_SMS,message)
                    EventBus.getDefault().post(
                            NewSms(
                                    SmsMessage.displayOriginatingAddress,
                                    message
                            )
                    )

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

    private var notifManager: NotificationManager? = null
    private fun createNotification(aMessage: String?, context: Context) {
        val NOTIFY_ID = 1235
        val id ="136810"
        val title = "channel_01"
        val intent: Intent
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder
        if (notifManager == null) {
            notifManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notifManager!!.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, title, importance)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notifManager!!.createNotificationChannel(mChannel)
            }
            builder = NotificationCompat.Builder(context, id)
            intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(aMessage)
                    .setSmallIcon(R.drawable.ic_baseline_sms_24)
                    .setContentText(context.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {
            builder = NotificationCompat.Builder(context, id)
            intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(aMessage) // required
                    .setSmallIcon(R.drawable.ic_baseline_sms_24) // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority = Notification.PRIORITY_HIGH
        }
        val notification = builder.build()
        notifManager!!.notify(NOTIFY_ID, notification)
    }

}
package com.bita.smscontrol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
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
            if (message.startsWith("on_time",true)){
                EventBus.getDefault().post(NewSms(SmsMessage.displayOriginatingAddress,message))
            }
        }
    }
}
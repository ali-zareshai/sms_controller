package com.bita.smscontrol.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bita.smscontrol.R

class MainActivity : AppCompatActivity() {
    var  smsManager:SmsManager?=null
    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smsManager = SmsManager.getDefault() as SmsManager
        requestSmsPermission(Manifest.permission.RECEIVE_SMS)
        requestSmsPermission(Manifest.permission.SEND_SMS)
    }

    private fun requestSmsPermission(permission:String) {
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_SMS_PERMISSION)
        }
    }

    private fun sendSMS(phoneNumber:String,message:String){
        smsManager?.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
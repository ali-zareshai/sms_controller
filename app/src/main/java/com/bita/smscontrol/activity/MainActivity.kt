package com.bita.smscontrol.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bita.smscontrol.R
import com.bita.smscontrol.Utility.SaveItem
import com.bita.smscontrol.event.NewSms
import com.suke.widget.SwitchButton
import com.valdesekamdem.library.mdtoast.MDToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


class MainActivity : AppCompatActivity(),View.OnClickListener {
    var  smsManager:SmsManager?=null

    var devicePhoneNumberEdit:EditText?=null;
    var offSwitche:SwitchButton?=null
    var timerSwitch:SwitchButton?=null
    var onTimeEdit:EditText?=null
    var offTimeEdit:EditText?=null
    var delayConEdit:EditText?=null
    var delayDisEdit:EditText?=null
    var maxAmpherEdit:EditText?=null
    var minAmpherEdit:EditText?=null
    var onTimeAmpherEdit:EditText?=null
    var phoneNumberEdit:EditText?=null

    var sendBtn:Button?=null
    var reportBtn:Button?=null

    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smsManager = SmsManager.getDefault() as SmsManager
        requestSmsPermission(Manifest.permission.RECEIVE_SMS)
        requestSmsPermission(Manifest.permission.SEND_SMS)

        devicePhoneNumberEdit =findViewById(R.id.edit_device_phone)
        offSwitche =findViewById(R.id.switch_off_button)
        timerSwitch =findViewById(R.id.switch_timer_button)
        onTimeEdit =findViewById(R.id.edit_on_time)
        offTimeEdit =findViewById(R.id.edit_off_time)
        delayConEdit =findViewById(R.id.edit_delay_connect)
        delayDisEdit =findViewById(R.id.edit_disconnect_delay)
        maxAmpherEdit =findViewById(R.id.edit_max_ampher)
        minAmpherEdit =findViewById(R.id.edit_min_ampher)
        onTimeAmpherEdit =findViewById(R.id.edit_ontime_ampher)
        phoneNumberEdit =findViewById(R.id.edit_phone_number)

        sendBtn =findViewById(R.id.btn_send)
        reportBtn =findViewById(R.id.btn_report)
        
        sendBtn?.setOnClickListener(this)
        reportBtn?.setOnClickListener(this)

        //set default device phone number
        devicePhoneNumberEdit?.setText(SaveItem.getItem(this,SaveItem.DEVICE_PHONE,""))
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe
    public fun onEvent(sms: NewSms){
        val parameters: MutableMap<String, String> = mutableMapOf<String, String>()
        try{
            for(parts in sms.sms.split(",")){
                val part =parts.split("=")
                parameters.put(part.get(0),part.get(1))
            }
            setResultSms(parameters)
        }catch (e:Exception){
            Log.e("exception:",e.message)
        }

    }

    private fun setResultSms(parameters: MutableMap<String, String>) {
        onTimeEdit?.setText(parameters["on_time"])
        offTimeEdit?.setText(parameters["off_time"])
        delayConEdit?.setText(parameters["delay_start"])
        delayDisEdit?.setText(parameters["off_delay"])
        maxAmpherEdit?.setText(parameters["over_amp"])
        minAmpherEdit?.setText(parameters["under_amp"])
        onTimeAmpherEdit?.setText(parameters["normal_amp"])
        ////////////////////////
        onTimeEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        offTimeEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        delayConEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        maxAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        minAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        onTimeAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun requestSmsPermission(permission: String) {
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_CODE_SMS_PERMISSION
            )
        }
    }

    private fun sendSMS(phoneNumber: String, message: String){
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.sending)
        pDialog.setCancelable(false)
        pDialog.show()
        smsManager?.sendTextMessage(phoneNumber, null, message, null, null)
        Handler().postDelayed(object:Runnable{
            override fun run() {
                pDialog.dismissWithAnimation()
            }
        },10000);

    }

    override fun onClick(p0: View?) {
        when(p0){
            sendBtn -> sendParameters()
            reportBtn -> getReport()
        }
    }

    private fun getReport() {
        val devicePhoneNumber =devicePhoneNumberEdit?.text.toString()
        if(devicePhoneNumber.isEmpty() || !Regex("09(1[0-9]|3[1-9]|2[1-9])-?[0-9]{3}-?[0-9]{4}").containsMatchIn(devicePhoneNumber)){
            devicePhoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            MDToast.makeText(this,getString(R.string.not_valid_phone_number),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show()
            return
        }
        sendSMS(devicePhoneNumber,"report")
    }

    private fun sendParameters() {
        if(!isFilledAllFields()){
            MDToast.makeText(
                this,
                getString(R.string.fill_all_fields),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            return
        }
        if(isValidAllFields()){
            val message:String ="*setall*${onTimeEdit?.text.toString()}*${offTimeEdit?.text.toString()}*${delayConEdit?.text.toString()}" +
                    "*${delayDisEdit?.text.toString()}*${maxAmpherEdit?.text.toString()}*${minAmpherEdit?.text.toString()}*${phoneNumberEdit?.text.toString()}*"

            showWaringDialog(devicePhoneNumberEdit?.text.toString(), message)
            SaveItem.setItem(this,SaveItem.DEVICE_PHONE,devicePhoneNumberEdit?.text.toString())
        }

    }

    private fun showWaringDialog(phoneNumber: String, message: String) {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.are_you_sure))
            .setContentText(getString(R.string.are_you_send_config))
            .setConfirmText(getString(R.string.yes_send))
            .setCancelText(getString(R.string.no))
            .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                override fun onClick(sweetAlertDialog: SweetAlertDialog?) {
                    sendSMS(phoneNumber, message)
                    sweetAlertDialog?.dismissWithAnimation()
                }

            })
            .show()
    }

    private fun isValidAllFields(): Boolean {
        val mobileRegex =Regex("09(1[0-9]|3[1-9]|2[1-9])-?[0-9]{3}-?[0-9]{4}")
        if(!mobileRegex.containsMatchIn(devicePhoneNumberEdit?.text.toString())){
            MDToast.makeText(
                this,
                getString(R.string.not_valid_phone_number),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            devicePhoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false;
        }else{
            devicePhoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(delayConEdit?.text.toString().toInt()>240){
            delayConEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            MDToast.makeText(
                this,
                getString(R.string.low_from_240),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            return false
        }else{
            delayConEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(delayDisEdit?.text.toString().toInt()>15){
            delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            MDToast.makeText(
                this,
                getString(R.string.low_from_240),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            return false
        }else{
            delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(!mobileRegex.containsMatchIn(phoneNumberEdit?.text.toString())){
            MDToast.makeText(
                this,
                getString(R.string.not_valid_phone_number),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            phoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false;
        }else{
            phoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        return true
    }

    private fun isFilledAllFields(): Boolean {
        if(devicePhoneNumberEdit?.text.toString().isNullOrEmpty()){
            devicePhoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            devicePhoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(onTimeEdit?.text.toString().isNullOrEmpty()){
            onTimeEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            onTimeEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(offTimeEdit?.text.toString().isNullOrEmpty()){
            offTimeEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            offTimeEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(delayConEdit?.text.toString().isNullOrEmpty()){
            delayConEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            delayConEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(delayDisEdit?.text.toString().isNullOrEmpty()){
            delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(minAmpherEdit?.text.toString().isNullOrEmpty()){
            minAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            minAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(maxAmpherEdit?.text.toString().isNullOrEmpty()){
            maxAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            maxAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(phoneNumberEdit?.text.toString().isNullOrEmpty()){
            phoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            phoneNumberEdit?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        return true
    }
}
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bita.smscontrol.R
import com.bita.smscontrol.Utility.SaveItem
import com.bita.smscontrol.Utility.TimerTextView
import com.bita.smscontrol.event.NewSms
import com.valdesekamdem.library.mdtoast.MDToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.honorato.multistatetogglebutton.MultiStateToggleButton
import org.honorato.multistatetogglebutton.ToggleButton
import java.lang.Exception


class MainActivity : AppCompatActivity(),View.OnClickListener {
    var  smsManager:SmsManager?=null

    var devicePhoneNumberEdit:EditText?=null;
    var offSwitche: MultiStateToggleButton?=null
    var onTimeEdit:EditText?=null
    var offTimeEdit:EditText?=null
    var delayConEdit:EditText?=null
    var delayDisEdit:EditText?=null
    var maxAmpherEdit:EditText?=null
    var minAmpherEdit:EditText?=null
    var onTimeAmpherEdit:EditText?=null
    var phoneNumberEdit:EditText?=null
    var statusTv:TextView?=null
    var phoneDeviceBorder:LinearLayout?=null
    var phoneOperatorBorder:LinearLayout?=null
    var hasNewSms:LinearLayout?=null
    var countNewSms:TextView?=null
    var timer:TimerTextView?=null

    var sendBtn:Button?=null
    var reportBtn:Button?=null

    var timerHander:Handler?=null
    var enableSwitchs:Boolean=true


    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        smsManager = SmsManager.getDefault() as SmsManager
        requestSmsPermission()

        devicePhoneNumberEdit =findViewById(R.id.edit_device_phone)
        offSwitche =findViewById(R.id.switch_off_button)
        onTimeEdit =findViewById(R.id.edit_on_time)
        offTimeEdit =findViewById(R.id.edit_off_time)
        delayConEdit =findViewById(R.id.edit_delay_connect)
        delayDisEdit =findViewById(R.id.edit_disconnect_delay)
        maxAmpherEdit =findViewById(R.id.edit_max_ampher)
        minAmpherEdit =findViewById(R.id.edit_min_ampher)
        onTimeAmpherEdit =findViewById(R.id.edit_ontime_ampher)
        phoneNumberEdit =findViewById(R.id.edit_phone_number)
        statusTv =findViewById(R.id.status_device_tv)
        phoneDeviceBorder= findViewById(R.id.edit_device_phone_border)
        phoneOperatorBorder= findViewById(R.id.edit_phone_number_lin)
        hasNewSms =findViewById(R.id.has_new_sms)
        countNewSms =findViewById(R.id.count_new_sms_tv)
        timer =findViewById(R.id.timerText)

        sendBtn =findViewById(R.id.btn_send)
        reportBtn =findViewById(R.id.btn_report)

        setCountNewSMS();

        offSwitche?.setOnValueChangedListener(object: ToggleButton.OnValueChangedListener{
            override fun onValueChanged(value: Int) {
                if(enableSwitchs){
                    clearFocus()
                    when(value) {
                        0 -> sendCommand("on_timer")
                        1 -> sendCommand("off")
                        2 -> sendCommand("on_manually")
                    }
                }

            }

        })
        sendBtn?.setOnClickListener(this)
        reportBtn?.setOnClickListener(this)

        //set default device phone number
        devicePhoneNumberEdit?.setText(SaveItem.getItem(this,SaveItem.DEVICE_PHONE,""))
        phoneNumberEdit?.setText(SaveItem.getItem(this,SaveItem.OPERATOR_PHONE,""))
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private fun setCountNewSMS() {
        val newSms =SaveItem.getItem(this,SaveItem.COUNT_UNREAD_SMS,"0").toInt()
        if (newSms>0){
            hasNewSms?.visibility =View.VISIBLE
            countNewSms?.text ="${newSms}"
            SaveItem.setItem(this,SaveItem.COUNT_UNREAD_SMS,"0")
        }
    }

    private fun startTimer(){
        sendBtn?.isEnabled =false
        reportBtn?.isEnabled=false
        offSwitche?.isEnabled =false
        timer?.setEndTime(System.currentTimeMillis() + (90 * 1000))
        timer?.visibility =View.VISIBLE
        timerHander=Handler()
        timerHander?.postDelayed(object:Runnable{
            override fun run() {
                stopTimer()
            }

        },90 * 1000)
    }

    private fun stopTimer(){
        sendBtn?.isEnabled =true
        reportBtn?.isEnabled=true
        offSwitche?.isEnabled =true
        timer?.visibility =View.GONE
        timer?.stopTimer()
        if(timerHander!=null){
            timerHander?.removeCallbacksAndMessages(null)
        }

    }

    @Subscribe
    public fun onEvent(sms: NewSms){
        val parameters: MutableMap<String, String> = mutableMapOf<String, String>()
        try{
            for(parts in sms.sms.split(",")){
                val part =parts.split("=")
                parameters.put(part.get(0),part.get(1))
            }
            enableSwitchs=false
            setResultSms(parameters)
        }catch (e:Exception){
            Log.e("exception:",e.message)
        }

    }

    private fun setResultSms(parameters: MutableMap<String, String>) {
        setStatusName(parameters["status_trip"])
        onTimeEdit?.setText(parameters["on_time"])
        offTimeEdit?.setText(parameters["off_time"])
        delayConEdit?.setText(parameters["delay_start"])
        delayDisEdit?.setText(parameters["off_delay"])
        maxAmpherEdit?.setText(parameters["over_amp"])
        minAmpherEdit?.setText(parameters["under_amp"])
        onTimeAmpherEdit?.setText(parameters["normal_amp"])
        ////////////////////////
        clearFocus()
        ////////////////////////
        onTimeEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        offTimeEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        delayConEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        maxAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        minAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        onTimeAmpherEdit?.setBackgroundResource(R.drawable.border_edittext_success)
        //////
        stopTimer()
        enableSwitchs=true
    }

    private fun clearFocus(){
        onTimeEdit?.clearFocus()
        offTimeEdit?.clearFocus()
        delayConEdit?.clearFocus()
        delayDisEdit?.clearFocus()
        maxAmpherEdit?.clearFocus()
        minAmpherEdit?.clearFocus()
        onTimeAmpherEdit?.clearFocus()
    }

    private fun setStatusName(code: String?) {
        when(code?.toInt()){
            0->{
                statusTv?.text =getString(R.string.off)
                offSwitche?.value=1
            }
            1->{
                statusTv?.text =getString(R.string.on_by_timer)
                offSwitche?.value=0
            }
            2->{
                statusTv?.text =getString(R.string.manual_by_timer)
                offSwitche?.value=2
            }
            3->{
                statusTv?.text =getString(R.string.over_amp)
                offSwitche?.value=1
            }
            4->{
                statusTv?.text =getString(R.string.under_amp)
                offSwitche?.value=1
            }
            5->{
                statusTv?.text =getString(R.string.digital_disconnect)
                offSwitche?.value=1
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun requestSmsPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
            return
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS ,Manifest.permission.SEND_SMS), REQUEST_CODE_SMS_PERMISSION)
    }

    private fun sendSMS(phoneNumber: String, message: String){
        try{
            smsManager?.sendTextMessage(phoneNumber, null, message, null, null)
        }catch (e:Exception){
            MDToast.makeText(this,getString(R.string.reset_app),MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
            finish()
        }
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = getString(R.string.sending)
        pDialog.setCancelable(false)
        pDialog.show()

        Handler().postDelayed(object:Runnable{
            override fun run() {
                pDialog.dismissWithAnimation()
            }
        },10000)

    }

    override fun onClick(p0: View?) {
        when(p0){
            sendBtn -> sendParameters()
            reportBtn -> sendCommand("reporte")
        }
    }

    private fun sendCommand(cmd:String) {
        val devicePhoneNumber =devicePhoneNumberEdit?.text.toString()
        if(devicePhoneNumber.isEmpty() || !Regex("09[0-9]{9}").containsMatchIn(devicePhoneNumber)){
            phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_error)
            MDToast.makeText(this,getString(R.string.not_valid_phone_number),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show()
            return
        }
        phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_normal)
        if(cmd=="reporte"){
            sendSMS(devicePhoneNumber,"reporte")
            startTimer()
        }else{
            showWaringDialog(devicePhoneNumber,"*set*${cmd}*")
        }

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
            val message:String ="setall*${onTimeEdit?.text.toString()}*${offTimeEdit?.text.toString()}*${delayConEdit?.text.toString()}" +
                    "*${delayDisEdit?.text.toString()}*${maxAmpherEdit?.text.toString()}*${minAmpherEdit?.text.toString()}*${phoneNumberEdit?.text.toString()}"

            showWaringDialog(devicePhoneNumberEdit?.text.toString(), message)
            SaveItem.setItem(this,SaveItem.DEVICE_PHONE,devicePhoneNumberEdit?.text.toString())
            SaveItem.setItem(this,SaveItem.OPERATOR_PHONE,phoneNumberEdit?.text.toString())

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
                    startTimer()
                }

            })
            .show()
    }

    private fun isValidAllFields(): Boolean {
        val mobileRegex =Regex("09[0-9]{9}")
        if(!mobileRegex.containsMatchIn(devicePhoneNumberEdit?.text.toString())){
            MDToast.makeText(
                this,
                getString(R.string.not_valid_phone_number),
                MDToast.LENGTH_LONG,
                MDToast.TYPE_ERROR
            ).show()
            phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_error)
            return false;
        }else{
            phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        if(delayConEdit?.text.toString().trim().toInt()>240){
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

        if(delayDisEdit?.text.toString().trim().toInt()>15){
            delayDisEdit?.setBackgroundResource(R.drawable.border_edittext_error)
            MDToast.makeText(
                this,
                getString(R.string.low_from_15),
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
            phoneOperatorBorder?.setBackgroundResource(R.drawable.border_edittext_error)
            return false;
        }else{
            phoneOperatorBorder?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        return true
    }

    private fun isFilledAllFields(): Boolean {
        if(devicePhoneNumberEdit?.text.toString().isNullOrEmpty()){
            phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            phoneDeviceBorder?.setBackgroundResource(R.drawable.border_edittext_normal)
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
            phoneOperatorBorder?.setBackgroundResource(R.drawable.border_edittext_error)
            return false
        }else{
            phoneOperatorBorder?.setBackgroundResource(R.drawable.border_edittext_normal)
        }

        return true
    }
}
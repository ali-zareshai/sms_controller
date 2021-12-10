package com.bita.smscontrol.Utility

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioGroup
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bita.smscontrol.R
import com.bita.smscontrol.Utility.SaveItem
import com.valdesekamdem.library.mdtoast.MDToast

class DevicesDialog(context: Context,val newPhoneNumbers: phoneNumbers) : Dialog(
    context
), View.OnClickListener {
    private var yesBtn: Button? = null
    private var noBtn: Button? = null
    private var nameDevice1: EditText? = null
    private var nameDevice2: EditText? = null
    private var nameDevice3: EditText? = null
    private var telDevice1: EditText? = null
    private var telDevice2: EditText? = null
    private var telDevice3: EditText? = null

    private var radioGroup:RadioGroup?=null
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_devices)
        yesBtn = findViewById<View>(R.id.btn_yes) as Button
        noBtn = findViewById<View>(R.id.btn_no) as Button
        nameDevice1 = findViewById<View>(R.id.device_name_1) as EditText
        nameDevice2 = findViewById<View>(R.id.device_name_2) as EditText
        nameDevice3 = findViewById<View>(R.id.device_name_3) as EditText
        telDevice1 = findViewById<View>(R.id.device_tel_1) as EditText
        telDevice2 = findViewById<View>(R.id.device_tel_2) as EditText
        telDevice3 = findViewById<View>(R.id.device_tel_3) as EditText

        radioGroup =findViewById<View>(R.id.radioGroup_device) as RadioGroup
        yesBtn!!.setOnClickListener(this)
        noBtn!!.setOnClickListener(this)
        setDefaultValues()
    }

    private fun setDefaultValues() {
        if (!SaveItem.getItem(context, SaveItem.DEVICE_NAME_1, "")
                .equals("", ignoreCase = true)
        ) {
            nameDevice1!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_NAME_1, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.DEVICE_NAME_2, "")
                .equals("", ignoreCase = true)
        ) {
            nameDevice2!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_NAME_2, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.DEVICE_NAME_3, "")
                .equals("", ignoreCase = true)
        ) {
            nameDevice3!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_NAME_3, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.DEVICE_TEL_1, "").equals("", ignoreCase = true)) {
            telDevice1!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_TEL_1, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.DEVICE_TEL_2, "").equals("", ignoreCase = true)) {
            telDevice2!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_TEL_2, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.DEVICE_TEL_3, "").equals("", ignoreCase = true)) {
            telDevice3!!.setText(SaveItem.getItem(context, SaveItem.DEVICE_TEL_3, ""))
        }
    }

    override fun onClick(view: View) {
        when (view) {
            yesBtn -> setNewValues()
            noBtn -> dismiss()
        }
    }

    private fun setNewValues() {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(context.getString(R.string.are_you_sure))
            .setContentText(context.getString(R.string.are_you_send_config))
            .setConfirmText(context.getString(R.string.yes_send))
            .setCancelText(context.getString(R.string.no))
            .setConfirmClickListener(object : SweetAlertDialog.OnSweetClickListener {
                override fun onClick(sweetAlertDialog: SweetAlertDialog?) {
                    val selectId =radioGroup?.checkedRadioButtonId
                    if (selectId!=-1){
                        MDToast.makeText(context,context.getString(R.string.select_1_device),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show()
                        return
                    }
                    setValues()
                    val radioBtn =findViewById<View>(selectId)
                    when(radioBtn.tag.toString().toInt()){
                        1->setDefaultDevice(telDevice1!!)
                        2->setDefaultDevice(telDevice2!!)
                        3->setDefaultDevice(telDevice3!!)
                    }


                    newPhoneNumbers.phoneNumber(
                        telDevice1?.text.toString(),
                        telDevice2?.text.toString(),
                        telDevice3?.text.toString()
                    )

                    sweetAlertDialog?.dismissWithAnimation()
                }
            })
            .show()
    }

    private fun setDefaultDevice(editText:EditText){
        if (!editText.text.isNullOrBlank() && Regex("09[0-9]{9}").containsMatchIn(editText.text.toString())){
            SaveItem.setItem(context,SaveItem.DEVICE_PHONE,editText.text?.toString())
        }
    }

    private fun setValues() {
        if(!(nameDevice1?.text.toString().equals("") || telDevice1?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.DEVICE_NAME_1,nameDevice1?.text.toString())
            SaveItem.setItem(context,SaveItem.DEVICE_TEL_1,telDevice1?.text?.toString())
        }
        if(!(nameDevice2?.text.toString().equals("") || telDevice2?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.DEVICE_NAME_2,nameDevice2?.text.toString())
            SaveItem.setItem(context,SaveItem.DEVICE_TEL_2,telDevice2?.text.toString())
        }
        if(!(nameDevice3?.text.toString().equals("") || telDevice3?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.DEVICE_NAME_3,nameDevice3?.text.toString())
            SaveItem.setItem(context,SaveItem.DEVICE_TEL_3,telDevice3?.text.toString())
        }
    }

    interface phoneNumbers{
        fun phoneNumber(number1:String ,number2:String,number3:String)
    }
}
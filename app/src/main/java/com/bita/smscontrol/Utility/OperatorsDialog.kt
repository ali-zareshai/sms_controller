package com.bita.smscontrol.Utility

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bita.smscontrol.R
import com.bita.smscontrol.Utility.SaveItem

class OperatorsDialog(context: Context,val newPhoneNumbers: phoneNumbers) : Dialog(
    context
), View.OnClickListener {
    private var yesBtn: Button? = null
    private var noBtn: Button? = null
    private var nameOperator1: EditText? = null
    private var nameOperator2: EditText? = null
    private var nameOperator3: EditText? = null
    private var telOperator1: EditText? = null
    private var telOperator2: EditText? = null
    private var telOperator3: EditText? = null
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_operators)
        yesBtn = findViewById<View>(R.id.btn_yes) as Button
        noBtn = findViewById<View>(R.id.btn_no) as Button
        nameOperator1 = findViewById<View>(R.id.operator_name_1) as EditText
        nameOperator2 = findViewById<View>(R.id.operator_name_2) as EditText
        nameOperator3 = findViewById<View>(R.id.operator_name_3) as EditText
        telOperator1 = findViewById<View>(R.id.operator_tel_1) as EditText
        telOperator2 = findViewById<View>(R.id.operator_tel_2) as EditText
        telOperator3 = findViewById<View>(R.id.operator_tel_3) as EditText
        yesBtn!!.setOnClickListener(this)
        noBtn!!.setOnClickListener(this)
        setDefaultValues()
    }

    private fun setDefaultValues() {
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_NAME_1, "")
                .equals("", ignoreCase = true)
        ) {
            nameOperator1!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_NAME_1, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_NAME_2, "")
                .equals("", ignoreCase = true)
        ) {
            nameOperator2!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_NAME_2, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_NAME_3, "")
                .equals("", ignoreCase = true)
        ) {
            nameOperator3!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_NAME_3, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_TEL_1, "").equals("", ignoreCase = true)) {
            telOperator1!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_TEL_1, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_TEL_2, "").equals("", ignoreCase = true)) {
            telOperator2!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_TEL_2, ""))
        }
        if (!SaveItem.getItem(context, SaveItem.OPERATOR_TEL_3, "").equals("", ignoreCase = true)) {
            telOperator3!!.setText(SaveItem.getItem(context, SaveItem.OPERATOR_TEL_3, ""))
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
                    setValues()
                    newPhoneNumbers.phoneNumber(
                        telOperator1?.text.toString(),
                        telOperator2?.text.toString(),
                        telOperator3?.text.toString()
                    )
                    sweetAlertDialog?.dismissWithAnimation()
                }
            })
            .show()
    }

    private fun setValues() {
        if(!(nameOperator1?.text.toString().equals("") || telOperator1?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.OPERATOR_NAME_1,nameOperator1?.text.toString())
            SaveItem.setItem(context,SaveItem.OPERATOR_TEL_1,telOperator1?.text?.toString())
        }
        if(!(nameOperator2?.text.toString().equals("") || telOperator2?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.OPERATOR_NAME_2,nameOperator2?.text.toString())
            SaveItem.setItem(context,SaveItem.OPERATOR_TEL_2,telOperator2?.text.toString())
        }
        if(!(nameOperator3?.text.toString().equals("") || telOperator3?.text?.equals("")!!)){
            SaveItem.setItem(context,SaveItem.OPERATOR_NAME_3,nameOperator3?.text.toString())
            SaveItem.setItem(context,SaveItem.OPERATOR_TEL_3,telOperator3?.text.toString())
        }
    }

    interface phoneNumbers{
        fun phoneNumber(number1:String ,number2:String,number3:String)
    }
}
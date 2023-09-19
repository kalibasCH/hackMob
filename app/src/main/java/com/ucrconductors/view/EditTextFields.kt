package com.ucrconductors.view

import android.widget.EditText

object EditTextFields {

    fun isFieldsFilling(vararg textEditText: EditText): Boolean {
        var isAllFieldsFilling = true
        textEditText.forEach {
            if (it.text.isBlank())
            isAllFieldsFilling = false
        }
        return isAllFieldsFilling
    }
}
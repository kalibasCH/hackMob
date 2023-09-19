package com.ucrconductors.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.ucrconductors.R

class UiSettings(private val activity: Activity) {

    fun changeUiWithStatusInternet(internetStatus: Boolean, imageStatusInternetConnection: ImageView, textStatusInternetConnection: TextView) {
        if (internetStatus) {
            imageStatusInternetConnection.setImageResource(R.drawable.status_internet_connection_is_in)
            textStatusInternetConnection.setText(R.string.status_internet_connection_is_in)
        } else {
            imageStatusInternetConnection.setImageResource(R.drawable.status_internet_connection_is_out)
            textStatusInternetConnection.setText(R.string.status_internet_connection_is_out)
        }
    }

    fun hideKeyWord(editText: EditText) {
        val keyWord = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyWord.hideSoftInputFromWindow(editText.windowToken, 0)
    }
    fun showProgressBar(progressBar: View) {
        progressBar.visibility = View.VISIBLE
    }
    fun hideProgressBar(progressBar: View) {
        progressBar.visibility = View.INVISIBLE
    }
    fun clearEditTextFields(vararg editTexts: EditText) {
        editTexts.forEach { it.text.clear() }
    }

    fun goneUiElements(vararg elements: View) {
        elements.forEach { it.visibility = View.GONE }
    }
}

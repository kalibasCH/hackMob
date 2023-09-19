package com.ucrconductors.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.ucrconductors.R
import com.ucrconductors.databinding.ActivityOpenSessionBinding
import com.ucrconductors.model.Repository
import com.ucrconductors.model.Transport
import com.ucrconductors.viewModel.ViewModelFactory
import com.ucrconductors.viewModel.OpenSessionViewModel

class OpenSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenSessionBinding

    private lateinit var textUserName: TextView
    private lateinit var editTextTransport: EditText
    private lateinit var editTextRout: EditText
    private lateinit var buttonStartSession: ImageButton

    private lateinit var editTextFields: EditTextFields

    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: OpenSessionViewModel by viewModels { viewModelFactory }

    val repository: Repository by lazy {
        Repository
    }
    /*private val regexForTransportId =
        "[АВЕКМНОРСТУХ]{1}\\d{3}[АВЕКМНОРСТУХ]{2}\\d{2,3}".toRegex()
    private val regexForTransportNum = "\\d{1,2}[А-Я]?".toRegex()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textUserName = binding.textUserName
        editTextTransport = binding.editTextTransport
        editTextRout = binding.editTextRout
        editTextTransport.isEnabled = true
        editTextRout.isEnabled = true
        editTextFields = EditTextFields

        viewModelFactory = ViewModelFactory(repository)

        buttonStartSession = binding.buttonStartSession

        viewModel.conductor.observe(this@OpenSessionActivity) { conductor ->
            if (conductor != null) {
                val formattedName =
                    getString(R.string.user_name_format, conductor.surname, conductor.name!!.first())
                textUserName.text = formattedName
            } else {
                startActivity(Intent(this@OpenSessionActivity, MainActivity::class.java))
            }
        }

        buttonStartSession.setOnClickListener {
            UiSettings(this).hideKeyWord(editTextRout)
            if (editTextFields.isFieldsFilling(
                    editTextRout,
                    editTextTransport
                )
            ) {
                val transportNum = editTextTransport.text.toString().uppercase()
                val transportNumRout = editTextRout.text.toString().uppercase()

                /*if (!regexForTransportId.matches(transportNum)) {
                    Toast.makeText(
                        this,
                        "Неверный формат ввода номера ТС",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (!regexForTransportNum.matches(transportNumRout)) {
                    Toast.makeText(
                        this,
                        "Неверный формат ввода маршрута ТС",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {*/
                    val transport = Transport(transportNum, transportNumRout)
                    viewModel.postTransportToServer(transport)

                    editTextTransport.isEnabled = false
                    editTextRout.isEnabled = false

                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container_main, MainScannersFragment.newInstance())
                        .commit()
                //}
            } else {
                Toast.makeText(this, "Для открытия сессии заполните данные!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack() // Вернитесь к предыдущему фрагменту,
            // если есть фрагменты в Back Stack.
        } else {
            viewModel.removeConductor()
            startActivity(Intent(this@OpenSessionActivity, MainActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val nfcScannerFragment =
            supportFragmentManager.findFragmentByTag("NFC_SCANNER_FRAGMENT") as? NfcScannerFragment
        nfcScannerFragment?.handleNfcIntent(intent)
    }

}
package com.ucrconductors.view

import NetworkChangeReceiver
import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ucrconductors.R
import com.ucrconductors.databinding.ActivityMainBinding
import com.ucrconductors.model.AppDatabase
import com.ucrconductors.model.NetworkChangeListener
import com.ucrconductors.model.NetworkService
import com.ucrconductors.model.Repository
import com.ucrconductors.model.UserDao
import com.ucrconductors.view.Animation.runVectorsAnim
import com.ucrconductors.viewModel.MainViewModel
import com.ucrconductors.viewModel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REGEX_FOR_CARD_NUM = "\\d{1,16}"
class MainActivity : AppCompatActivity(), NetworkChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var uiSettings: UiSettings

    private val networkChangeReceiver = NetworkChangeReceiver(this)

    private lateinit var imageStatusInternetConnection: ImageView
    private lateinit var textStatusInternetConnection: TextView

    private lateinit var vector1: ImageView
    private lateinit var vector2: ImageView
    private lateinit var vector3: ImageView
    private lateinit var vector4: ImageView

    private lateinit var textLoginInfo: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var textInputPassword: EditText
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText

    private lateinit var buttonLogin: ImageButton
    private lateinit var buttonLoginNfc: ImageButton

    private lateinit var editTextFields: EditTextFields

    private var isInternetConnection = false

    private lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy { ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java) }

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    private lateinit var networkService: NetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialiseComponents()
        runInitTasks()
        buttonLogin.setOnClickListener { login() }
        buttonLoginNfc.setOnClickListener { /* TODO() */ }

    }
    private fun initialiseComponents() {
        viewModelFactory = ViewModelFactory(Repository)
        editTextLogin = binding.editTextLogin
        editTextPassword = binding.editTextTextPassword
        editTextFields = EditTextFields

        buttonLogin = binding.buttonLogin
        buttonLoginNfc = binding.buttonFastLogin

        textInputPassword = binding.editTextTextPassword

        imageStatusInternetConnection = binding.imageStatusInternetConnection
        textStatusInternetConnection = binding.textStatusInternetConnection

        uiSettings = UiSettings(this@MainActivity)

        vector1 = binding.vector1
        vector2 = binding.vector2
        vector3 = binding.vector3
        vector4 = binding.vector4

        textLoginInfo = binding.textLoginInfo
        progressBar = binding.progressBar

        networkService = NetworkService()

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)

        viewModel.textLogin.observe(this) { textLogin ->
            editTextLogin.setText(textLogin)
        }
        viewModel.textPassword.observe(this) { textPassword ->
            editTextPassword.setText(textPassword)
        }

    }

    private fun runInitTasks() {
        lifecycleScope.launch {
            runVectorsAnim(vector1, vector2, vector3, vector4)
            withContext(Dispatchers.IO) {
                database = AppDatabase.getDatabase(this@MainActivity)
                userDao = database.userDao()
            }
            Toast.makeText(this@MainActivity, getString(R.string.initialized_db), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
        buttonLogin.setOnClickListener {
            login()
        }
        buttonLoginNfc.setOnClickListener {
            //TODO(nfcLogin())
        }

        viewModel.isConductorPresent().observe(this) { result ->
            Log.d("OBNOVA", result.toString())
            if (result == true) {
                uiSettings.hideProgressBar(progressBar)
                uiSettings.clearEditTextFields()
                goToIntent(Intent(this@MainActivity, OpenSessionActivity::class.java))
            } else {
                uiSettings.hideProgressBar(progressBar)
            }

        }
    }

    override fun onNetworkAvailable() {
        isInternetConnection = true
        uiSettings.changeUiWithStatusInternet(
            true,
            imageStatusInternetConnection,
            textStatusInternetConnection
        )
    }

    override fun onNetworkUnavailable() {
        isInternetConnection = false
        uiSettings.changeUiWithStatusInternet(
            false,
            imageStatusInternetConnection,
            textStatusInternetConnection
        )
    }

    private fun goToIntent(intent: Intent) {
        startActivity(intent)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), 15
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun login() {
        uiSettings.hideKeyWord(editTextPassword)
        if (isInternetConnection) {
            if (editTextFields.isFieldsFilling(editTextLogin, editTextPassword)) {
                val login: String = editTextLogin.text.toString()
                val password: String = editTextPassword.text.toString()
                viewModel.setLoginText(login)
                viewModel.setPasswordText(password)

                uiSettings.showProgressBar(progressBar)
                viewModel.checkConductorInDb(login, password)

                viewModel.isConductorPresent().observe(this) { result ->
                    if (result == true) {
                        uiSettings.hideProgressBar(progressBar)
                        uiSettings.clearEditTextFields()
                        goToIntent(Intent(this@MainActivity, OpenSessionActivity::class.java))
                    } else {
                        uiSettings.hideProgressBar(progressBar)
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.fill_graphs_error), Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
                .show()
        }
    }


    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onDestroy() {
        viewModel.textLogin.removeObservers(this)
        viewModel.textPassword.removeObservers(this)
        unregisterReceiver(networkChangeReceiver)
        super.onDestroy()
    }
}

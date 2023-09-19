package com.ucrconductors.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ucrconductors.R
import com.ucrconductors.viewModel.MainScannersFragmentViewModel
import com.ucrconductors.viewModel.QrScannerFragmentViewModel
import com.ucrconductors.viewModel.ViewModelFactory

class MainScannersFragment : Fragment() {

    private val repository by lazy { (requireActivity() as OpenSessionActivity).repository }
    private lateinit var viewModel: MainScannersFragmentViewModel

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var buttonExit: ImageButton

    private lateinit var textUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MainScannersFragmentViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_scanners_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomBar = view.findViewById(R.id.bottom_navigation_bar)
        buttonExit = view.findViewById(R.id.button_exit_session)
        textUserName = view.findViewById(R.id.text_user_name)

        viewModel.conductor.observe(requireActivity()) { conductor ->
            if (conductor != null) {
                val formattedName =
                    getString(R.string.user_name_format, conductor.surname, conductor.name!!.first())
                textUserName.text = formattedName
            }
        }

        buttonExit.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val currentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container_for_qr_and_nfc)
                    if (currentFragment is QrScannerFragment || currentFragment is NfcScannerFragment) {
                        parentFragmentManager.beginTransaction().remove(currentFragment).commit()
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.qr_scanner -> {
                    val targetFragment = QrScannerFragment.newInstance()
                    switchFragment(targetFragment)
                    Toast.makeText(
                        requireActivity(),
                        "Отсканируйте QR код камерой",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnItemSelectedListener true
                }

                R.id.nfc_scanner -> {
                    val targetFragment =
                        NfcScannerFragment.newInstance() // Вам потребуется создать этот фрагмент
                    switchFragment(targetFragment, tag = "NFC_SCANNER_FRAGMENT")
                    Toast.makeText(
                        requireActivity(),
                        "Приложите ЕКЖ к обратной стороне устройства",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(targetFragment: Fragment, tag: String = "") {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val currentFragment =
            fragmentManager.findFragmentById(R.id.fragment_container_for_qr_and_nfc)
        currentFragment?.let { fragmentTransaction.remove(it) }

        fragmentTransaction.add(R.id.fragment_container_for_qr_and_nfc, targetFragment, tag)
            .commit()
    }
    companion object {
        fun newInstance() = MainScannersFragment()
    }

}
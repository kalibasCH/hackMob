package com.ucrconductors.view

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ucrconductors.R
import com.ucrconductors.viewModel.NfcScannerFragmentViewModel
import com.ucrconductors.viewModel.QrScannerFragmentViewModel
import com.ucrconductors.viewModel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NfcScannerFragment : Fragment() {
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var complex_progress_nfc: View
    private lateinit var imageOk: ImageView

    private val repository by lazy { (requireActivity() as OpenSessionActivity).repository }
    private lateinit var viewModel: NfcScannerFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(NfcScannerFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nfc_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        complex_progress_nfc = view.findViewById(R.id.complex_progress_nfc)
        imageOk = view.findViewById(R.id.image_ok)

        Animation.rotateXmlAnimationNfcProgress(
            requireContext(),
            view.findViewById(R.id.progress_bar_nfc)
        )

        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val filters = arrayOf(tagDetected)
        val pendingIntent = PendingIntent.getActivity(
            requireActivity(),
            0,
            Intent(requireActivity(), javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        nfcAdapter?.enableForegroundDispatch(requireActivity(), pendingIntent, filters, null)
    }

    fun handleNfcIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                val ndefMessage = ndef.cachedNdefMessage
                val records = ndefMessage.records
                for (record in records) {
                    val payload = String(record.payload)
                    if (REGEX_FOR_CARD_NUM.toRegex().matches(payload)) {
                        viewModel.getUserByNumberOfCard(payload)
                    } else {
                        Toast.makeText(context, "Неверный формат данных", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = NfcScannerFragment()
    }
}
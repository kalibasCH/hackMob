package com.ucrconductors.view

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ucrconductors.R
import com.ucrconductors.viewModel.QrScannerFragmentViewModel
import com.ucrconductors.viewModel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class QrScannerFragment : Fragment(), ZBarScannerView.ResultHandler {

    private lateinit var zBarScannerView: ZBarScannerView

    private val repository by lazy { (requireActivity() as OpenSessionActivity).repository }
    private lateinit var viewModel: QrScannerFragmentViewModel

    private lateinit var imageOk: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(QrScannerFragmentViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)
        zBarScannerView = view.findViewById<View>(R.id.zbar_scanner_view) as ZBarScannerView
        zBarScannerView.setResultHandler(this)
        zBarScannerView.startCamera()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageOk = view.findViewById(R.id.image_ok)

        viewModel.user.observe(requireActivity()) {
            Toast.makeText(context, "Держатель карты: ${it.lastName}", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                imageOk.visibility = View.VISIBLE
                delay(2000)
                imageOk.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        zBarScannerView.resumeCameraPreview(this)
    }

    override fun onPause() {
        super.onPause()
        zBarScannerView.stopCameraPreview()
        zBarScannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        val scanResult = result?.contents
        if (REGEX_FOR_CARD_NUM.toRegex().matches(scanResult.toString())) {
            if (scanResult != null) {
                viewModel.getUserByNumberOfCard(scanResult.toString())
            } else {
                Toast.makeText(context, "Не удалось прочитать код", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Неверный формат данных", Toast.LENGTH_SHORT).show()
        }
        zBarScannerView.resumeCameraPreview(this)
    }

    companion object {
        fun newInstance(): QrScannerFragment {
            return QrScannerFragment()
        }
    }
}
package com.example.innogeeks_team_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.innogeeks_team_project.api.apiservice
import com.example.innogeeks_team_project.api.helper
import com.example.innogeeks_team_project.databinding.FragmentScanBinding
import com.example.innogeeks_team_project.models.itemDetails
import com.example.innogeeks_team_project.repository.itemRepo
import com.example.innogeeks_team_project.viewmodels.mainViewModel
import com.example.innogeeks_team_project.viewmodels.mainViewModelFactory
import com.google.mlkit.vision.barcode.common.Barcode

class scanFrag : Fragment() {
    lateinit var mainviewmodel: mainViewModel
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val camerapermission = android.Manifest.permission.CAMERA
    private val resultactivitylauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                scannerFragment.startScanner(this.requireContext()) { barcodes ->
                    barcodes.forEach { barcode ->
                        when (barcode.valueType) {
                            Barcode.TYPE_TEXT -> {

                            }

                            else -> {
                                Toast.makeText(this.context, "working", Toast.LENGTH_SHORT).show()
                                binding.resTV.text = barcode.rawValue.toString()
                            }
                        }
                    }

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.raita, scannerFragment())
        transaction.commit()

        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val service = helper.getInstance().create(apiservice::class.java)
        val repo = itemRepo(service)
        mainviewmodel =
            ViewModelProvider(this, mainViewModelFactory(repo)).get(mainViewModel::class.java)



        binding.btnhu.setOnClickListener {

            requestcameraandstartScanner()

        }
        mainviewmodel.selectItem("3017620422003")

        mainviewmodel.fooditem.observe(viewLifecycleOwner, Observer { task ->

            Log.d("idk", task.product.allergens)


        })


    }


    private fun requestcameraandstartScanner() {
        if (requireActivity().isPermissionGranted(camerapermission)) {
            scannerFragment.startScanner(this.requireContext()) { barcodes ->
                barcodes.forEach { barcode ->
                    when (barcode.valueType) {
                        Barcode.TYPE_TEXT -> {

                        }

                        else -> {
                            Toast.makeText(this.context, "working", Toast.LENGTH_SHORT).show()
                            binding.resTV.text = barcode.rawValue.toString()
                        }
                    }
                }
            }
        } else {
            requestCameraPermissionn()
        }

    }

    private fun requestCameraPermissionn() {
        if (shouldShowRequestPermissionRationale(camerapermission)) {
            requireActivity().cameraPermissionRequest {
                requireActivity().openPermissionSetting()
            }
        } else {
            resultactivitylauncher.launch(camerapermission)

        }
    }
}

